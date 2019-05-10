package newenergy.admin.background.task;

import newenergy.admin.background.service.DeviceRequireService;
import newenergy.admin.background.service.StorageService;
import newenergy.core.util.SpringUtil;
import newenergy.core.util.TimeUtil;
import newenergy.db.domain.CorrPlot;
import newenergy.db.service.CorrPlotService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HUST Corey on 2019-04-17.
 */
public class DeviceRequireRunnable implements Runnable {
    StorageService storageService;
    DeviceRequireService deviceRequireService;
    public DeviceRequireRunnable(){
        storageService = SpringUtil.getBean(StorageService.class);
        deviceRequireService = SpringUtil.getBean(DeviceRequireService.class);
    }

    @Override
    public void run() {

        List<String> plotNums = deviceRequireService.findAllPlotNums();

        System.out.println("计算需水量：");
        for(String plotNum : plotNums){
            BigDecimal require = storageService.calRequireWaterByPlotNum(plotNum);
            deviceRequireService.setRequire(plotNum,require);
            System.out.println(plotNum + " : " + require);
        }
        deviceRequireService.setUpdateTime(TimeUtil.getUTCNow());

    }
}
