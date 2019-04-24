package newenergy.admin.controller;


import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.ManualRecord;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RefundRecord;
import newenergy.db.domain.RemainWater;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/refundRecord")
@Validated
public class RefundRecordController {

    private static class ReviewState{
        private Integer id;
        private Integer state;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }
    }

    @Autowired
    private RefundRecordService refundRecordService;

    @Autowired
    private ManualRecordService manualRecordService;

    @Autowired
    RemainWaterService remainWaterService;

    @Autowired
    ExtraWaterService extraWaterService;

    @Autowired
    RechargeRecordService rechargeRecordService;

//    通过订单id查询退款记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RefundRecord findById(@RequestParam Integer id){
        return refundRecordService.findById(id);
    }
//通过注册id，状态查询订单
    @RequestMapping(value = "/findByConditions", method = RequestMethod.GET)
    public List<RefundRecord> list(@RequestParam(required = false) String registerId,
                                   @RequestParam(required = false) Integer state){
        return refundRecordService.findByCondition(registerId,state);
    }

    //    审核退款记录
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody List<ReviewState> reviewStates,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip) throws  CloneNotSupportedException {
//        state为1代表待审核，0代表审核通过，2代表审核不通过
        for (ReviewState reviewState:reviewStates) {
            RefundRecord refundRecord = (RefundRecord) refundRecordService.findById(reviewState.getId()).clone();
            refundRecord.setState(reviewState.getState());
            refundRecord.setCheckId(operatorId);
            if (reviewState.getState()==0){
                RemainWater remainWater = remainWaterService.findByRegisterId(refundRecord.getRegisterId());
                if (remainWater == null){
                    remainWater = new RemainWater();
                    remainWater.setRegisterId(refundRecord.getRegisterId());
                    remainWater.setCurRecharge(new BigDecimal(0));
                }
                remainWater.setCurRecharge(remainWater.getCurRecharge().subtract(refundRecord.getRefundVolume()));
                remainWater.setUpdateTime(LocalDateTime.now());
                remainWaterService.updateRemainWater(remainWater);
                extraWaterService.add(refundRecord.getRegisterId(),
                        refundRecord.getRefundVolume().multiply(new BigDecimal(-1)),
                        refundRecord.getId(),
                        refundRecord.getRefundAmount()*(-1));
                RechargeRecord rechargeRecord = rechargeRecordService.findById(refundRecord.getRecordId());
                rechargeRecord.setState(1);
            }
            RefundRecord newRecord = refundRecordService.updateRefundRecord(refundRecord,operatorId);
            manualRecordService.add(operatorId,ip,3,newRecord.getId());
        }
        return ResponseUtil.ok();
    }

//    发起退款
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object review(@RequestBody RefundRecord refundRecord,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip){
//        state为1代表待审核，0代表审核通过，2代表审核不通过
            refundRecord.setState(1);
            refundRecord.setRefundTime(LocalDateTime.now());
            refundRecord.setRechargeId(operatorId);
            RefundRecord newRecord = refundRecordService.addRefundRecord(refundRecord,operatorId);
            manualRecordService.add(operatorId,ip,2,newRecord.getId());
            return ResponseUtil.ok();
    }

}
