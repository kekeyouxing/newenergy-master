package newenergy.admin.controller;


import com.google.gson.*;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.ManualRecord;
import newenergy.db.domain.RefundRecord;
import newenergy.db.service.ManualRecordService;
import newenergy.db.service.RefundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/refundRecord")
@Validated
public class RefundRecordController {

    @Autowired
    private RefundRecordService refundRecordService;

    @Autowired
    private ManualRecordService manualRecordService;

//    通过订单id查询退款记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RefundRecord findById(@RequestParam Integer id){
        return refundRecordService.findById(id);

    }
//通过注册id，状态查询订单
    @RequestMapping(value = "/findRefundRecord", method = RequestMethod.GET)
    public List<RefundRecord> list(@RequestParam(defaultValue = "") String registerId,
                                   @RequestParam(defaultValue = "-1") Integer state,
                                   @RequestParam(defaultValue = "0") Integer safeDelete
                                   ){
        return refundRecordService.findByCondition(registerId,state,safeDelete);

    }

    //    审核退款记录
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody String string,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip) throws JSONException, CloneNotSupportedException {
        JSONArray jsonArray = new JSONArray(string);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            RefundRecord refundRecord = (RefundRecord) refundRecordService.findById(jsonObject.getInt("id")).clone();
            refundRecord.setState(jsonObject.getInt("state"));
            RefundRecord newRecore = refundRecordService.updateRefundRecord(refundRecord,operatorId);
            addManualRecord(newRecore.getId(),operatorId,ip,3);
        }
        return ResponseUtil.ok();
    }

//    发起退款
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object review(@RequestBody RefundRecord refundRecord,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip) throws JSONException {
            refundRecord.setState(1);
            refundRecord.setRefundTime(LocalDateTime.now());
            refundRecord.setRechargeId(operatorId);
            RefundRecord newRecord = refundRecordService.addRefundRecord(refundRecord,operatorId);
            addManualRecord(newRecord.getId(),operatorId,ip,2);
            return ResponseUtil.ok();
    }

    //    添加人工操作记录
    public void addManualRecord(Integer recordId,Integer operatorId,Integer ip,Integer event){
        ManualRecord manualRecord = new ManualRecord();
        manualRecord.setRecordId(recordId);
        manualRecord.setOperateTime(LocalDateTime.now());
        manualRecord.setLaborIp(ip);
        manualRecord.setLaborId(operatorId);
        manualRecord.setEvent(event);
        manualRecordService.save(manualRecord);
    }
}
