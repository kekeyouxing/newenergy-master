package newenergy.db.service;

import newenergy.db.domain.*;
import newenergy.db.repository.*;
import newenergy.db.template.FaultRecordPredicate;
import newenergy.db.template.PredicateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class FaultRecordService {
    @Autowired
    private FaultRecordRepository repository;
    @Autowired
    private ResidentRepository residentRepository;
    @Autowired
    private CorrAddressRepository corrAddressRepository;
    @Autowired
    private CorrPlotRepository corrPlotRepository;
    @Autowired
    private CorrPlotAdminRepository corrPlotAdminRepository;
    @Autowired
    private CorrTypeRepository corrTypeRepository;
    @Autowired
    private NewenergyAdminRepository newenergyAdminRepository;
    @Autowired
    private ResidentService residentService;
    /**
     * 默认质保期1年
     */
    public final Integer warranty = 1;

    public NewenergyAdmin getNewenergyAdmin(Integer id){
        return newenergyAdminRepository.findById(id).orElse(null);
    }

    public Resident getResident(String registerId){
        return residentRepository.findFirstByRegisterIdAndSafeDelete(registerId,0);
    }
    public CorrAddress getCorrAddress(String addressNum){
        return corrAddressRepository.findByAddressNumAndSafeDelete(addressNum,0);
    }
    public CorrPlotAdmin getCorrPlotAdmin(String plotNum){
        return corrPlotAdminRepository.findFirstByPlotNumAndSafeDelete(plotNum,0);
    }
    public CorrType getCorrType(String typeNum){
        return corrTypeRepository.findFirstByTypeNumAndSafeDelete(typeNum,0);
    }
    public CorrPlot getCorrPlot(String plotNum){
        return corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum,0);
    }
    public String getCorrAddressStr(CorrAddress corrAddress){
        return String.format("%s小区%d栋%d单元",
                getCorrPlot(corrAddress.getAddressPlot()).getPlotDtl(),
                corrAddress.getAddressBlock(),
                corrAddress.getAddressUnit());
    }
    /**
     * 默认不分页，排序按照id递增
     * @param specification 故障记录查询条件，可为null
     * @return
     */
    private Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification){
        return findBySpecificate(specification,null,null);
    }

    /**
     * 排序按照id递增
     * @param specification 故障记录查询条件，可为null
     * @param pageable 分页（从0开始），可为null
     * @return
     */
    private Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification, Pageable pageable){
        return findBySpecificate(specification,pageable,null);
    }

    /**
     *
     * @param specification 故障记录查询条件
     * @param pageable 分页（从0开始），可为null
     * @param sort 排序（默认按照id递增），可为null
     * @return
     */
    Page<FaultRecord> findBySpecificate(Specification<FaultRecord> specification, Pageable pageable, Sort sort){
        Sort newSort = null;
        Pageable newPageable = null;
        if(pageable == null){
            newSort = sort==null?Sort.by(Sort.Direction.ASC, "id"):sort;
            newPageable = PageRequest.of(0,(int)repository.count(),newSort);
        }else{
            if(sort != null){
                newPageable = PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),sort);
            }else{
                newPageable = pageable;
            }
        }
        return repository.findAll(specification,newPageable);
    }

    Specification<FaultRecord> addAlive(Specification<FaultRecord> other){
        Specification<FaultRecord> specification = (root,cq,cb)->
                cb.equal(root.get("safeDelete").as(Integer.class), PredicateFactory.getAlivePredicate().getSafeDelete());

        return other==null?specification:specification.and(other);
    }


    Specification<FaultRecord> addConditioin(FaultRecordPredicate predicate, Specification<FaultRecord> other){
        Specification<FaultRecord> specification = (root,cq,cb)->{
            List<Predicate> conditions = new ArrayList<>();
            if(predicate.getId() != null)
                conditions.add(cb.equal(root.get("id").as(Integer.class),predicate.getId()));
            if(predicate.getRegisterId() != null)
                conditions.add(cb.equal(root.get("registerId").as(String.class),predicate.getRegisterId()));
            if(predicate.getState() != null)
                conditions.add(cb.equal(root.get("state").as(Integer.class),predicate.getState()));
            if(predicate.getMonitorId() != null)
                conditions.add(cb.equal(root.get("monitorId").as(Integer.class),predicate.getMonitorId()));
            if(predicate.getServicerId() != null)
                conditions.add(cb.equal(root.get("servicerId").as(Integer.class),predicate.getServicerId()));
            if(predicate.getUsername() != null){
                List<Resident> residents = residentRepository.findAllByUserNameAndSafeDelete(predicate.getUsername(),0);
                residents.forEach(resident -> {
                    conditions.add(cb.equal(root.get("registerId").as(String.class),resident.getRegisterId()));
                });
            }
            if(predicate.getPlots() != null){
                for(String plot : predicate.getPlots()){
                    List<Resident> residents = residentRepository.findAllByPlotNumAndSafeDelete(plot,0);
                    residents.forEach(resident -> {
                        conditions.add(cb.equal(root.get("registerId").as(String.class),resident.getRegisterId()));
                    });
                }
            }
            Predicate[] arrConditions = new Predicate[conditions.size()];
            return cb.and(conditions.toArray(arrConditions));
        };
        return other==null?specification:specification.and(other);
    }

    public Page<FaultRecord> findByPredicate(FaultRecordPredicate predicate,Pageable pageable, Sort sort){
        Specification<FaultRecord> cond = addConditioin(predicate,null);
        return findBySpecificate(cond,pageable,sort);
    }
    /**
     *
     * @param userid 用户id
     * @return
     * 没有该用户 或 该用户没有 运营人员 或 故障领导 的权限 List 为null，
     * 有全部小区权限的返回 List 为空
     * 有部分小区权限的返回 List ，List里的小区编号为有权限的小区
     */
    public List<String> getPlotLimit(Integer userid){
        NewenergyAdmin user = newenergyAdminRepository.findById(userid).orElse(null);
        if(user == null) return null;
        List<String> ret = new ArrayList<>();
        boolean hasAccess = false;
        for(int i : user.getRoleIds()){
            /**
             * 9 故障领导
             * 6 审计人员
             * 1 admin
             *
             * 8 运营人员
             */
            if(i == 9 || i == 6 || i == 1)
                return ret;
            if(i == 8)
                hasAccess = true;
        }
        if(!hasAccess)
            return null;
        List<CorrPlotAdmin> plotAdmins =
                corrPlotAdminRepository.findAllByMonitorIdAndSafeDelete(userid,0);
        plotAdmins.forEach(plotAdmin->{
            ret.add(plotAdmin.getPlotNum());
        });

        return ret;
    }

    public Page<Resident> getResidentsByPlots(List<String> plots,Integer page, Integer limit, String registerId, String username){
        Specification<Resident> specification = null;
        for(String plot : plots){
            Resident resident = new Resident();
            resident.setPlotNum(plot);
            Specification<Resident> subCondition = residentService.findByPlotNumOrSearch(resident);
            if(specification != null){
                specification = specification.or(subCondition);
            }else{
                specification = subCondition;
            }
        }
        Resident resident = new Resident();
        resident.setRegisterId(registerId);
        resident.setUserName(username);
        Specification<Resident> searchCondition = residentService.findByPlotNumOrSearch(resident);
        specification = (specification==null?specification:specification.and(searchCondition));
        return residentRepository.findAll(specification,PageRequest.of(page,limit,Sort.by(Sort.Direction.ASC,"registerId")));
    }


    public FaultRecord addRecord(FaultRecord record){
        return repository.save(record);
    }
}
