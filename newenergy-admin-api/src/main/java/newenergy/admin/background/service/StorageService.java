package newenergy.admin.background.service;

import newenergy.admin.background.communicate.constant.RefundState;
import newenergy.admin.background.communicate.constant.StoragePath;
import newenergy.core.util.TimeUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RefundRecord;
import newenergy.db.domain.RemainWater;
import newenergy.db.domain.Resident;
import newenergy.db.global.Parameters;
import newenergy.db.repository.ResidentRepository;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.RefundRecordService;
import newenergy.db.service.RemainWaterService;
import newenergy.db.service.ResidentService;
import newenergy.db.util.StringUtilCorey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by HUST Corey on 2019-05-08.
 */
@Service
public class StorageService {
    @Autowired
    ResidentService residentService;
    @Autowired
    ResidentRepository residentRepository;
    @Autowired
    RefundRecordService refundRecordService;
    @Autowired
    RechargeRecordService rechargeRecordService;
    @Autowired
    RemainWaterService remainWaterService;

    RestTemplate restTemplate;

    String refundUrl = "http://localhost/wx/order/refund";

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

    /**
     * key: orderId
     * value: refundWater
     */
    private ConcurrentHashMap<Integer, BigDecimal> refundWaterMap;

    /**
     * key: deviceNum
     * value: list of rechargeRecord id
     */
    private ConcurrentHashMap<String, List<Integer>> notifyRemainWaterMap;
    StorageService(){
        extraWaterMap = loadExtraWaterMap();
        requireWaterMap = loadRequireWaterMap();
        requireWaterTrustMap = loadRequireTrustMap();
        refundWaterMap = loadRefundWaterMap();
        notifyRemainWaterMap = loadNotifyRemainWaterMap();
        restTemplate = new RestTemplate();
    }



