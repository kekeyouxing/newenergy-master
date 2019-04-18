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
import java.time.LocalDateTime;
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

    @Transactional
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void configureTasks(){
        List<ExtraWater> sortedExtraWaterList = extraWaterService.findAll();
        for(ExtraWater extraWater : sortedExtraWaterList){
            BigDecimal addVolume = extraWater.getAddVolume();
            RemainWater remainWater = remainWaterService.findByRegisterId(extraWater.getRegisterId());
            if (isTrustworthy(remainWater)){
                updateReaminwater(remainWater,addVolume);
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
     */
    @Transactional
    public void updateReaminwater(RemainWater remainWater,BigDecimal addVolume){
        remainWater.setRemainVolume(remainWater.getRemainVolume().add(addVolume));
        remainWater.setUpdateTime(LocalDateTime.now());
        remainWater.setCurRecharge(remainWater.getCurRecharge().add(addVolume));
        remainWaterService.updateRemainWater(remainWater);
    }


    /**
     * 每月1号定时生成设备用水量月报表
     */
    @Transactional
    @Async
    @Scheduled(cron = "0 0 0 1 1/1 ? *")
    public void updateConsume() {
        List<RemainWater> remainWaters = remainWaterService.findAll();
        for(RemainWater remainWater: remainWaters) {
            StatisticConsume consume = new StatisticConsume();
            consume.setUpdateTime(LocalDateTime.now());
            consume.setCurRecharge(remainWater.getCurRecharge());
            consume.setLastRemain(remainWater.getCurFirstRemain());
            consume.setCurRemain(remainWater.getRemainVolume());
            BigDecimal curUsed = remainWater.getCurRecharge().add(remainWater.getCurFirstRemain()).
                    subtract(remainWater.getRemainVolume());
            consume.setCurUsed(curUsed);
            consumeService.addConsume(consume);
            remainWater.setCurRecharge(new BigDecimal(0));
            remainWater.setCurFirstRemain(remainWater.getRemainVolume());
            remainWater.setUpdateTime(LocalDateTime.now());
            remainWaterService.updateRemainWater(remainWater);
        }
    }

    /**
     * 每月更新小区用水量月报表
     */
    public void updatePlotRecharge() {
        List<CorrPlot> plots = corrPlotService.findAll();
        for(CorrPlot plot: plots) {
            List<Resident> residents = residentService.findByPlotNum(plot.getPlotNum());
        }
    }

}
