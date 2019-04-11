package newenergy.admin.controller;

import newenergy.db.domain.BatchRelative;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.service.BatchRelativeService;
import newenergy.db.service.RechargeRecordService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    BatchRelativeService batchRelativeService;


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
                                                    @RequestParam(defaultValue = "-1") Integer state){
        List<BatchRelative> relatives = batchRelativeService.findByBatchRecordIdAndState(batchRecordId,state);
        List<RechargeRecord> records = new ArrayList<>();
        for (BatchRelative batchRelative:relatives
                ) {
            records.add(rechargeRecordService.findById(batchRelative.getRechargeRecordId()));
        }
        return records;
    }

//    //    充值订单审核
//    @RequestMapping(value = "/findByBatchRecord", method = RequestMethod.POST)
//    public List<RechargeRecord> findByBatchRelative(@RequestBody String string,
//                                                    @RequestParam Integer operatorId){
//        JSONArray jsonArray = new JSONArray(string);
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObject= jsonArray.getJSONObject(i);
//            RechargeRecord rechargeRecord = rechargeRecordService.findById(jsonObject.getInt("id"));
//            rechargeRecord.setsta
//
//        }
//    }

}
