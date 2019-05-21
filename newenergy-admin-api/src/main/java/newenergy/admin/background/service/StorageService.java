package newenergy.admin.background.service;

import newenergy.admin.background.communicate.constant.StoragePath;
import newenergy.core.util.TimeUtil;
import newenergy.db.domain.Resident;
import newenergy.db.global.Parameters;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.ResidentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());
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
    /**
     * key: deviceNum
     * value: requireWater的过期时间
     */
    private ConcurrentHashMap<String, LocalDateTime> requireWaterTrustMap;
    StorageService(){
        extraWaterMap = loadExtraWaterMap();
        requireWaterMap = loadRequireWaterMap();
        requireWaterTrustMap = loadRequireTrustMap();

    }

    public void saveMaps(){
        try(ObjectOutputStream oosExtra = new ObjectOutputStream(new FileOutputStream(StoragePath.EXTRA_WATER));
            ObjectOutputStream oosRequire = new ObjectOutputStream(new FileOutputStream(StoragePath.REQUIRE_WATER));
            ObjectOutputStream oosRequireTrust = new ObjectOutputStream(new FileOutputStream(StoragePath.REQUIRE_TRUST))
        ){
            oosExtra.writeObject(extraWaterMap);
            oosRequire.writeObject(requireWaterMap);
            oosRequireTrust.writeObject(requireWaterTrustMap);
        }catch (IOException e){
            e.printStackTrace();
            logger.error("存储maps失败");
        }
    }
    public ConcurrentHashMap<String,BigDecimal> loadExtraWaterMap(){
        ConcurrentHashMap<String,BigDecimal> extra = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoragePath.EXTRA_WATER))
        ){
            extra = (ConcurrentHashMap<String, BigDecimal>) ois.readObject();
        }catch (IOException e){
            logger.error("读取extraWaterMap失败");
        }catch (ClassNotFoundException e){
            logger.info("未找到extraWaterMap");
        }
        if(extra==null) extra = new ConcurrentHashMap<>();

//DEBUG
for(Map.Entry<String,BigDecimal> entry : extra.entrySet()){
    System.out.println(entry.getKey()+":"+entry.getValue());
}

        return extra;
    }

    public ConcurrentHashMap<String,BigDecimal> loadRequireWaterMap(){
        ConcurrentHashMap<String,BigDecimal> require = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoragePath.REQUIRE_WATER))
        ){
            require = (ConcurrentHashMap<String, BigDecimal>) ois.readObject();
        }catch (IOException e){
            logger.error("读取requireWaterMap失败");
        }catch (ClassNotFoundException e){
            logger.info("未找到requireWaterMap");
        }
        if(require==null) require = new ConcurrentHashMap<>();

//DEBUG
        for(Map.Entry<String,BigDecimal> entry : require.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

        return require;
    }

    public ConcurrentHashMap<String,LocalDateTime> loadRequireTrustMap(){
        ConcurrentHashMap<String,LocalDateTime> trust = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoragePath.REQUIRE_TRUST))
        ){
            trust = (ConcurrentHashMap<String, LocalDateTime>) ois.readObject();
        }catch (IOException e){
            logger.error("读取requireTrustMap失败");
        }catch (ClassNotFoundException e){
            logger.info("未找到requireTrustMap");
        }
        if(trust==null) trust = new ConcurrentHashMap<>();

//DEBUG
        for(Map.Entry<String,LocalDateTime> entry : trust.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue());
        }

        return trust;
    }


    /**
     * 定时任务更新剩余用水量时，需要调用此方法
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
        saveMaps();
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
        saveMaps();
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
            //添加可信时间
            requireWaterTrustMap.put(deviceNum, TimeUtil.getUTCNow().plusSeconds(Parameters.TRUSTDURATION));
        }else{
            requireWaterMap.remove(deviceNum);
            requireWaterTrustMap.remove(deviceNum);
        }
        saveMaps();
    }

    /**
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
                //判断是否过期
                if(!requireWaterTrustMap.containsKey(resident.getDeviceNum())
                        || requireWaterTrustMap.get(resident.getDeviceNum()).isBefore(TimeUtil.getUTCNow())){
                    requireWaterMap.remove(resident.getDeviceNum());
                    requireWaterTrustMap.remove(resident.getDeviceNum());
                }else {
                    result = result.add(requireWaterMap.get(resident.getDeviceNum()));
                }
            }
        }
        saveMaps();
        return result;
    }

}