    public void saveMaps(){
        try(ObjectOutputStream oosExtra = new ObjectOutputStream(new FileOutputStream(StoragePath.EXTRA_WATER));
            ObjectOutputStream oosRequire = new ObjectOutputStream(new FileOutputStream(StoragePath.REQUIRE_WATER));
            ObjectOutputStream oosRequireTrust = new ObjectOutputStream(new FileOutputStream(StoragePath.REQUIRE_TRUST));
            ObjectOutputStream oosRefund = new ObjectOutputStream(new FileOutputStream(StoragePath.REFUND_WATER));
            ObjectOutputStream oosNotify = new ObjectOutputStream(new FileOutputStream(StoragePath.NOTIFY_REMAIN));
        ){
            oosExtra.writeObject(extraWaterMap);
            oosRequire.writeObject(requireWaterMap);
            oosRequireTrust.writeObject(requireWaterTrustMap);
            oosRefund.writeObject(refundWaterMap);
            oosNotify.writeObject(notifyRemainWaterMap);
        }catch (IOException e){
            e.printStackTrace();
            logger.error("存储maps失败");
        }
    }
    public ConcurrentHashMap<String, List<Integer>> loadNotifyRemainWaterMap() {
        ConcurrentHashMap<String,List<Integer>> notifyList = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoragePath.NOTIFY_REMAIN))
        ){
            notifyList = (ConcurrentHashMap<String, List<Integer>>) ois.readObject();
        }catch (IOException e){
            logger.error("读取notifyRemainWaterMap失败");
        }catch (ClassNotFoundException e){
            logger.info("未找到notifyRemainWaterMap");
        }
        if(notifyList==null) notifyList = new ConcurrentHashMap<>();

        return notifyList;
    }

    public ConcurrentHashMap<Integer,BigDecimal> loadRefundWaterMap(){
        ConcurrentHashMap<Integer,BigDecimal> refund = null;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(StoragePath.REFUND_WATER))
        ){
            refund = (ConcurrentHashMap<Integer, BigDecimal>) ois.readObject();
        }catch (IOException e){
            logger.error("读取refundWaterMap失败");
        }catch (ClassNotFoundException e){
            logger.info("未找到refundWaterMap");
        }
        if(refund==null) refund = new ConcurrentHashMap<>();

        return refund;
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


        return trust;
    }

    /**
     * 将剩余水量通知到对应的所有充值记录
     * @param deviceNum 机器编码
     * @param remainWater
     */
    public void notifyPostSolve(String deviceNum,BigDecimal remainWater){
        if(notifyRemainWaterMap.containsKey(deviceNum)){
            List<Integer> ids = notifyRemainWaterMap.get(deviceNum);
            ids.forEach(id->updateVolume(remainWater,id));
            notifyRemainWaterMap.remove(deviceNum);
        }
    }

    public void updateVolume(BigDecimal remainVolume, Integer rechargeRecordId){
        //对充值记录进行更新
        RechargeRecord record = rechargeRecordService.findById(rechargeRecordId);
        record.setRemainVolume(remainVolume);
        if(record.getRechargeVolume() != null)
            record.setUpdatedVolume( remainVolume.add( record.getRechargeVolume() ) );
        rechargeRecordService.updateRechargeRecord(record,null);

        //对剩余水量表的充值流量和充值金额进行更新
        RemainWater remainWater = remainWaterService.findByRegisterId(record.getRegisterId());
        //在之前的WaterService.updateRemainWater()中已经完成了对剩余水量的初始化
        if(remainWater == null || remainWater.getCurAmount()==null || remainWater.getCurRecharge() == null) return;
        if(record.getAmount() != null)
            remainWater.setCurAmount(remainWater.getCurAmount() + record.getAmount());
        if(record.getRechargeVolume() != null)
            remainWater.setCurRecharge(remainWater.getCurRecharge().add(record.getRechargeVolume()));
        remainWaterService.updateRemainWater(remainWater);

    }

    /**
     * 添加待通知剩余水量的充值记录id
     * @param deviceNum 机器编码
     * @param recordId 充值记录id
     */
    public void addNotifyItem(String deviceNum, Integer recordId){
        if( notifyRemainWaterMap.containsKey(deviceNum) ){
            List<Integer> ids = notifyRemainWaterMap.get(deviceNum);
            ids.add(recordId);
            notifyRemainWaterMap.put(deviceNum,ids);
        }else{
            notifyRemainWaterMap.put(deviceNum, Arrays.asList(recordId));
        }
    }

    /**
     * 退款处理
     * @param deviceNum
     */
    public void refundPostSolve(String deviceNum){
        List<Integer> ids = getAllOrderIdByDeviceNum(deviceNum);
        BigDecimal success = new BigDecimal(RefundState.SUCCESS);
        BigDecimal failed = new BigDecimal(RefundState.FAILED);
        for(Integer id : ids){
            logger.info("orderId:"+id+",refundWater:"+refundWaterMap.get(id));
            //退款成功
            if(success.equals( refundWaterMap.get(id) )){
                Map<String,Object> request = new HashMap<>();
                request.put("orderId",id);
                restTemplate.postForObject(refundUrl,request,Object.class);
            }else if(failed.equals( refundWaterMap.get(id) )){  //退款失败
                RefundRecord refundRecord = refundRecordService.findByIdWithAlive(id);
                if(refundRecord != null){
                    refundRecord.setState(RefundState.STATE_CHECK_REFUND_FAILED);
                    refundRecordService.updateRefundRecord(refundRecord,null);
                }
            }
            refundWaterMap.remove(id);
        }
    }

    /**
     *  退款（微信充值退款）审核通过后，调用该方法
     * @param orderid 退款记录id
     * @param refundWater 退款水量，为正数
     */
    synchronized public void addRefundWater(Integer orderid, BigDecimal refundWater){
        RefundRecord refundRecord = refundRecordService.findByIdWithAlive(orderid);
        if(refundRecord == null) return;
        if(refundWater.signum() <= 0) return;
        refundWaterMap.put(orderid,refundWater);
        logger.info("添加退款信息，orderid:"+orderid+";refundWater:"+refundWater);
        saveMaps();
    }

    /**
     * 获取refundWaterMap中该deviceNum对应的orderid
     * @param deviceNum
     * @return
     */
    public List<Integer> getAllOrderIdByDeviceNum(String deviceNum){
        List<Integer> result = new ArrayList<>();
        Resident resident = residentService.findByDeviceNumWithAlive(deviceNum);
        if(resident==null) return result;
        String registerId = resident.getRegisterId();
        if(StringUtilCorey.emptyCheck(registerId)) return result;

        return refundRecordService
                .findByRegisterIdWithAlive(registerId)
                .stream()
                .map(RefundRecord::getId)
                .filter(refundWaterMap::containsKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取该deviceNum对应的所有退款水量之和
     * @param deviceNum
     * @return
     */
    synchronized public BigDecimal getAllRefundWater(String deviceNum){
        Resident resident = residentService.findByDeviceNumWithAlive(deviceNum);
        if(resident == null) return new BigDecimal(0);
        String registerId = resident.getRegisterId();
        if(StringUtilCorey.emptyCheck(registerId)) return new BigDecimal(0);
        List<RefundRecord> records = refundRecordService.findByRegisterIdWithAlive(registerId);

        BigDecimal result = new BigDecimal(0);
        for(RefundRecord record : records){
            if(refundWaterMap.containsKey(record.getId()) && refundWaterMap.get(record.getId()).signum() > 0){
                BigDecimal refundWater = refundWaterMap.get(record.getId());
                result = result.add( refundWater );
            }
        }
        saveMaps();
        return result;
    }
    public boolean hasRefundWater(Integer orderid){
        return refundWaterMap.containsKey(orderid);
    }
    public void setRefundWater(Integer orderid, BigDecimal value){
        refundWaterMap.put(orderid,value);
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
