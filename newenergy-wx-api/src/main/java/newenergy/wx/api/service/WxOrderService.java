package newenergy.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundQueryRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
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
        order.setOrderSn(rechargeRecordService.generateOrderSn());
        order.setRechargeVolume(recharge_volumn);
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
            result = wxPayService.createOrder(orderRequest);
//            rechargeRecordService.addRechargeRecord(order,null);//改为orderMap
            //判断是否有相同订单号
            while(orderMap.containsKey(order.getOrderSn())){
                order.setOrderSn(rechargeRecordService.generateOrderSn());
            }
            orderMap.put(order.getOrderSn(),order);
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
//        RechargeRecord order = rechargeRecordService.findBySn(orderSn);//改为orderMap
        RechargeRecord order = orderMap.get(orderSn);

        if (order == null){
            return WxPayNotifyResponse.fail("订单不存在 sn="+orderSn);
        }
        logger.info("<payNotify> order.orderSn:"+order.getOrderSn());

        //检查这个订单是否被处理过

        //检查支付订单金额
        if(!totalFee.equals(order.getAmount())){
            logger.info("<payNotify> retAmount, amount : " +totalFee+","+order.getAmount());
            return WxPayNotifyResponse.fail(order.getOrderSn()+":支付金额不符合 totalFee="+totalFee);
        }

        order.setTransactionId(payId);
        order.setRechargeTime(LocalDateTime.now());

//        order = rechargeRecordService.updateRechargeRecord(order,null);//改为添加
        order = rechargeRecordService.addRechargeRecord(order,null);
        int recordId = order.getId();
        logger.info("<payNotify> recordId : " + recordId);
        //添加新增水量记录
        extraWaterService.add(order.getRegisterId(),order.getRechargeVolume(),recordId,order.getAmount());
        return WxPayNotifyResponse.success("处理成功");
    }


    public Object refund(String body){
        Integer orderId = JacksonUtil.parseInteger(body,"orderId");
        if (orderId==null){
            return ResponseUtil.badArgument();
        }
        RefundRecord order = refundRecordService.findById(orderId);
        if (order == null){
            return ResponseUtil.badArgument();
        }
        Integer refundFee = new BigDecimal(order.getRefundAmount()).multiply(new BigDecimal(100)).intValue();
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();

        RechargeRecord rechargeRecord = rechargeRecordService.findById(order.getRecordId());
        if (rechargeRecord == null) return ResponseUtil.badArgument();

        wxPayRefundRequest.setOutTradeNo(rechargeRecord.getOrderSn());
        wxPayRefundRequest.setOutRefundNo("refund_"+rechargeRecord.getOrderSn());
        Integer totalFee = new BigDecimal(rechargeRecord.getAmount()).multiply(new BigDecimal(100)).intValue();
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(refundFee);

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
        return ResponseUtil.ok();
    }

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

        return WxPayNotifyResponse.success("成功");
    }
}
