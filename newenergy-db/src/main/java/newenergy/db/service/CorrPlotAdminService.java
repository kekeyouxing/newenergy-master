package newenergy.db.service;

import newenergy.db.constant.AdminConstant;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.CorrPlotAdmin;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.CorrPlotAdminPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.repository.CorrPlotAdminRepository;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.template.Searchable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-11.
 */
@Service
public class CorrPlotAdminService extends LogicOperation<CorrPlotAdmin>
        implements Searchable<CorrPlotAdmin, CorrPlotAdminPredicate> {
    @Autowired
    private CorrPlotAdminRepository repository;
    @Autowired
    private CorrPlotRepository corrPlotRepository;
    @Autowired
    private NewenergyAdminRepository newenergyAdminRepository;

    public String getPlotdtl(String plotNum){
        if(plotNum == null) return null;
        CorrPlot corrPlot = corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum,0);
        return corrPlot==null?null:corrPlot.getPlotDtl();
    }
    public String getAdminName(Integer userid){
        if(userid == null) return null;
        NewenergyAdmin admin = newenergyAdminRepository
                .findById(userid)
                .orElse(null);
        return admin==null?null:admin.getRealName();
    }
    public List<Map<String,Object>> getMonitors(){
        /**
         * TODO
         * 待修改
         */
//        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification(),Sort.by(Sort.Direction.ASC,"id"));
        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification2(),Sort.by(Sort.Direction.ASC,"id"));
        System.out.println("monitor:"+admins.size());
        List<Map<String,Object>> ret = new ArrayList<>();
        admins.forEach(admin -> {
            if(isMonitor(admin)){
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("id",admin.getId());
                tmp.put("name",admin.getRealName());
                ret.add(tmp);
            }
        });
        return ret;
    }
    public List<Map<String,Object>> getServicers(){
//        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification(),Sort.by(Sort.Direction.ASC,"id"));
        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification2(),Sort.by(Sort.Direction.ASC,"id"));
        System.out.println("servicer:"+admins.size());
        List<Map<String,Object>> ret = new ArrayList<>();
        admins.forEach(admin -> {
            if(isServicer(admin)){
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("id",admin.getId());
                tmp.put("name",admin.getRealName());
                ret.add(tmp);
            }
        });
        return ret;
    }
    private boolean isMonitor(NewenergyAdmin admin){
        boolean result = false;
        for(Integer role : admin.getRoleIds()){
            if(role.equals(AdminConstant.ROLE_MONITOR) )
                result = true;
        }
        return result;
    }
    private boolean isServicer(NewenergyAdmin admin){
        boolean result = false;
        for(Integer role : admin.getRoleIds()){
            if(role.equals(AdminConstant.ROLE_SERVICER))
                result = true;
        }
        return result;
    }

    public CorrPlotAdmin addARecord(CorrPlot corrPlot, Integer userid){
        CorrPlotAdmin record = new CorrPlotAdmin();
        record.setPlotNum(corrPlot.getPlotNum());
        return super.addRecord(record,userid,repository);
    }
    public CorrPlotAdmin updateARecord(CorrPlotAdmin corrPlotAdmin, Integer userid){
        return super.updateRecord(corrPlotAdmin,userid,repository);
    }
    public void deleteARecord(String plotNum, Integer userid){
        CorrPlotAdminPredicate predicate = new CorrPlotAdminPredicate();
        if(plotNum == null) return;
        predicate.setPlotNum(plotNum);
        Page<CorrPlotAdmin> res = findByPredicateWithAive(predicate,null,null);
        res.forEach(e->{
            super.deleteRecord(e.getId(),userid,repository);
        });
    }

    @Override
    public Specification<CorrPlotAdmin> addConditioin(CorrPlotAdminPredicate predicate, Specification<CorrPlotAdmin> other) {
        Specification<CorrPlotAdmin> specification =
        (Root<CorrPlotAdmin> root, CriteriaQuery<?> cq, CriteriaBuilder cb)->{
            List<Predicate> list = new ArrayList<>();
            if(predicate.getPlotNum() != null){
                list.add(cb.equal(root.get("plotNum").as(String.class),predicate.getPlotNum()));
            }
            if(predicate.getPlotName() != null){
                CorrPlot corrPlot = corrPlotRepository.findByPlotDtlAndSafeDelete(predicate.getPlotName(),0);
                String plotNum = corrPlot.getPlotNum();
                list.add(cb.equal(root.get("plotNum").as(String.class),plotNum));
            }
            if(predicate.getMonitorName() != null){
                List<NewenergyAdmin> admins = newenergyAdminRepository.findAllByRealNameAndDeleted(predicate.getMonitorName(),false);
                /**
                 * TODO
                 * 待更改属性名
                 */
                //List<NewenergyAdmin> admins = newenergyAdminRepository.findAllByRealNameAndSafeDelete(predicate.getMonitorName(),0);
                admins.forEach(admin->{
                    list.add(cb.equal(root.get("monitorId").as(String.class),admin.getId()));
                });
            }
            if(predicate.getServicerName() != null){
                List<NewenergyAdmin> admins = newenergyAdminRepository.findAllByRealNameAndDeleted(predicate.getServicerName(),false);
                /**
                 * 待更改属性名
                 */
                //List<NewenergyAdmin> admins = newenergyAdminRepository.findAllByRealNameAndSafeDelete(predicate.getServicerName(),0);
                admins.forEach(admin->{
                    list.add(cb.equal(root.get("servicerId").as(String.class),admin.getId()));
                });

            }
            return cb.and(list.toArray(new Predicate[list.size()]));
        };
        return other==null?specification:specification.and(other);
    }

    @Override
    public JpaSpecificationExecutor<CorrPlotAdmin> getRepository() {
        return repository;
    }

}
