package newenergy.admin.controller;


import newenergy.admin.annotation.AdminLoginUser;
import newenergy.admin.background.service.StorageService;
import newenergy.admin.util.IpUtil;
import newenergy.core.pojo.MsgRet;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    StorageService storageService;

    @Value("${server.port}")
    private String port;
    private String sendMsgPrefix = "http://localhost:";
    private String sendMsgSuffix ="/wx/fault/refund";
    private RestTemplate restTemplate = new RestTemplate();
    //    未审核通过的订单发起退款
    @RequestMapping(value = "/addRefund", method = RequestMethod.POST)
    public Object addReview(@RequestBody PostInfo postInfo,
                            HttpServletRequest request,
                            @AdminLoginUser NewenergyAdmin user){
//        state为1代表待审核，0代表审核通过，2代表审核不通过
        RechargeRecord rechargeRecord = rechargeRecordService.findById(postInfo.getRechargeId());
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setRegisterId(rechargeRecord.getRegisterId());
        refundRecord.setPlotNum(rechargeRecord.getPlotNum());
        refundRecord.setRefundAmount(rechargeRecord.getAmount());
        refundRecord.setRefundVolume(rechargeRecord.getRechargeVolume());
        refundRecord.setDelegate(rechargeRecord.getDelegate());
        refundRecord.setRefundTime(LocalDateTime.now());
        refundRecord.setRecordId(rechargeRecord.getId());
        refundRecord.setRechargeId(user.getId());
        refundRecord.setState(1);
        RefundRecord newRecord = refundRecordService.addRefundRecord(refundRecord,user.getId());
        manualRecordService.add(user.getId(), IpUtil.getIpAddr(request),2,newRecord.getId());
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
            resultInfo.setDelegate(refundRecord.getDelegate());
            resultInfo.setUserName(resident.getUserName());
            resultInfo.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultInfo.setRoomNum(resident.getRoomNum());
            resultInfo.setRechargeTime(localDateTimeToLong(rechargeRecord.getRechargeTime()));
            resultInfo.setRefundAmount(refundRecord.getRefundAmount());
            resultInfo.setRefundVolume(refundRecord.getRefundVolume());
            resultInfo.setRefundTime(localDateTimeToLong(refundRecord.getRefundTime()));
            resultInfo.setRefundName(adminService.findById(refundRecord.getRechargeId())==null?""
                    :adminService.findById(refundRecord.getRechargeId()).getRealName());
            resultInfo.setRefundReason(rechargeRecord.getRejectReason());
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
            resultInfo.setDelegate(refundRecord.getDelegate());
            resultInfo.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultInfo.setRoomNum(resident.getRoomNum());
            RechargeRecord rechargeRecord = rechargeRecordService.findById(refundRecord.getRecordId());
            resultInfo.setRechargeTime(localDateTimeToLong(rechargeRecord.getRechargeTime()));
            resultInfo.setRefundAmount(refundRecord.getRefundAmount());
            resultInfo.setRefundVolume(refundRecord.getRefundVolume());
            resultInfo.setRefundTime(localDateTimeToLong(refundRecord.getRefundTime()));
            resultInfo.setRefundName(adminService.findById(refundRecord.getRechargeId())==null?"":
                    adminService.findById(refundRecord.getRechargeId()).getRealName());
            resultInfo.setCheckTime(localDateTimeToLong(refundRecord.getSafeChangedTime()));
            resultInfo.setCheckName(adminService.findById(refundRecord.getCheckId())==null?"":
                    adminService.findById(refundRecord.getCheckId()).getRealName());
            resultInfo.setState(refundRecord.getState());
            resultInfo.setRefundReason(rechargeRecord.getRejectReason());
            resultInfo.setRejectReason(refundRecord.getRejectReason());
            resultInfos.add(resultInfo);
        }
        Map<String,Object> result = new HashMap<>();
        result.put("total",refundRecordService.findByCondition(postInfo.getRegisterId(),
                0,
                postInfo.getPlotNum()).size());
        result.put("list",resultInfos);
        return result;
    }



