package newenergy.admin.background.service;

import newenergy.db.constant.DeviceRequireConstant;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.DeviceRequire;
import newenergy.db.global.DeviceRequireGlobal;
import newenergy.db.predicate.DeviceRequirePredicate;
import newenergy.db.repository.CorrPlotRepository;
import newenergy.db.repository.DeviceRequireRepository;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.CorrPlotService;
import newenergy.admin.background.task.DeviceRequireRunnable;
import newenergy.admin.background.task.ScheduledService;
import newenergy.db.template.LogicOperation;
import newenergy.db.template.Searchable;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

/**
 * Created by HUST Corey on 2019-03-29.
 */
@Service
public class DeviceRequireService extends LogicOperation<DeviceRequire>
                                    implements Searchable<DeviceRequire, DeviceRequirePredicate> {
    @Autowired
    private DeviceRequireRepository repository;

    @Autowired
    private CorrPlotService corrPlotService;

    @Autowired
    private CorrPlotRepository corrPlotRepository;

    @Autowired
    private ScheduledService scheduledService;

    @Autowired
    private ResidentRepository residentRepository;

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
    public List<String> findAllPlotNums(){
        List<String> result = new ArrayList<>();
        List<CorrPlot> corrPlots = corrPlotRepository.findAllBySafeDelete(SafeConstant.SAFE_ALIVE);
        corrPlots.forEach(e->result.add(e.getPlotNum()));
        return result;
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
        if(record != null){
            record.setRequireVolume(volume);
            repository.saveAndFlush(record);
        }
    }

    public BigDecimal getRequire(String plotNum){
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotNum(plotNum);
        DeviceRequire result = findOneByPredicateWithAive(predicate,null,null);
        if(result == null) return new BigDecimal(0);
        return result.getRequireVolume();
    }

    @Override
    public Specification<DeviceRequire> addConditioin(DeviceRequirePredicate predicate, Specification<DeviceRequire> other) {
        Specification<DeviceRequire> specification =
        (Root<DeviceRequire> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            List<Predicate> list = new ArrayList<>();
            if(!StringUtilCorey.emptyCheck(predicate.getPlotDtl())){
                List<CorrPlot> plots = corrPlotService.findAllByPlotDtl(predicate.getPlotDtl());
                Path<String> path = root.get("plotNum");
                CriteriaBuilder.In<String> in = cb.in(path);
                for (CorrPlot plot : plots){
                    String plotNum = plot.getPlotNum();
                    in.value(plotNum);
                }
                list.add(cb.and(in));
            }
            if(!StringUtilCorey.emptyCheck(predicate.getPlotNum())){
                list.add(cb.equal(root.get("plotNum").as(String.class),predicate.getPlotNum()));
            }

            if(predicate.getPlots() != null){
                Path<Object> path = root.get("plotNum");
                CriteriaBuilder.In<Object> in = cb.in(path);
                if(predicate.getPlots().isEmpty()){
                    predicate.setPlots(corrPlotRepository
                            .findAll()
                            .stream()
                            .map(CorrPlot::getPlotNum)
                            .collect(Collectors.toList()));
                }
                for(String plot : predicate.getPlots()){
                    if(StringUtilCorey.emptyCheck(plot)) continue;
                    in.value(plot);
                }
                list.add(cb.and(in));
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
