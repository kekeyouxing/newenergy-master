package newenergy.admin.controller;


import newenergy.admin.util.IpUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    NewenergyAdminService adminService;

    @Autowired
    ResidentService residentService;

    @Autowired
    CorrAddressService corrAddressService;
    //    未审核通过的订单发起退款
    @RequestMapping(value = "/addRefund", method = RequestMethod.POST)
    public Object review(@RequestBody PostInfo postInfo,
                         HttpServletRequest request){
//        state为1代表待审核，0代表审核通过，2代表审核不通过
        RechargeRecord rechargeRecord = rechargeRecordService.findById(postInfo.getRechargeId());
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setRegisterId(rechargeRecord.getRegisterId());
        refundRecord.setPlotNum(rechargeRecord.getPlotNum());
        refundRecord.setRefundAmount(rechargeRecord.getAmount());
        refundRecord.setRefundVolume(rechargeRecord.getRechargeVolume());
        refundRecord.setRefundTime(LocalDateTime.now());
        refundRecord.setRecordId(rechargeRecord.getId());
        refundRecord.setRechargeId(postInfo.getOperatorId());
        refundRecord.setState(1);
        RefundRecord newRecord = refundRecordService.addRefundRecord(refundRecord,postInfo.getOperatorId());
        manualRecordService.add(postInfo.getOperatorId(), IpUtil.getIpAddr(request),2,newRecord.getId());
        return ResponseUtil.ok();
    }

//    查询和显示待审核的退款记录
    @RequestMapping(value = "/findInReviewing", method = RequestMethod.POST)
    public Object list(@RequestBody PostInfo postInfo){
        List<RefundRecord> refundRecords = refundRecordService.findByCondition(postInfo.getRegisterId(),
                1,
                postInfo.getPlotNum(),
                postInfo.getPage()-1,
                postInfo.getLimit()).getContent();
        List<ResultInfo> resultInfos = new ArrayList<>();
        for (RefundRecord refundRecord:
             refundRecords) {
            ResultInfo resultInfo = new ResultInfo();
            Resident resident = residentService.fingByRegisterId(refundRecord.getRegisterId());
            RechargeRecord rechargeRecord = rechargeRecordService.findById(refundRecord.getRecordId());
            resultInfo.setId(refundRecord.getId());
            resultInfo.setRegisterId(refundRecord.getRegisterId());
            resultInfo.setUserName(resident.getUserName());
            resultInfo.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultInfo.setRoomNum(resident.getRoomNum());
            resultInfo.setRechargeTime(localDateTimeToLong(rechargeRecord.getRechargeTime()));
            resultInfo.setRefundAmount(refundRecord.getRefundAmount());
            resultInfo.setRefundVolume(refundRecord.getRefundVolume());
            resultInfo.setRefundTime(localDateTimeToLong(refundRecord.getRefundTime()));
            resultInfo.setRefundName(adminService.findById(refundRecord.getRechargeId()).getUsername());
            resultInfo.setState(refundRecord.getState());
            resultInfos.add(resultInfo);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",refundRecordService.findByCondition(postInfo.getRegisterId(),
                1,
                postInfo.getPlotNum()).size());
        result.put("list",resultInfos);
        return result;
    }

    //    查询和显示已审核的退款记录
    @RequestMapping(value = "/findReviewed", method = RequestMethod.POST)
    public Object reviewed(@RequestBody PostInfo postInfo){
        List<RefundRecord> refundRecords = refundRecordService.findByCondition(postInfo.getRegisterId(),
                0,
                postInfo.getPlotNum(),
                postInfo.getPage()-1,
                postInfo.getLimit()).getContent();
        List<ResultInfo> resultInfos = new ArrayList<>();
        for (RefundRecord refundRecord:
                refundRecords) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setId(refundRecord.getId());
            resultInfo.setRegisterId(refundRecord.getRegisterId());
            Resident resident = residentService.fingByRegisterId(refundRecord.getRegisterId());
            resultInfo.setUserName(resident.getUserName());
            resultInfo.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultInfo.setRoomNum(resident.getRoomNum());
            RechargeRecord rechargeRecord = rechargeRecordService.findById(refundRecord.getRecordId());
            resultInfo.setRechargeTime(localDateTimeToLong(rechargeRecord.getRechargeTime()));
            resultInfo.setRefundAmount(refundRecord.getRefundAmount());
            resultInfo.setRefundVolume(refundRecord.getRefundVolume());
            resultInfo.setRefundTime(localDateTimeToLong(refundRecord.getRefundTime()));
            resultInfo.setRefundName(adminService.findById(refundRecord.getRechargeId()).getUsername());
            resultInfo.setCheckTime(localDateTimeToLong(refundRecord.getSafeChangedTime()));
            resultInfo.setCheckName(adminService.findById(refundRecord.getSafeChangedUserid()).getUsername());
            resultInfo.setState(refundRecord.getState());
            resultInfos.add(resultInfo);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",refundRecordService.findByCondition(postInfo.getRegisterId(),
                0,
                postInfo.getPlotNum()).size());
        result.put("list",resultInfos);
        return result;
    }



//    通过订单id查询退款记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RefundRecord findById(@RequestParam Integer id){
        return refundRecordService.findById(id);
    }



    //    审核退款记录
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody List<ReviewState> reviewStates,
                         @RequestParam Integer operatorId,
                         @RequestParam String ip) throws  CloneNotSupportedException {
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



    private static class PostInfo{
        private Integer operatorId;
        private Integer rechargeId;
        private String plotNum;
        private Integer page;
        private Integer limit;
        private String registerId;

        public Integer getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(Integer operatorId) {
            this.operatorId = operatorId;
        }

        public Integer getRechargeId() {
            return rechargeId;
        }

        public void setRechargeId(Integer rechargeId) {
            this.rechargeId = rechargeId;
        }

        public String getPlotNum() {
            return plotNum;
        }

        public void setPlotNum(String plotNum) {
            this.plotNum = plotNum;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }
    }

    private static class ResultInfo{
        private Integer id;
        private String registerId;
        private String userName;
        private String addressDtl;
        private String roomNum;
        private Long rechargeTime;
        private Integer refundAmount;
        private BigDecimal refundVolume;
        private Long refundTime;
        private String refundName;
        private Integer state;
        private String checkName;
        private Long checkTime;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getAddressDtl() {
            return addressDtl;
        }

        public void setAddressDtl(String addressDtl) {
            this.addressDtl = addressDtl;
        }


        public Integer getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(Integer refundAmount) {
            this.refundAmount = refundAmount;
        }

        public BigDecimal getRefundVolume() {
            return refundVolume;
        }

        public void setRefundVolume(BigDecimal refundVolume) {
            this.refundVolume = refundVolume;
        }

        public String getRefundName() {
            return refundName;
        }

        public void setRefundName(String refundName) {
            this.refundName = refundName;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public String getCheckName() {
            return checkName;
        }

        public void setCheckName(String checkName) {
            this.checkName = checkName;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }

        public Long getRechargeTime() {
            return rechargeTime;
        }

        public void setRechargeTime(Long rechargeTime) {
            this.rechargeTime = rechargeTime;
        }

        public Long getRefundTime() {
            return refundTime;
        }

        public void setRefundTime(Long refundTime) {
            this.refundTime = refundTime;
        }

        public Long getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(Long checkTime) {
            this.checkTime = checkTime;
        }
    }

    private Long localDateTimeToLong(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000;
    }

}