////    通过订单id查询退款记录
//    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
//    public RefundRecord findById(@RequestParam Integer id){
//        return refundRecordService.findById(id);
//    }
//
//
//
    //    审核退款记录
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    public Object review(@RequestBody PostInfo postInfo,
                         HttpServletRequest request,
                         @AdminLoginUser NewenergyAdmin user) throws  CloneNotSupportedException {
//        state为1代表待审核，0代表审核通过，2代表审核不通过
        for (ReviewState reviewState:postInfo.getList()) {
            RefundRecord refundRecord = (RefundRecord) refundRecordService.findById(reviewState.getId()).clone();
            refundRecord.setState(reviewState.getReviewState());
            refundRecord.setCheckId(user.getId());
            RechargeRecord rechargeRecord = rechargeRecordService.findById(refundRecord.getRecordId());
//            如果审核通过，进一步判断
            if ((reviewState.getReviewState()==0)){
                //应该先发送微信退款请求，返回success之后再添加数据库记录
//                extraWaterService.add(refundRecord.getRegisterId(),
//                        refundRecord.getRefundVolume().multiply(new BigDecimal(-1)),
//                        refundRecord.getId(),
//                        refundRecord.getRefundAmount()*(-1));
//                如果为非代充，即微信充值，发送退款请求
                if (rechargeRecord.getDelegate()==0){
//                    Map<String,Object> requestBody = new HashMap<>();
//                    requestBody.put("orderId",refundRecord.getRecordId());
//                    ErrorMsg e = restTemplate.postForObject(sendMsgPrefix + port + sendMsgSuffix,requestBody, ErrorMsg.class);
////                    如果返回为空或者未返回“成功”，则判断审核失败，失败原因为退款失败
//                    if ((e==null)||(!e.getErrmsg().equals("成功"))){
//                        refundRecord.setState(2);
//                        refundRecord.setRejectReason("微信退款失败");
//                    }
                    storageService.addRefundWater(refundRecord.getId(),refundRecord.getRefundVolume());

                }else if (rechargeRecord.getDelegate()==1){
//                    若为代充，则直接将充值订单作废
                    rechargeRecord.setState(1);
                }
            }else if (reviewState.getReviewState()==2){
//                若审核不通过，则添加审核不通过原因
                refundRecord.setRejectReason(reviewState.getRejectReason());
            }
            rechargeRecordService.updateRechargeRecord(rechargeRecord,user.getId());
            RefundRecord newRecord = refundRecordService.updateRefundRecord(refundRecord,user.getId());
            manualRecordService.add(user.getId(),IpUtil.getIpAddr(request),3,newRecord.getId());
        }
        Map<String,Integer> state = new HashMap<>();
//        0代表正常、其他代表异常
        state.put("state",0);
        return state;
    }

    private static class ReviewState{
        private Integer id;
        private Integer reviewState;
        private String rejectReason;

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }

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
        private Integer operatorId;
        private Integer rechargeId;
        private String plotNum;
        private Integer page;
        private Integer limit;
        private String registerId;
        List<ReviewState> list;

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

        public List<ReviewState> getList() {
            return list;
        }

        public void setList(List<ReviewState> list) {
            this.list = list;
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
        private String refundReason;
        private String rejectReason;
        private Integer delegate;

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

        public String getRefundReason() {
            return refundReason;
        }

        public void setRefundReason(String refundReason) {
            this.refundReason = refundReason;
        }

        public String getRejectReason() {
            return rejectReason;
        }

        public void setRejectReason(String rejectReason) {
            this.rejectReason = rejectReason;
        }

        public Integer getDelegate() {
            return delegate;
        }

        public void setDelegate(Integer delegate) {
            this.delegate = delegate;
        }
    }

    private static class ErrorMsg{
        private Integer errno;
        private String errmsg;

        public Integer getErrno() {
            return errno;
        }

        public void setErrno(Integer errno) {
            this.errno = errno;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }
    }

    private Long localDateTimeToLong(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()/1000;
    }

}
