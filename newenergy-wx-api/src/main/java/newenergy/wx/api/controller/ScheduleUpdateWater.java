package newenergy.wx.api.controller;

import newenergy.admin.background.service.StorageService;
import newenergy.core.util.SpringUtil;
import newenergy.db.domain.*;
import newenergy.db.global.Parameters;
import newenergy.db.service.*;
import newenergy.db.util.StringUtilCorey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Configuration
@EnableScheduling
@EnableAsync
public class ScheduleUpdateWater {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RemainWaterService remainWaterService;

    @Autowired
    private ExtraWaterService extraWaterService;

    @Autowired
    private StatisticConsumeService consumeService;

    @Autowired
    private CorrPlotService corrPlotService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private StatisticPlotRechargeService plotRechargeService;

    @Autowired
    private StorageService storageService;

    private static ScheduleUpdateWater scheduleUpdateWater;

    @PostConstruct
    public void init() {
        scheduleUpdateWater = this;
        scheduleUpdateWater.plotRechargeService = this.plotRechargeService;
        scheduleUpdateWater.remainWaterService = this.remainWaterService;
        scheduleUpdateWater.corrPlotService = this.corrPlotService;
        scheduleUpdateWater.consumeService = this.consumeService;
        scheduleUpdateWater.extraWaterService = this.extraWaterService;
        scheduleUpdateWater.rechargeRecordService = this.rechargeRecordService;
        scheduleUpdateWater.residentService = this.residentService;
    }

    @Transactional
    @Async
    /**
     * TODO [TEST]每分钟生成一次
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void configureTasks(){
        List<ExtraWater> sortedExtraWaterList = scheduleUpdateWater.extraWaterService.findAll();
        for(ExtraWater extraWater : sortedExtraWaterList){
//            BigDecimal addVolume = extraWater.getAddVolume();
//            Integer addAmount = extraWater.getAddAmount();
            RechargeRecord rechargeRecord = scheduleUpdateWater.rechargeRecordService.findById(extraWater.getRecordId());
//            RemainWater remainWater = scheduleUpdateWater.remainWaterService.findByRegisterId(extraWater.getRegisterId());

//            updateVolume(rechargeRecord,remainWater,addVolume,addAmount);
            Resident resident = residentService.fingByRegisterId(extraWater.getRegisterId());
            String deviceNum = resident==null?"":resident.getDeviceNum();
            if(!StringUtilCorey.emptyCheck(deviceNum)) {
                storageService.addNotifyItem(deviceNum,rechargeRecord.getId());
            }
            storageService.addExtraWater(extraWater.getRegisterId(),extraWater.getAddVolume());
            scheduleUpdateWater.extraWaterService.deleteRecord(extraWater);

        }
    }


    private boolean isTrustworthy(RemainWater remainWater){
        if(remainWater==null || remainWater.getRemainVolume()==null || remainWater.getCurAmount()==null || remainWater.getCurFirstRemain() ==null
        || remainWater.getCurRecharge() == null) return false;
        LocalDateTime updateTime = remainWater.getUpdateTime();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(updateTime,now);
        long differMinutes = duration.toMinutes();
        if (differMinutes > Parameters.TRUSTDURATION){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * 更新剩余水量表,更改为updateVolume方法
     * 此处应该加上对日期的判断，比如是否为新的月份，后续添加
     */
//    @Transactional
//    public void updateReaminwater(RemainWater remainWater,BigDecimal addVolume){
//        remainWater.setRemainVolume(remainWater.getRemainVolume().add(addVolume));
//        remainWater.setUpdateTime(LocalDateTime.now());
//        remainWater.setCurRecharge(remainWater.getCurRecharge().add(addVolume));
//        remainWaterService.updateRemainWater(remainWater);
//    }

