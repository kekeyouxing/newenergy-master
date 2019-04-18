package newenergy.db.service;

import newenergy.db.constant.AdminConstant;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.*;
import newenergy.db.predicate.PredicateExecutor;
import newenergy.db.repository.*;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.template.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class FaultRecordService implements Searchable<FaultRecord,FaultRecordPredicate> {
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

    public FaultRecord addRecord(FaultRecord record){
        return repository.save(record);
    }

    @Override
    public Specification<FaultRecord> addConditioin(FaultRecordPredicate predicate, Specification<FaultRecord> other){
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

    @Override
    public JpaSpecificationExecutor<FaultRecord> getRepository() {
        return repository;
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
            if(i == AdminConstant.ROLE_FAULTLEADER
                    || i == AdminConstant.ROLE_AUDIT
                    || i == AdminConstant.ROLE_ADMIN)
                return ret;
            if(i == AdminConstant.ROLE_MONITOR)
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
    public NewenergyAdmin getNewenergyAdmin(Integer id){
        /**
         * TODO 待修改deleted
         */
        return newenergyAdminRepository.findFirstByIdAndDeleted(id, false);
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

}
