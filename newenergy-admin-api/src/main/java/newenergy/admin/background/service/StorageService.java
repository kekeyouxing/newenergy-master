package newenergy.admin.background.service;

import newenergy.db.domain.Resident;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by HUST Corey on 2019-05-08.
 */
@Service
public class StorageService {
    @Autowired
    ResidentService residentService;
    @Autowired
    ResidentRepository residentRepository;
    /**
     * key: deviceNum
     * value: extraWater
     */
    private ConcurrentHashMap<String, BigDecimal> extraWaterMap;
    /**
     * key: deviceNum
     * value: requireWater
     */
    private ConcurrentHashMap<String, BigDecimal> requireWaterMap;

    StorageService(){
        extraWaterMap = new ConcurrentHashMap<>();
        requireWaterMap = new ConcurrentHashMap<>();
    }

    /**
     * TODO 定时任务更新剩余用水量时，需要调用此方法
     * @param registerId
     * @param extraWater
     */
    synchronized public void addExtraWater(String registerId, BigDecimal extraWater){
        Resident resident = residentService.fingByRegisterId(registerId);
        String deviceNum = resident.getDeviceNum();
        BigDecimal lastWater = new BigDecimal(0);
        if(extraWaterMap.containsKey(deviceNum) && extraWaterMap.get(deviceNum) != null){
            lastWater = lastWater.add(extraWaterMap.get(deviceNum));
        }
        extraWaterMap.put(deviceNum,lastWater.add(extraWater));
    }

    /**
     * 是否存在新增水量，用于响应设备发送的消息
     * @param deviceNum
     * @return
     */
    synchronized public boolean containsExtraWater(String deviceNum){
        return extraWaterMap.containsKey(deviceNum);
    }

    /**
     * 获取并删除新增水量，用于响应设备发送的消息
     * @param deviceNum
     * @return
     */
    synchronized public BigDecimal getAndDropExtraWater(String deviceNum){
        BigDecimal result = null;
        if(extraWaterMap.containsKey(deviceNum)){
            result = extraWaterMap.get(deviceNum);
            extraWaterMap.remove(deviceNum);
        }
        return result;
    }

    synchronized public void updateRequireWater(String deviceNum, boolean started){
        Resident condition = new Resident();
        condition.setDeviceNum(deviceNum);
        List<Resident> allRes = residentRepository.findAll( residentService.findByPlotNumOrSearch(condition) );
        if(allRes == null || allRes.size() != 1) return;
        Resident resident = allRes.get(0);
        BigDecimal ratedFlow = resident.getRatedFlow();
        if(started){
            requireWaterMap.put(deviceNum,ratedFlow);
        }else{
            requireWaterMap.remove(deviceNum);
        }
    }

    /**
     * TODO 计算需水量定时任务调用
     * @param plotNum
     * @return
     */
    public BigDecimal calRequireWaterByPlotNum(String plotNum){
        Resident condition = new Resident();
        condition.setPlotNum(plotNum);
        List<Resident> residents = residentRepository.findAll( residentService.findByPlotNumOrSearch(condition) );
        BigDecimal result = new BigDecimal(0);
        for(Resident resident : residents){
            if(requireWaterMap.containsKey(resident.getDeviceNum())){
                result = result.add(requireWaterMap.get(resident.getDeviceNum()));
            }
        }
        return result;
    }

}
