package newenergy.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RefundRecord;
import newenergy.db.service.*;
import newenergy.wx.api.util.IpUtil;
import newenergy.wx.product.manager.UserTokenManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static newenergy.wx.api.util.WxResponseCode.ORDER_PAY_FAIL;
import static newenergy.wx.api.util.WxResponseCode.ORDER_REFUND_FAILED;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WxOrderService {
    private final Log logger = LogFactory.getLog(WxOrderService.class);

    private String wxPayNotify = "http://hgdr.top/wx/order/pay-notify";
    private String wxRefundNotify = "http://hgdr.top/wx/order/refund-notify";

    private static Map<String,RechargeRecord> orderMap = new HashMap<>();

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private ExtraWaterService extraWaterService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CorrPlotService corrPlotService;

    @Autowired
    private RefundRecordService refundRecordService;

    /**
     * 提交充值申请
     *    该接口调用“统一下单”接口，并拼装发起支付请求需要的参数.
     *    详见https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_5
     * @author yangq
     *<p>
     *     1.创建订单并生成商户订单号
     *     2.保存充值金额与之后充值成功之后比较金额是否匹配
     *</p>
     * @param body
     * @param request
     * @return result{"appId":"wx2421b1c4370ec43b",     //公众号名称，由商户传入
     *                  "timeStamp":"1395712654",         //时间戳，自1970年以来的秒数
     *                  "nonceStr":"e61463f8efa94090b1f366cccfbbb444", //随机串
     *                  "packageValue":"prepay_id=u802345jgfjsdfgsdg888",
     *                  "signType":"MD5",         //微信签名方式：
     *                  "paySign":"70EA570631E4BB79628FBCA90534C63FF7FADD89"  //微信签名}
     *                          state为0代表正常订单（已支付），1代表作废订单（已退款），2代表充值中订单（已预支付）
     *                          reviewState为0代表待审核订单，1代表已通过订单，2代表未通过订单（批量充值订单需要分辨这个字段）
     *                          delegate为0代表非代充（即微信充值），为1代表代充（即批量充值）
     */
    @Transactional
    public Object submit(String body,HttpServletRequest request){
        if(body == null){
            return ResponseUtil.badArgument();
        }
        //通过token验证openid，保证本人操作
        String token = JacksonUtil.parseString(body,"token");
        String openid = UserTokenManager.getOpenId(token);
        String nickname = UserTokenManager.getNickname(token);
        logger.info("<submit> nickname:" + nickname);
        if (openid == null || openid.isEmpty() || nickname == null || nickname.isEmpty()){
            return ResponseUtil.unauthz();
        }

//        String openid = JacksonUtil.parseString(body,"openid");
        String amount = JacksonUtil.parseString(body,"money");
        String deviceid = JacksonUtil.parseString(body,"registerId");
        if (amount == null || deviceid == null){
            return ResponseUtil.badArgument();
        }
        //充值金额转化为分
        BigDecimal acturalAmount = new BigDecimal(amount).multiply(new BigDecimal(100));

        RechargeRecord order = null;
        order = new RechargeRecord();
        order.setRegisterId(deviceid);
        order.setAmount(acturalAmount.intValue());
        order.setUserName(nickname);
        //有设备号查找小区
        String plot_num = residentService.findPlotNumByRegisterid(deviceid,0);
//        Double plot_factor = rechargeRecordService.findByPlotNum(plot_num);
        //由小区查找充值系数就（元每吨）
        BigDecimal plot_factor = corrPlotService.findPlotFacByPlotNum(plot_num);
//        Double recharge_volumn = acturalAmount.doubleValue()*plot_factor;

        //充值流量（充值金额（元）/充值系数（元/吨））这里暂时用分代替元进行测试
        BigDecimal recharge_volumn = acturalAmount.divide(plot_factor,3,RoundingMode.HALF_DOWN);
//        BigDecimal recharge_volumn = acturalAmount.divide(plot_factor.multiply(new BigDecimal(100)),3,RoundingMode.HALF_DOWN);

        //生成商户订单号
        String orderSn = rechargeRecordService.generateOrderSn();
        while(rechargeRecordService.findBySn(orderSn)!=null){
            orderSn = rechargeRecordService.generateOrderSn();
        }
        order.setOrderSn(orderSn);
        order.setRechargeVolume(recharge_volumn);
        order.setPlotNum(plot_num);
        //设为充值中
        order.setState(2);
        //设为待审核
        order.setReviewState(0);
        WxPayMpOrderResult result = null;
        try{
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(order.getOrderSn());
            orderRequest.setOpenid(openid);
            //设置商品名称
            orderRequest.setBody("订单："+order.getOrderSn());
            // 将充值金额转换为分，微信接口需要以分为单位
//            int fee = acturalAmount.multiply(new BigDecimal(100)).intValue();
            int fee = acturalAmount.intValue();
            //设置充值金额（分）
            orderRequest.setTotalFee(fee);
            //设置充值设备的ip
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            orderRequest.setNotifyUrl(wxPayNotify);
            result = wxPayService.createOrder(orderRequest);

            rechargeRecordService.addRechargeRecord(order,null);
//            //判断是否有相同订单号
//            while(orderMap.containsKey(order.getOrderSn())){
//                order.setOrderSn(rechargeRecordService.generateOrderSn());
//            }
//            orderMap.put(order.getOrderSn(),order);
//            String prepayId = result.getPackageValue();
//            prepayId = prepayId.replace("prepay_id=","");

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }
        logger.info("<submit> result:"+result.toString());

        return ResponseUtil.ok(result);
    }

    /**
     * 充值完成的回调
     *
     * @author yangq
     * @param request
     * @return
     * state为0代表正常订单（已支付），1代表作废订单（已退款），2代表充值中订单（已预支付）
     * reviewState为0代表待审核订单，1代表已通过订单，2代表未通过订单（批量充值订单需要分辨这个字段）
     * delegate为0代表非代充（即微信充值），为1代表代充（即批量充值）
     */
    @Transactional
    public Object payNotify(HttpServletRequest request){
        String xmlResult = null;
        try{
            xmlResult = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
        }catch(IOException e){
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        WxPayOrderNotifyResult result = null;
        try{
            result = wxPayService.parseOrderNotifyResult(xmlResult);
        }catch (WxPayException e){
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }

        logger.info("处理腾讯支付平台的订单支付");
        logger.info(result);
        //获取充值成功结果的商户订单号，待后续与订单中对比
        String orderSn = result.getOutTradeNo();
        //订单的交易号，可用于退款操作
        String payId = result.getTransactionId();

//        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
        Integer totalFee = result.getTotalFee();

        logger.info("<payNotify> orderSn:"+orderSn);
        RechargeRecord order = rechargeRecordService.findBySn(orderSn);//改为orderMap
//        RechargeRecord order = orderMap.get(orderSn);
        //判断订单是否存在
        if (order == null){
            return WxPayNotifyResponse.fail("订单不存在 sn="+orderSn);
        }
        logger.info("<payNotify> order.orderSn:"+order.getOrderSn());

        //检查这个订单是否被处理过(因为微信会发送多次请求)
        if (order.getTransactionId() != null || order.getState() == 0) return WxPayNotifyResponse.success("订单已经处理成功!");
        //检查支付订单金额
        if(!totalFee.equals(order.getAmount())){
            logger.info("<payNotify> retAmount, amount : " +totalFee+","+order.getAmount());
            return WxPayNotifyResponse.fail(order.getOrderSn()+":支付金额不符合 totalFee="+totalFee);
        }

        order.setTransactionId(payId);
        order.setRechargeTime(LocalDateTime.now());
        order.setState(0);
        order.setDelegate(0);
        order.setReviewState(1);
        order = rechargeRecordService.updateRechargeRecord(order,null);//改为添加
//        order = rechargeRecordService.addRechargeRecord(order,null);
        int recordId = order.getId();
        logger.info("<payNotify> recordId : " + recordId);
        //添加新增水量记录
        extraWaterService.add(order.getRegisterId(),order.getRechargeVolume(),recordId,order.getAmount());
        return WxPayNotifyResponse.success("处理成功");
    }

    /**
     * 处理退款申请
     * 详见 https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
     * @author yangq
     * @param body {"orderId":"6"}
     * @return Map<String,object>
     *      退款记录状态：refundState{
     *               0:退款成功
     *               4：审核通过退款中
     *               5：审核通过退款失败
     *           }
            充值记录状态：orderState{
     *               0: 正常
     *               1：作废
     *           }
     */
    @Transactional
    public Object refund(Map<String,Object> body){
//        Integer orderId = JacksonUtil.parseInteger(body,"orderId");
        //退款订单ID
        String orderIdStr = (String)body.get("orderId");
        if(orderIdStr == null) return ResponseUtil.badArgument();
        Integer orderId = Integer.valueOf(orderIdStr);
        if (orderId==null){
            return ResponseUtil.badArgument();
        }
        RefundRecord order = refundRecordService.findById(orderId);
        if (order == null){
            return ResponseUtil.badArgument();
        }
//        Integer refundFee = new BigDecimal(order.getRefundAmount()).multiply(new BigDecimal(100)).intValue();
        //退款金额
        Integer refundFee = order.getRefundAmount();
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
//查询对应充值记录
        RechargeRecord rechargeRecord = rechargeRecordService.findById(order.getRecordId());
        //判断是否存在订单以及是否已退款
        if (rechargeRecord == null || rechargeRecord.getState() == 1) return ResponseUtil.badArgument();

//        wxPayRefundRequest.setOutTradeNo(rechargeRecord.getOrderSn());
        //退款订单号（商家生成）
        String outRefundNo = "refund_"+rechargeRecord.getOrderSn();
        wxPayRefundRequest.setOutRefundNo(outRefundNo);
//        Integer totalFee = new BigDecimal(rechargeRecord.getAmount()).multiply(new BigDecimal(100)).intValue();
        //订单总金额（支付时的）
        Integer totalFee = rechargeRecord.getAmount();

        wxPayRefundRequest.setTotalFee(totalFee);
        //退款金额（退款时的）
        wxPayRefundRequest.setRefundFee(refundFee);
        wxPayRefundRequest.setTransactionId(rechargeRecord.getTransactionId());
        wxPayRefundRequest.setNotifyUrl(wxRefundNotify);

        WxPayRefundResult wxPayRefundResult = null;
        try{
            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
        }catch (WxPayException e){
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_REFUND_FAILED,"订单退款失败");
        }
        if(!wxPayRefundResult.getReturnCode().equals("SUCCESS")){
            logger.info("refund fail:"+wxPayRefundResult.getReturnMsg());
            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
        }
        order.setOutRefundNo(outRefundNo);
        order.setState(4);
        refundRecordService.updateRefundRecord(order,order.getSafeChangedUserid());
        return ResponseUtil.ok();
    }

    /**
     * 退款成功的回调
     * @param request
     * @return
     * 退款记录状态：refundState{
     *     0:退款成功
     *     4：审核通过退款中
     *     5：审核通过退款失败
     * }
     * 充值记录状态：orderState{
     *     0:正常
     *     1：作废
     * }
     */
    @Transactional
    public Object refundNotify(HttpServletRequest request){
        String xmlResult = null;
        try{
            xmlResult = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
        }catch(IOException e){
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }
        WxPayRefundNotifyResult result = null;
        try{
            result = wxPayService.parseRefundNotifyResult(xmlResult);
        }catch (WxPayException e){
            e.printStackTrace();
            return WxPayNotifyResponse.fail(e.getMessage());
        }
        //添加退款成功逻辑
        Integer refundState = 4;
        Integer orderState = 0;
        WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();
        //订单号
        String outTradeNo = reqInfo.getOutTradeNo();
        //微信退款单号
        String outRefundNo = reqInfo.getOutRefundNo();
        //订单金额
        Integer totalFee = reqInfo.getTotalFee();
        //实际退款金额（单位分）
        Integer refundFee = reqInfo.getSettlementRefundFee();
        //退款状态
        String refundStatus = reqInfo.getRefundStatus();
        if (refundStatus == "CHANGE" || refundStatus== "REFUNDCLOSE") {
            refundState = 5;
            orderState = 0;
        }
        else {
            refundState = 0;
            orderState = 1;
        }
//        //退款成功时间
//        String successTime = reqInfo.getSuccessTime();
//        //退款账户
//        String refundRecvAccount = reqInfo.getRefundRecvAccout();
//        //退款资金来源
//        String refundRequestSource = reqInfo.getRefundRequestSource();

        RechargeRecord order = rechargeRecordService.findBySn(outTradeNo);
        RefundRecord refundRecord = refundRecordService.findBySn(outRefundNo);
        //判断订单是否存在
        if (order == null || refundRecord == null) return WxPayNotifyResponse.fail("订单不存在 sn="+outTradeNo);
        //判断订单是否被处理过，因为微信会发起多次请求
        if (order.getState() == 1 || refundRecord.getState() == 0 || refundRecord.getState() == 5) return WxPayNotifyResponse.success("订单已经处理成功");
//      实际退款金额（单位分）
//        Integer refundAmount = new BigDecimal(BaseWxPayResult.fenToYuan(refundFee)).intValue();
        Integer refundAmount = refundFee;
        refundRecord.setRefundAmount(refundAmount);
//      小区编号
//        String plot_num = residentService.findPlotNumByRegisterid(refundRecord.getRegisterId(),0);
        String plot_num = order.getPlotNum();

        //充值系数（元/吨 ~ 分/吨，这里暂时分当作元进行测试）
//        BigDecimal plot_factor = corrPlotService.findPlotFacByPlotNum(plot_num).multiply(new BigDecimal(100));
        BigDecimal plot_factor = corrPlotService.findPlotFacByPlotNum(plot_num);
        //实际退款流量
        BigDecimal refundVolume = new BigDecimal(refundAmount).divide(plot_factor,3,RoundingMode.HALF_DOWN);
        refundRecord.setRefundVolume(refundVolume);

        //实际退款时间
        refundRecord.setRefundTime(LocalDateTime.now());

        refundRecord.setState(refundState);
        Integer userId = refundRecord.getSafeChangedUserid();
        refundRecordService.updateRefundRecord(refundRecord,userId);

        order.setState(orderState);
        rechargeRecordService.updateRechargeRecord(order,userId);

        return WxPayNotifyResponse.success("成功");
    }
}
