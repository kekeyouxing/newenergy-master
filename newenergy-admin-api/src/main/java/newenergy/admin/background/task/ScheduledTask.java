package newenergy.admin.background.task;

import newenergy.admin.background.service.DeviceRequireService;
import newenergy.core.pojo.MsgRet;
import newenergy.core.util.TimeUtil;
import newenergy.db.constant.AdminConstant;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.domain.*;
import newenergy.db.predicate.AdminPredicate;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.service.BackupService;
import newenergy.db.service.FaultRecordService;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.service.RemainWaterService;
import newenergy.db.util.StringUtilCorey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-05-08.
 *
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
    @Autowired
    private NewenergyAdminService newenergyAdminService;
    @Autowired
    private BackupService backupService;

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Value("${server.port}")
    private String port;
    private  String sendMsgPrefix = "http://localhost:";
    private String reportSendSuffix = "/wx/report/send";
    private String thresholdSuffix = "/wx/threshold/send";
    private RestTemplate restTemplate;
    ScheduledTask(){
        restTemplate = new RestTemplate();
    }

    /**
     * 超时时间，秒。
     * TODO [TEST]模拟300秒超时
     */
    private Integer timeOut = 300;


    /**
     * 检查故障记录超时情况
     * TODO [TEST]模拟每分钟
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkTimeOut(){
        log.info("检查故障记录超时定时任务启动>>>>>");
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setState(FaultRecordConstant.STATE_WAIT);
        predicate.setFaultTime(LocalDateTime.now().minusSeconds(timeOut));
        Page<FaultRecord> records = faultRecordService.findByPredicate(predicate,null,null);
        records.forEach(record -> {
            log.info("超时故障记录id："+record.getId());
            record.setState(FaultRecordConstant.STATE_FINISH);
            record.setResult(FaultRecordConstant.RESULT_TIMEOUT);
            faultRecordService.updateRecord(record);

            Resident resident = faultRecordService.getResident(record.getRegisterId());
            CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
            NewenergyAdmin servicer = faultRecordService.getNewenergyAdmin(record.getServicerId());
            List<NewenergyAdmin> allRes =
                    newenergyAdminService
                    .findAllByRoleIds(
                            new Integer[]{AdminConstant.ROLE_FAULTLEADER}
                    );
            String openid = null;
            if(!allRes.isEmpty()){
                openid = allRes.get(0).getOpenid();
            }

            Map<String,Object> request = new HashMap<>();
            request.put("touser",openid);
            request.put("faultTime",TimeUtil.getString(record.getFaultTime()));
            request.put("address",corrAddress.getAddressDtl());
            String servicerName = "";
            if(servicer != null && !StringUtilCorey.emptyCheck(servicer.getRealName())) servicerName = servicer.getRealName();
            request.put("servicer",servicerName);
            request.put("faultId",record.getId());
            restTemplate.postForObject(sendMsgPrefix+port+reportSendSuffix,request, MsgRet.class);
        });

        log.info("检查故障记录超时定时任务结束>>>>>");

    }

    /**
     * 余额不足提醒
     * TODO [TEST]模拟每10分钟检查阈值
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void checkThreshold(){
        log.info("检查余额定时任务启动>>>>>");
        List<RemainWater> remainWaterList = remainWaterService.findAll();
        remainWaterList.forEach(remainWater -> {
            String registerId = remainWater.getRegisterId();
            Resident resident = faultRecordService.getResident(registerId);
            if(resident == null) return;
            CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
            String address = "";
            if(corrAddress != null) address = corrAddress.getAddressDtl();
            log.info("剩余水量："+remainWater.getRemainVolume()+"; 阈值："+resident.getThreshold()+"; 登记号："+registerId);
            if(remainWater.getRemainVolume()==null || resident.getThreshold()==null) return;
            int compare = remainWater.getRemainVolume().compareTo(new BigDecimal(resident.getThreshold()));
            if(compare <= 0){
                Map<String,Object> request = new HashMap<>();
                request.put("touser",resident.getOpenid());
                request.put("remainWater",remainWater.getRemainVolume());
                request.put("registerId",registerId);
                request.put("username",resident.getUserName());
                request.put("address",address);
                restTemplate.postForObject(sendMsgPrefix+port+thresholdSuffix,request,MsgRet.class);
            }
        });
        log.info("检查余额定时任务结束>>>>>");

    }

    /**
     * 每日备份发送数据库
     * TODO [TEST]模拟每10分钟备份
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void saveAndSendBackup(){
        List<NewenergyAdmin> admins = newenergyAdminService.findAllByRoleIds(new Integer[]{AdminConstant.ROLE_BACKUP});
        NewenergyAdmin backupAdmin = admins.isEmpty()?null:admins.get(0);

        if(admins.isEmpty() || backupAdmin==null){
            log.error("没有备份人员用户，备份失败");
            return;
        }
        if(StringUtilCorey.emptyCheck( backupAdmin.getEmail() )){
            log.error("备份人员没有邮箱，备份失败");
            return;
        }

        try {
            backupService.saveBackup();
        }catch (Exception e){
            log.error("保存失败，备份失败");
            e.printStackTrace();
            return;
        }
        try{
            backupService.sendBackup(backupAdmin.getEmail());
        }catch (Exception e){
            log.error("发送邮件失败，备份失败");
            e.printStackTrace();
        }

    }




}
