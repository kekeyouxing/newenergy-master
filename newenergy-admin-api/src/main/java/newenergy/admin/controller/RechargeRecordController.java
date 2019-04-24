package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.ExtraWaterService;
import newenergy.db.service.ManualRecordService;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.RemainWaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/rechargeRecord")
@Validated
public class RechargeRecordController {

    private static class ReviewState {
        private Integer id;
        private Integer reviewState;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getReviewState() {
            return reviewState;
        }

        public void setReviewState(Integer reviewState) {
            this.reviewState = reviewState;
        }
    }

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

    @Autowired
    RemainWaterService remainWaterService;

    @Autowired
    ExtraWaterService extraWaterService;

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
    public Object review(@RequestBody List<ReviewState> reviewStates,
                         @RequestParam Integer operatorId,
                         @RequestParam Integer ip) throws CloneNotSupportedException {
        for (ReviewState reviewState:reviewStates){
            RechargeRecord rechargeRecord = (RechargeRecord) rechargeRecordService.findById(reviewState.getId()).clone();
            rechargeRecord.setReviewState(reviewState.getReviewState());
            rechargeRecord.setCheckId(operatorId);
            if (reviewState.getReviewState()==1){
                RemainWater remainWater = remainWaterService.findByRegisterId(rechargeRecord.getRegisterId());
                if (remainWater == null){
                    remainWater = new RemainWater();
                    remainWater.setRegisterId(rechargeRecord.getRegisterId());
                    remainWater.setCurRecharge(new BigDecimal(0));
                }
                remainWater.setCurRecharge(rechargeRecord.getRechargeVolume().add(remainWater.getCurRecharge()));
                remainWater.setUpdateTime(LocalDateTime.now());
                remainWaterService.updateRemainWater(remainWater);
                extraWaterService.add(rechargeRecord.getRegisterId(),
                        rechargeRecord.getRechargeVolume(),
                        rechargeRecord.getId(),
                        rechargeRecord.getAmount());
            }else if (reviewState.getReviewState()==2){
                rechargeRecord.setState(1);
            }
            RechargeRecord newRecord = rechargeRecordService.updateRechargeRecord(rechargeRecord,operatorId);
            manualRecordService.add(operatorId,ip,1,newRecord.getId());
        }
        return ResponseUtil.ok();
    }

}
