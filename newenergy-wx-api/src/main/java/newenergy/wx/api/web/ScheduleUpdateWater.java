package newenergy.wx.api.web;

import newenergy.db.domain.*;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.swing.MenuItemLayoutHelper;

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

    @Transactional
    @Async
    //@Scheduled(cron = "0/5 * * * * ?")
    public void configureTasks(){
        List<ExtraWater> sortedExtraWaterList = extraWaterService.findAll();
        for(ExtraWater extraWater : sortedExtraWaterList){
            BigDecimal addVolume = extraWater.getAddVolume();
            Integer amount = rechargeRecordService.findById(extraWater.getRecordId()).getAmount();
            RemainWater remainWater = remainWaterService.findByRegisterId(extraWater.getRegisterId());
            if (isTrustworthy(remainWater)){
                updateReaminwater(remainWater,addVolume,amount);
                extraWaterService.deleteRecord(extraWater);
            }
        }
    }

    private boolean isTrustworthy(RemainWater remainWater){
        LocalDateTime updataTime = remainWater.getUpdateTime();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now,updataTime);
        long differMinutes = duration.toMinutes();
        if (differMinutes > 10){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * 更新剩余水量表
     * 此处应该加上对日期的判断，比如是否为新的月份，后续添加
     * （修改）数据库表新增字段当月累计充值金额，更新表时更新金额
     */
    @Transactional
    public void updateReaminwater(RemainWater remainWater,BigDecimal addVolume, Integer amount){
        remainWater.setRemainVolume(remainWater.getRemainVolume().add(addVolume));
        remainWater.setUpdateTime(LocalDateTime.now());
        remainWater.setCurRecharge(remainWater.getCurRecharge().add(addVolume));
        remainWater.setCurAmount(remainWater.getCurAmount()+amount);
        remainWaterService.updateRemainWater(remainWater);
    }


    /**
     * 每月1号定时生成设备用水量月报表
     */
    @Transactional
    @Async
    @Scheduled(cron = "0 0 0 1 1/1 ?")
    public void updateConsume() {
        List<RemainWater> remainWaters = remainWaterService.findAll();
        for(RemainWater remainWater: remainWaters) {
            StatisticConsume consume = new StatisticConsume();
            consume.setUpdateTime(LocalDateTime.now());
            consume.setCurRecharge(remainWater.getCurRecharge());
            consume.setLastRemain(remainWater.getCurFirstRemain());
            consume.setCurRemain(remainWater.getRemainVolume());
            consume.setCurAmount(remainWater.getCurAmount());
            BigDecimal curUsed = remainWater.getCurRecharge().add(remainWater.getCurFirstRemain()).
                    subtract(remainWater.getRemainVolume());
            consume.setCurUsed(curUsed);
            consumeService.addConsume(consume);
            remainWater.setCurRecharge(new BigDecimal(0));
            remainWater.setCurAmount(0);
            remainWater.setCurFirstRemain(remainWater.getRemainVolume());
            remainWater.setUpdateTime(LocalDateTime.now());
            remainWaterService.updateRemainWater(remainWater);
        }
    }

    /**
     * 每月更新小区用水量月报表
     */
    @Transactional
    @Async
    @Scheduled(cron = "0 30 0 1 1/1 ?")
    public void updatePlotRecharge() {
        List<CorrPlot> plots = corrPlotService.findAll();
        for(CorrPlot plot: plots) {
            StatisticPlotRecharge plotRecharge = new StatisticPlotRecharge();
            plotRecharge.setPlotNum(plot.getPlotNum());
            Integer amount = 0;
            BigDecimal plotFactor = plot.getPlotFactor();
            BigDecimal rechargeVolume = new BigDecimal(0);
            BigDecimal usedVolume = new BigDecimal(0);
            LocalDate curTime = LocalDate.now();
            List<Resident> residents = residentService.findByPlotNum(plot.getPlotNum());
            for(Resident resident: residents) {
                StatisticConsume consume = consumeService.findByRegisterIdAndUpdateTime(resident.getRegisterId(), curTime);
                amount = amount + consume.getCurAmount();
                rechargeVolume = rechargeVolume.add(consume.getCurRecharge());
                usedVolume = usedVolume.add(consume.getCurUsed());
            }
            plotRecharge.setAmount(amount);
            plotRecharge.setPlotFactor(plotFactor);
            plotRecharge.setRechargeVolume(rechargeVolume);
            plotRecharge.setCurUsed(usedVolume);
            plotRecharge.setUpdateTime(LocalDateTime.now());
            plotRechargeService.addPlotRecharge(plotRecharge);

        }
    }

}
