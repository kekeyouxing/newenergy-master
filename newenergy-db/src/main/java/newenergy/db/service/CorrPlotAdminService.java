package newenergy.db.service;

import newenergy.db.constant.AdminConstant;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.CorrPlotAdmin;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.CorrPlotAdminPredicate;
import newenergy.db.predicate.CorrPlotPredicate;
import newenergy.db.predicate.PredicateFactory;
import newenergy.db.repository.CorrPlotAdminRepository;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.template.LogicOperation;
import newenergy.db.template.Searchable;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
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
    @Autowired
    private NewenergyAdminService newenergyAdminService;
    @Autowired
    private CorrPlotService corrPlotService;
    @Autowired
    private FaultRecordService faultRecordService;

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

        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification(),Sort.by(Sort.Direction.ASC,"id"));
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
        List<NewenergyAdmin> admins = newenergyAdminRepository.findAll(PredicateFactory.getAliveSpecification(),Sort.by(Sort.Direction.ASC,"id"));
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
        /**
         * TODO 更新设备信息表？
         */
//        Integer id = corrPlotAdmin.getId();
//        if(id == null) return null;
//        CorrPlotAdmin origin = repository.findById(id).orElse(null);

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
        /**
         * TODO 更新设备信息表？
         */
    }

    @Override
    public Specification<CorrPlotAdmin> addConditioin(CorrPlotAdminPredicate predicate, Specification<CorrPlotAdmin> other) {
        Specification<CorrPlotAdmin> specification =
        (Root<CorrPlotAdmin> root, CriteriaQuery<?> cq, CriteriaBuilder cb)->{
            List<Predicate> list = new ArrayList<>();
            if(!StringUtilCorey.emptyCheck(predicate.getPlotNum())){
                list.add(cb.equal(root.get("plotNum").as(String.class),predicate.getPlotNum()));
            }
            if(!StringUtilCorey.emptyCheck(predicate.getPlotName())){
                CorrPlotPredicate corrPlotPredicate = new CorrPlotPredicate();
                corrPlotPredicate.setPlotDtl(predicate.getPlotName());
                List<CorrPlot> allRes = corrPlotService.findAllCorrPlotWithAlive(corrPlotPredicate);
                Path<Object> path = root.get("plotNum");
                CriteriaBuilder.In<Object> in = cb.in(path);
                allRes.forEach(e->in.value(e.getPlotNum()));
                list.add(cb.and(in));
            }
            if(!StringUtilCorey.emptyCheck(predicate.getMonitorName())){
                List<NewenergyAdmin> admins = newenergyAdminService.findAllByRealName(predicate.getMonitorName());
                Path<Object> path = root.get("monitorId");
                CriteriaBuilder.In<Object> in = cb.in(path);
                admins.forEach(admin-> in.value(admin.getId()));
                list.add(cb.and(in));
            }
            if(!StringUtilCorey.emptyCheck(predicate.getServicerName())){
                List<NewenergyAdmin> admins = newenergyAdminService.findAllByRealName(predicate.getServicerName());
                Path<Object> path = root.get("servicerId");
                CriteriaBuilder.In<Object> in = cb.in(path);
                admins.forEach(admin-> in.value(admin.getId()));
                list.add(cb.and(in));
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
