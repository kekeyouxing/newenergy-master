//package newenergy.wx.api.web;
//
//import newenergy.db.domain.ExtraWater;
//import newenergy.db.domain.RemainWater;
//import newenergy.db.service.ExtraWaterService;
//import newenergy.db.service.RemainWaterService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.transaction.Transactional;
//import java.math.BigDecimal;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Component
//@Configuration
//@EnableScheduling
//public class ScheduleUpdateWater {
//    @Autowired
//    private RemainWaterService remainWaterService;
//
//    @Autowired
//    private ExtraWaterService extraWaterService;
//
//    @Transactional
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void configureTasks(){
//        List<ExtraWater> sortedExtraWaterList = extraWaterService.findAll();
//        for(ExtraWater extraWater : sortedExtraWaterList){
//            BigDecimal addVolume = extraWater.getAddVolume();
//            RemainWater remainWater = remainWaterService.findByRegisterId(extraWater.getRegisterId());
//            if (isTrustworthy(remainWater)){
//                updateReaminwater(remainWater,addVolume);
//                extraWaterService.deleteRecord(extraWater);
//            }
//        }
//    }
//
//    private boolean isTrustworthy(RemainWater remainWater){
//        LocalDateTime updataTime = remainWater.getUpdateTime();
//        LocalDateTime now = LocalDateTime.now();
//        Duration duration = Duration.between(now,updataTime);
//        long differMinutes = duration.toMinutes();
//        if (differMinutes > 10){
//            return false;
//        }
//        else{
//            return true;
//        }
//    }
//
//    /**
//     * 更新剩余水量表
//     * 此处应该加上对日期的判断，比如是否为新的月份，后续添加
//     */
//    @Transactional
//    public void updateReaminwater(RemainWater remainWater,BigDecimal addVolume){
//        remainWater.setRemainVolume(remainWater.getRemainVolume().add(addVolume));
//        remainWater.setUpdateTime(LocalDateTime.now());
//        remainWater.setCurRecharge(remainWater.getCurRecharge().add(addVolume));
//        remainWaterService.updateRemainWater(remainWater);
//    }
//}
