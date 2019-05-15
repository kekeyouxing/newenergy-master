package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
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
@RequestMapping("/admin/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

    @Autowired
    BatchRecordService batchRecordService;

    @Autowired
    RemainWaterService remainWaterService;

    @Autowired
    ExtraWaterService extraWaterService;

    @Autowired
    CorrPlotService corrPlotService;

    @Autowired
    ResidentService residentService;

    @Autowired
    CorrAddressService corrAddressService;

    @Autowired
    NewenergyAdminService adminService;

    @Autowired
    RefundRecordService refundRecordService;


//    根据id查询充值记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RechargeRecord findById(@RequestParam Integer id){
        System.out.println(id);
        return rechargeRecordService.findById(id);
    }

//    查询和显示待审核的批量充值记录
    @RequestMapping(value = "/findInReviewing", method = RequestMethod.POST)
    public Object findInReview(@RequestBody PostInfo postInfo){
        List<BatchRecord> queryResult = batchRecordService.findByConditions(postInfo.getPlotNum(),
                0,
                postInfo.getPage()-1,
                postInfo.getLimit()).getContent();
        List<ResultModel> list = new ArrayList<>();
        for (BatchRecord batchRecord:queryResult
             ) {
                ResultModel resultModel = new ResultModel();
                resultModel.setId(batchRecord.getId());
                resultModel.setPlotDtl(corrPlotService.findByPlotNum(batchRecord.getPlotNum()));
                resultModel.setUsername(adminService.findById(batchRecord.getBatchAdmin()).getRealName());
                resultModel.setRechargeTime(localDateTimeToLong(batchRecord.getRechargeTime()));
                resultModel.setState(batchRecord.getState());
                list.add(resultModel);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",batchRecordService.findByConditions(postInfo.getPlotNum(),
                0).size());
        result.put("list",list);
        return result;
    }

    //    查询某条批量充值对应的充值记录
    @RequestMapping(value = "/findByBatchRecord", method = RequestMethod.POST)
    public Object findByBatchRecordId(@RequestBody PostInfo postInfo){
        List<RechargeRecord> queryResult = rechargeRecordService.findByConditions(postInfo.getBatchRecordId(),
                null,
                null,
                null,
                null,
                postInfo.getPage()-1,
                postInfo.getLimit()).getContent();
        List<ResultModel> list = new ArrayList<>();
        for (RechargeRecord rechargeRecord:queryResult
        ) {
            ResultModel resultModel = new ResultModel();
            resultModel.setId(rechargeRecord.getId());
            resultModel.setRegisterId(rechargeRecord.getRegisterId());
            resultModel.setUsername(residentService.fingByRegisterId(rechargeRecord.getRegisterId()).getUserName());
            Resident resident = residentService.fingByRegisterId(rechargeRecord.getRegisterId());
            resultModel.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultModel.setRoomNum(resident.getRoomNum());
            resultModel.setAmount(rechargeRecord.getAmount());
            resultModel.setReviewState(rechargeRecord.getReviewState());
            resultModel.setRefundState(refundRecordService.haveRefundRecord(rechargeRecord.getId()));
            list.add(resultModel);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",rechargeRecordService.findByConditions(postInfo.getBatchRecordId(),
                null,
                null,
                null,
                null).size());
        result.put("list",list);
        result.put("haveCredential",batchRecordService.queryById(postInfo.getBatchRecordId()).getImgUrl()==null?0:1);
        result.put("amount",batchRecordService.queryById(postInfo.getBatchRecordId()).getAmount());
        return result;
    }

    //    查询和显示已审核的批量充值记录
    @RequestMapping(value = "/findReviewed", method = RequestMethod.POST)
    public Object findReviewed(@RequestBody PostInfo postInfo){
        List<BatchRecord> queryResult = batchRecordService.findByConditions(postInfo.getPlotNum(),
                1,
                postInfo.getPage()-1,
                postInfo.getLimit()).getContent();
        List<ResultModel> list = new ArrayList<>();
        for (BatchRecord batchRecord:queryResult
        ) {
            ResultModel resultModel = new ResultModel();
            resultModel.setId(batchRecord.getId());
            resultModel.setPlotDtl(corrPlotService.findByPlotNum(batchRecord.getPlotNum()));
            resultModel.setUsername(adminService.findById(batchRecord.getBatchAdmin()).getRealName());
            resultModel.setRechargeTime(localDateTimeToLong(batchRecord.getRechargeTime()));
            resultModel.setVerifyTime(localDateTimeToLong(batchRecord.getSafeChangedTime()));
            resultModel.setVerifyUsername(adminService.findById(batchRecord.getSafeChangedUserid()).getRealName());
            resultModel.setState(batchRecord.getState());
            list.add(resultModel);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",batchRecordService.findByConditions(postInfo.getPlotNum(),
                1).size());
        result.put("list",list);
        return result;
    }

    //    某个用户充值记录
    @RequestMapping(value = "/findPersonal", method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    public Object findPersonal(@RequestBody PostInfo postInfo){
        List<RechargeRecord> queryResult = rechargeRecordService.findByConditions(null,
                null,
                postInfo.getRegisterId(),
                null,
                null);
        List<ResultModel> list = new ArrayList<>();
        for (RechargeRecord rechargeRecord:queryResult
        ) {
            ResultModel resultModel = new ResultModel();
            resultModel.setId(rechargeRecord.getId());
            resultModel.setRechargeTime(localDateTimeToLong(rechargeRecord.getRechargeTime()));
            resultModel.setAmount(rechargeRecord.getAmount());
            resultModel.setRechargeVolume(rechargeRecord.getRechargeVolume());
            resultModel.setRemainVolume(rechargeRecord.getRemainVolume());
            resultModel.setUpdateVolume(rechargeRecord.getUpdatedVolume());
            resultModel.setRefundState(refundRecordService.haveRefundRecord(rechargeRecord.getId()));
            list.add(resultModel);
        }
        Map<String,Object> result = new HashMap<>();
        BigDecimal remainWater = new BigDecimal(0);
        if (remainWaterService.findByRegisterId(postInfo.getRegisterId())!=null){
            remainWater = remainWaterService.findByRegisterId(postInfo.getRegisterId()).getRemainVolume();
        }
        result.put("currentRemainVolume",remainWater);
        result.put("list",list);
        return result;
    }

//    根据批量充值id，审核状态，注册id，订单状态查询批量充值记录
  /*  @RequestMapping(value = "/findByConditions", method = RequestMethod.GET)
    public List<RechargeRecord> findByConditions(@RequestParam(required = false) Integer batchRecordId,
                                                 @RequestParam(required = false) Integer reviewState,
                                                 @RequestParam(required = false) String registerId,
                                                 @RequestParam(required = false) Integer state
                                                 ){
        return rechargeRecordService.findByConditions(batchRecordId,reviewState,registerId,state,null);
    }
*/
    //    充值订单审核
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody PostInfo postInfo,
                         HttpServletRequest request,
                         @AdminLoginUser NewenergyAdmin user) throws CloneNotSupportedException {
        for (ReviewState reviewState:postInfo.getList()){
            RechargeRecord rechargeRecord = (RechargeRecord) rechargeRecordService.findById(reviewState.getId()).clone();
            rechargeRecord.setReviewState(reviewState.getReviewState());
            rechargeRecord.setCheckId(user.getId());
            Integer state = 1;
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
                state=2;
            }
            BatchRecord batchRecord = batchRecordService.queryById(rechargeRecordService.findById(reviewState.getId()).getBatchRecordId());
            batchRecord.setState(state);
            batchRecordService.updateBatchRecord(batchRecord,user.getId());
            RechargeRecord newRecord = rechargeRecordService.updateRechargeRecord(rechargeRecord,postInfo.getBatchRecordId());
            manualRecordService.add(user.getId(), IpUtil.getIpAddr(request),1,newRecord.getId());
        }
        Map<String,Integer> state = new HashMap<>();
