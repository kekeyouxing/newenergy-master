package newenergy.admin.controller;


import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RefundRecord;
import newenergy.db.service.RefundRecordService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refundRecord")
@Validated
public class RefundRecordController {

    @Autowired
    private RefundRecordService refundRecordService;

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
                         @RequestParam Integer operatorId){
        JSONArray jsonArray = new JSONArray(string);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            RefundRecord refundRecord = refundRecordService.findById(jsonObject.getInt("id"));
            refundRecord.setState(jsonObject.getInt("state"));
            refundRecordService.updateRefundRecord(refundRecord,operatorId);
        }
        return ResponseUtil.ok();
    }
}
