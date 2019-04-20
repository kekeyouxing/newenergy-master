package newenergy.db.service;

import newenergy.db.constant.DeviceRequireConstant;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.DeviceRequire;
import newenergy.db.global.DeviceRequireGlobal;
import newenergy.db.predicate.DeviceRequirePredicate;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.DeviceRequireRepository;
import newenergy.db.task.DeviceRequireRunnable;
import newenergy.db.task.ScheduledService;
import newenergy.db.template.LogicOperation;
import newenergy.db.template.Searchable;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by HUST Corey on 2019-03-29.
 */
@Service
public class DeviceRequireService extends LogicOperation<DeviceRequire>
                                    implements Searchable<DeviceRequire, DeviceRequirePredicate> {
    @Autowired
    private DeviceRequireRepository repository;

    @Autowired
    private CorrPlotRepository corrPlotRepository;

    @Autowired
    private ScheduledService scheduledService;

    private ScheduledFuture<?> future;
    /**
     *
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public DeviceRequire addDeviceRequire(DeviceRequire require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     *
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public DeviceRequire updateDeviceRequire(DeviceRequire require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     *
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteDeviceRequire(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

    public DeviceRequire addPlot(String plotNum,Integer userid){
        setHeader();
        DeviceRequire plotRequire = new DeviceRequire();
        plotRequire.setPlotNum(plotNum);
        return addDeviceRequire(plotRequire,userid);
    }
    public void deletePlot(String plotNum,Integer userid){
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotNum(plotNum);
        Page<DeviceRequire> res = findByPredicateWithAive(predicate,null,null);
        if(res.getTotalElements() != 1) return;
        DeviceRequire plot = res.get().findFirst().orElse(null);
        if(plot == null) return;
        deleteDeviceRequire(plot.getId(),userid);
    }
    private void setHeader(){
        DeviceRequire header = repository.findFirstByPlotNum("##");
        if(header == null){
            header = new DeviceRequire();
            header.setPlotNum(DeviceRequireConstant.SETTINGS);
            header.setUpdateLoop(DeviceRequireConstant.DEFAULT_LOOP);
            header.setSafeDelete(SafeConstant.SAFE_ALIVE);
            header.setSafeChangedTime(LocalDateTime.now());
            header.setSafeParent(null);
            header.setSafeChangedUserid(null);
            repository.saveAndFlush(header);
            setSetting(header,null);
        }

    }
    public String getPlotDtl(String plotNum){
        CorrPlot plot = corrPlotRepository.findFirstByPlotNumAndSafeDelete(plotNum,SafeConstant.SAFE_ALIVE);
        return  plot==null?null:plot.getPlotDtl();
    }
    public DeviceRequire getSetting(){
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotNum(DeviceRequireConstant.SETTINGS);
        return findOneByPredicateWithAive(predicate,null,null);
    }
    public DeviceRequire setSetting(DeviceRequire setting,Integer userid){
        if(setting == null) return null;
        Integer updateLoop = setting.getUpdateLoop();
        DeviceRequireGlobal.updateLoop.set(updateLoop);
        //修改定时任务
        updateCron();
        return updateDeviceRequire(setting,userid);
    }

    /**
     * 根据全局变量DeviceRequireGlobal.updateLoop开始或修改定时任务
     */
    public void updateCron(){
        DeviceRequireRunnable runnable = new DeviceRequireRunnable();
        /**
         * TODO
         * 每 updateLoop 秒 -> 分钟
         */
        String cron = scheduledService.getCronByRate(null,DeviceRequireGlobal.updateLoop.get(),null);
        System.out.println("changed to " + cron);
        if(future == null){
            future = scheduledService.startCron(runnable,cron);
        }else{
            future = scheduledService.changeCron(future,runnable,cron);
        }
    }
    public void setUpdateTime(LocalDateTime time){
        DeviceRequire setting = getSetting();
        if(setting == null) return;
        setting.setUpdateTime(time);
        repository.saveAndFlush(setting);
    }
    public void setRequire(String plotNum, BigDecimal volume){
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotNum(plotNum);
        DeviceRequire record = findOneByPredicateWithAive(predicate,null,null);
        record.setRequireVolume(volume);
        repository.saveAndFlush(record);
    }

    @Override
    public Specification<DeviceRequire> addConditioin(DeviceRequirePredicate predicate, Specification<DeviceRequire> other) {
        Specification<DeviceRequire> specification =
        (Root<DeviceRequire> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtilCorey.emptyCheck(predicate.getPlotDtl())){
                CorrPlot plot = corrPlotRepository.findByPlotDtlAndSafeDelete(predicate.getPlotDtl(),SafeConstant.SAFE_ALIVE);
                String plotNum = "";
                if(plot != null)
                    plotNum =  plot.getPlotNum();
                list.add(cb.equal(root.get("plotNum").as(String.class),plotNum));


            }
            if(!StringUtilCorey.emptyCheck(predicate.getPlotNum())){
                list.add(cb.equal(root.get("plotNum").as(String.class),predicate.getPlotNum()));
            }
            Predicate[] arr = new Predicate[list.size()];
            return cb.and(list.toArray(arr));
        };

        return other==null?specification:specification.and(other);
    }

    @Override
    public JpaSpecificationExecutor<DeviceRequire> getRepository() {
        return repository;
    }
}