//        0代表正常、其他代表异常
        state.put("state",0);
        return state;
    }

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

    private static class PostInfo{
        Integer operatorId;
        String plotNum;
        Integer page;
        Integer limit;
        Integer batchRecordId;
        String registerId;
        List<ReviewState> list;

        public Integer getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(Integer operatorId) {
            this.operatorId = operatorId;
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

        public Integer getBatchRecordId() {
            return batchRecordId;
        }

        public void setBatchRecordId(Integer batchRecordId) {
            this.batchRecordId = batchRecordId;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public List<ReviewState> getList() {
            return list;
        }

        public void setList(List<ReviewState> list) {
            this.list = list;
        }
    }

    private static class ResultModel{
        private Integer id;
        private String plotDtl;
        private Long rechargeTime;
        private String username;
        private Integer state;
        private String registerId;
        private String addressDtl;
        private String roomNum;
        private Integer amount;
        private Integer reviewState;
        private Long verifyTime;
        private String verifyUsername;
        private BigDecimal remainVolume;
        private BigDecimal updateVolume;
        private BigDecimal rechargeVolume;
        private Integer refundState;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotDtl() {
            return plotDtl;
        }

        public void setPlotDtl(String plotDtl) {
            this.plotDtl = plotDtl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getAddressDtl() {
            return addressDtl;
        }

        public void setAddressDtl(String addressDtl) {
            this.addressDtl = addressDtl;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public Integer getReviewState() {
            return reviewState;
        }

        public void setReviewState(Integer reviewState) {
            this.reviewState = reviewState;
        }

        public Long getRechargeTime() {
            return rechargeTime;
        }

        public void setRechargeTime(Long rechargeTime) {
            this.rechargeTime = rechargeTime;
        }

        public Long getVerifyTime() {
            return verifyTime;
        }

        public void setVerifyTime(Long verifyTime) {
            this.verifyTime = verifyTime;
        }

        public String getVerifyUsername() {
            return verifyUsername;
        }

        public void setVerifyUsername(String verifyUsername) {
            this.verifyUsername = verifyUsername;
        }

        public BigDecimal getRemainVolume() {
            return remainVolume;
        }

        public void setRemainVolume(BigDecimal remainVolume) {
            this.remainVolume = remainVolume;
        }

        public BigDecimal getUpdateVolume() {
            return updateVolume;
        }

        public void setUpdateVolume(BigDecimal updateVolume) {
            this.updateVolume = updateVolume;
        }

        public BigDecimal getRechargeVolume() {
            return rechargeVolume;
        }

        public void setRechargeVolume(BigDecimal rechargeVolume) {
            this.rechargeVolume = rechargeVolume;
        }

        public Integer getRefundState() {
            return refundState;
        }

        public void setRefundState(Integer refundState) {
            this.refundState = refundState;
        }
    }

    private Long localDateTimeToLong(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000;
    }

}