    /**
     * 更新水量表
     * @param rechargeRecord 更改充值记录表的剩余流量和更新流量
     * @param remainWater 更改剩余水量表的剩余流量和新增流量
     * @param addVolume 新增水量
     */
    @Transactional
    public void updateVolume(RechargeRecord rechargeRecord,RemainWater remainWater,BigDecimal addVolume, Integer addAmount){
        BigDecimal remainVolume = remainWater.getRemainVolume();
        BigDecimal updatedVolume = remainVolume.add(addVolume);
        rechargeRecord.setRemainVolume(remainVolume);
        rechargeRecord.setUpdatedVolume(updatedVolume);
        scheduleUpdateWater.rechargeRecordService.updateRechargeRecord(rechargeRecord,null);
        //剩余水量表与设备的数据同步，传来多少就更新为多少。无需直接定时更新数据库
//        remainWater.setRemainVolume(updatedVolume);
//        remainWater.setUpdateTime(LocalDateTime.now());
        remainWater.setCurRecharge(remainWater.getCurRecharge().add(addVolume));
        remainWater.setCurAmount(remainWater.getCurAmount()+addAmount);
        scheduleUpdateWater.remainWaterService.updateRemainWater(remainWater);
    }


    /**
     * 每月1号定时生成设备用水量月报表
     */
    @Transactional
    @Async
    /**
     * TODO [TEST]每5分钟生成一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
//    @Scheduled(cron = "0 0 0 1 1/1 ?")
    public void updateConsume() {
        List<RemainWater> remainWaters = scheduleUpdateWater.remainWaterService.findAll();
        for(RemainWater remainWater: remainWaters) {
            StatisticConsume consume = new StatisticConsume();
            consume.setRegisterId(remainWater.getRegisterId());
            consume.setUpdateTime(LocalDateTime.now());
            consume.setCurRecharge(remainWater.getCurRecharge());
            consume.setLastRemain(remainWater.getCurFirstRemain());
            consume.setCurRemain(remainWater.getRemainVolume());
            consume.setCurAmount(remainWater.getCurAmount());
            BigDecimal curUsed = remainWater.getCurRecharge().add(remainWater.getCurFirstRemain()).
                    subtract(remainWater.getRemainVolume());
            consume.setCurUsed(curUsed);
            scheduleUpdateWater.consumeService.addConsume(consume);
            remainWater.setCurRecharge(new BigDecimal(0));
            remainWater.setCurAmount(0);
            remainWater.setCurFirstRemain(remainWater.getRemainVolume());
//            remainWater.setUpdateTime(LocalDateTime.now());
            scheduleUpdateWater.remainWaterService.updateRemainWater(remainWater);
        }
    }

    /**
     * 每月更新小区用水量月报表
     */
    @Transactional
    @Async
    /**
     * TODO [TEST]每5分钟生成一次
     */
    @Scheduled(cron = "0 1/5 * * * ?")
    //@Scheduled(cron = "0 30 0 1 1/1 ?")
    public void updatePlotRecharge() {
        List<CorrPlot> plots = scheduleUpdateWater.corrPlotService.findAll();
        for(CorrPlot plot: plots) {
            StatisticPlotRecharge plotRecharge = new StatisticPlotRecharge();
            plotRecharge.setPlotNum(plot.getPlotNum());
            Integer amount = 0;
            BigDecimal plotFactor = plot.getPlotFactor();
            BigDecimal rechargeVolume = new BigDecimal(0);
            BigDecimal usedVolume = new BigDecimal(0);
            LocalDate curTime = LocalDate.now();
            List<Resident> residents = scheduleUpdateWater.residentService.findByPlotNum(plot.getPlotNum());
            for(Resident resident: residents) {
                StatisticConsume consume = scheduleUpdateWater.consumeService.findByRegisterIdAndUpdateTime(resident.getRegisterId(), curTime);
                if(consume==null) continue;
                amount = amount + consume.getCurAmount();
                rechargeVolume = rechargeVolume.add(consume.getCurRecharge());
                usedVolume = usedVolume.add(consume.getCurUsed());
            }
            plotRecharge.setAmount(amount);
            plotRecharge.setPlotFactor(plotFactor);
            plotRecharge.setRechargeVolume(rechargeVolume);
            plotRecharge.setCurUsed(usedVolume);
            plotRecharge.setUpdateTime(LocalDateTime.now());
            scheduleUpdateWater.plotRechargeService.addPlotRecharge(plotRecharge);

        }
        new PlotFactorService().updateFactor();
    }
}
