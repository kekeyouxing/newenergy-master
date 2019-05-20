package newenergy.admin.background.service;

import newenergy.core.util.TimeUtil;
import newenergy.db.domain.RemainWater;
import newenergy.db.domain.Resident;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.RemainWaterService;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by HUST Corey on 2019-05-08.
 */
@Service
public class WaterService {
    @Autowired
    private ResidentService residentService;
    @Autowired
    private ResidentRepository residentRepository;
    @Autowired
    private RemainWaterService remainWaterService;
    @Autowired
    private StorageService storageService;

    public RemainWater updateRemainWater(String deviceNum, BigDecimal remainWater){
        Resident condition = new Resident();
        condition.setDeviceNum(deviceNum);
        List<Resident> allRes = residentRepository.findAll(residentService.findByPlotNumOrSearch(condition));
        if(allRes.size() != 1) return null;
        Resident resident = allRes.get(0);
        RemainWater record = remainWaterService.findByRegisterId(resident.getRegisterId());
        if(record == null) {
            record = new RemainWater();
            record.setRegisterId(resident.getRegisterId());
            record.setCurAmount(0);
            record.setCurFirstRemain(new BigDecimal(0));
            record.setCurRecharge(new BigDecimal(0));
        }
        if(record.getCurRecharge()==null) record.setCurRecharge(new BigDecimal(0));
        if(record.getCurFirstRemain()==null) record.setCurFirstRemain(new BigDecimal(0));
        if(record.getCurAmount()==null) record.setCurAmount(0);
        record.setRemainVolume(remainWater);
        record.setUpdateTime(TimeUtil.getUTCNow());
        return remainWaterService.updateRemainWater(record);
    }
    public BigDecimal getExtraWater(String deviceNum){
        BigDecimal result = new BigDecimal(0);
        if(storageService.containsExtraWater(deviceNum)){
            result = result.add(storageService.getAndDropExtraWater(deviceNum));
        }
        return result;
    }
    public void updateRequireWater(String deviceNum, boolean started){
        storageService.updateRequireWater(deviceNum,started);
    }
}
