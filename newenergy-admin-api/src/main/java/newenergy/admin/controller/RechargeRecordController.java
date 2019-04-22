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
@RequestMapping("/admin/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

//    根据id查询批量充值记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RechargeRecord findById(@RequestParam Integer id){
        System.out.println(id);
        return rechargeRecordService.findById(id);
    }

//    根据批量充值id，审核状态，注册id，订单状态查询批量充值记录
    @RequestMapping(value = "/findByConditions", method = RequestMethod.GET)
    public List<RechargeRecord> findByConditions(@RequestParam(required = false) Integer batchRecordId,
                                                 @RequestParam(required = false) Integer reviewState,
                                                 @RequestParam(required = false) String registerId,
                                                 @RequestParam(required = false) Integer state
                                                 ){
        return rechargeRecordService.findByConditions(batchRecordId,reviewState,registerId,state);
    }

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
            if (jsonObject.getInt("reviewState")==2){
                rechargeRecord.setState(1);
            }else {
                rechargeRecord.setState(0);
            }
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
