package newenergy.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.ExtraWater;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.service.CorrPlotService;
import newenergy.db.service.ExtraWaterService;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.ResidentService;
import newenergy.wx.api.util.IpUtil;
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
import java.time.LocalDateTime;

import static newenergy.wx.api.util.WxResponseCode.ORDER_PAY_FAIL;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WxOrderService {
    private final Log logger = LogFactory.getLog(WxOrderService.class);

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

    @Transactional
    public Object submit(String body,HttpServletRequest request){
        if(body == null){
            return ResponseUtil.badArgument();
        }
        String openid = JacksonUtil.parseString(body,"openid");
        String amount = JacksonUtil.parseString(body,"amount");
        String deviceid = JacksonUtil.parseString(body,"deviceid");
        String nickName = JacksonUtil.parseString(body,"nickName");
        if (amount == null || deviceid == null){
            return ResponseUtil.badArgument();
        }
        BigDecimal acturalAmount = new BigDecimal(amount);
//      将充值金额转换为分
        int fee = acturalAmount.multiply(new BigDecimal(100)).intValue();
        RechargeRecord order = null;
        order = new RechargeRecord();
        order.setRegisterId(deviceid);
        order.setAmount(acturalAmount.intValue());
        String plot_num = residentService.findPlotNumByRegisterid(deviceid,0);
//        Double plot_factor = rechargeRecordService.findByPlotNum(plot_num);
        BigDecimal plot_factor = corrPlotService.findPlotFacByPlotNum(plot_num);
//        Double recharge_volumn = acturalAmount.doubleValue()*plot_factor;
        BigDecimal recharge_volumn = acturalAmount.multiply(plot_factor);
        order.setOrderSn(rechargeRecordService.generateOrderSn());
        order.setRechargeVolume(recharge_volumn);
        WxPayMpOrderResult result = null;
        try{
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(order.getOrderSn());
            orderRequest.setOpenid(openid);
            orderRequest.setBody("订单："+order.getOrderSn());
            orderRequest.setTotalFee(fee);
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            result = wxPayService.createOrder(orderRequest);
            rechargeRecordService.addRechargeRecord(order,null);
//            String prepayId = result.getPackageValue();
//            prepayId = prepayId.replace("prepay_id=","");

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }
        return ResponseUtil.ok(result);
    }

    @Transactional
    public Object payNotify(HttpServletRequest request, HttpServletResponse response){
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

        String orderSn = result.getOutTradeNo();
        String payId = result.getTransactionId();

        String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
        RechargeRecord order = rechargeRecordService.findBySn(orderSn);
        if (order == null){
            return WxPayNotifyResponse.fail("订单不存在 sn="+orderSn);
        }

        //检查这个订单是否被处理过

        //检查支付订单金额
        if(!totalFee.equals(order.getAmount().toString())){
            return WxPayNotifyResponse.fail(order.getOrderSn()+":支付金额不符合 totalFee="+totalFee);
        }

        order.setTransactionId(payId);
        order.setRechargeTime(LocalDateTime.now());
        rechargeRecordService.updateRechargeRecord(order,null);

        //TODO 发送邮件和短信通知，这里采用异步发送

        ExtraWater extraWater = null;
        extraWater = new ExtraWater(order.getRegisterId(),order.getRechargeVolume(),null);
//        extraWater = new ExtraWater(order.getRegisterId(),new BigDecimal(order.getRechargeVolume()),null);
//        extraWater.setRegisterId(order.getRegister_id());
//        extraWater.setRecord_id(null);
//        extraWater.setAdd_volume(new BigDecimal(order.getRecharge_volume()));
        extraWaterService.add(extraWater);
        return WxPayNotifyResponse.success("处理成功");
    }

    public Object refund(String body){
        Integer orderId = JacksonUtil.parseInteger(body,"orderId");
        if (orderId==null){
            return ResponseUtil.badArgument();
        }

        RechargeRecord order = rechargeRecordService.findById(orderId);
        if (order == null){
            return ResponseUtil.badArgument();
        }
        return ResponseUtil.ok();
    }
}
