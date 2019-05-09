package newenergy.admin.background.task;

import newenergy.core.pojo.MsgRet;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.domain.*;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.service.FaultRecordService;
import newenergy.db.service.RemainWaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-05-08.
 *
 * TODO 定时任务
 * 定时检查故障记录超时情况
 * 定时轮询用户阈值
 *
 */

@Service
public class ScheduledTask {
    @Autowired
    private FaultRecordService faultRecordService;
    @Autowired
    private RemainWaterService remainWaterService;

    @Value("${server.port}")
    private String port;
    private  String sendMsgUrl = "http://localhost:" + port + "/wx/report/send";
    private RestTemplate restTemplate;
    ScheduledTask(){
        restTemplate = new RestTemplate();
    }

    /**
     * 超时时间，秒。
     * [TEST]模拟120秒超时
     */
    private Integer timeOut = 120;


    /**
     * 检查故障记录超时情况
     * [TEST]模拟每10秒
     */
    @Scheduled(cron = "300/10 * * * * ?")
    public void checkTimeOut(){
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setState(FaultRecordConstant.STATE_WAIT);
        predicate.setFaultTime(LocalDateTime.now().minusSeconds(timeOut));
        Page<FaultRecord> records = faultRecordService.findByPredicate(predicate,null,null);
        records.forEach(record -> {
            record.setState(FaultRecordConstant.STATE_FINISH);
            record.setResult(FaultRecordConstant.RESULT_TIMEOUT);

            Resident resident = faultRecordService.getResident(record.getRegisterId());
            CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
            NewenergyAdmin servicer = faultRecordService.getNewenergyAdmin(record.getServicerId());
            Map<String,Object> request = new HashMap<>();
            request.put("touser",servicer.getOpenid());
            request.put("faultTime",record.getFaultTime());
            request.put("address",corrAddress.getAddressDtl());
            request.put("servicer",servicer.getRealName());
            request.put("faultId",record.getId());
            restTemplate.postForObject(sendMsgUrl,request, MsgRet.class);
        });
    }

    /**
     * 余额不足提醒
     * [TEST]模拟每分钟检查阈值
     */
    @Scheduled(cron = "0 5/1 * * * ?")
    public void checkThreshold(){
        List<RemainWater> remainWaterList = remainWaterService.findAll();
        remainWaterList.forEach(remainWater -> {
            String registerId = remainWater.getRegisterId();
            Resident resident = faultRecordService.getResident(registerId);
            int compare = remainWater.getRemainVolume().compareTo(new BigDecimal(resident.getThreshold()));
            if(compare <= 0){
                Map<String,Object> request = new HashMap<>();
                request.put("touser",resident.getOpenid());
                request.put("remainWater",remainWater.getRemainVolume());
                request.put("updateTime",remainWater.getUpdateTime());
            }
        });
    }



}
