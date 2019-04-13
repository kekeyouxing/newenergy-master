package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.ManualRecordService;
import newenergy.db.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RechargeRecord findById(@RequestParam Integer id){
        System.out.println(id);
        return rechargeRecordService.findById(id);
    }

//根据订单状态和注册号查充值记录
    @RequestMapping(value = "/findByRechargeRecord", method = RequestMethod.GET)
    public List<RechargeRecord> findByRegisterId(@RequestParam(defaultValue = "") String registerId,
                                                 @RequestParam(defaultValue = "0") Integer safeDelete,
                                                 @RequestParam(defaultValue = "-1") Integer state){
            return rechargeRecordService.findByRegisterIdAndSafeDeleteAndState(registerId,safeDelete,state);
    }

//    根据批量充值订单和审核状态查询批量充值记录
    @RequestMapping(value = "/findByBatchRecord", method = RequestMethod.GET)
    public List<RechargeRecord> findByBatchRelative(@RequestParam(defaultValue = "-1") Integer batchRecordId,
                                                    @RequestParam(defaultValue = "-1") Integer reviewState,
                                                    @RequestParam(defaultValue = "0") Integer safeDelete){
        return rechargeRecordService.findByBatchRecordAndReviewState(batchRecordId,reviewState,safeDelete);
    }

//    //    添加批量充值
//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public Object add(@RequestParam Integer operatorId,
//                      @RequestBody BatchAndRecharge batchAndRecharge){
//        BatchAndRecharge batchAndRecharge1 = new BatchAndRecharge();
//        return batchAndRecharge.getRechargeRecords().size();
//    }


    //    充值订单审核
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody String string,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip) throws JSONException, CloneNotSupportedException {
        JSONArray jsonArray = new JSONArray(string);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject= jsonArray.getJSONObject(i);
            RechargeRecord rechargeRecord = (RechargeRecord) rechargeRecordService.findById(jsonObject.getInt("id")).clone();
            rechargeRecord.setReviewState(jsonObject.getInt("reviewState"));
            RechargeRecord newRecord = rechargeRecordService.updateRechargeRecord(rechargeRecord,operatorId);
            ManualRecord manualRecord = new ManualRecord();
            manualRecord.setLaborId(operatorId);
            manualRecord.setEvent(1);
            manualRecord.setLaborIp(ip);
            manualRecord.setRecordId(newRecord.getId());
            manualRecord.setOperateTime(LocalDateTime.now());
            manualRecordService.save(manualRecord);
        }
        return ResponseUtil.ok();
    }

}
