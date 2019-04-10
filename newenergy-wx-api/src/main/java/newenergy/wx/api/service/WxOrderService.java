package newenergy.wx.api.service;

import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyOrder;
import newenergy.db.domain.Resident;
import newenergy.db.service.NewenergyOrderService;
import newenergy.wx.api.util.IpUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static newenergy.wx.api.util.WxResponseCode.ORDER_PAY_FAIL;

@Service
public class WxOrderService {
    private final Log logger = LogFactory.getLog(WxOrderService.class);

    @Autowired
    private NewenergyOrderService newenergyOrderService;
    @Autowired
    private WxPayService wxPayService;

    @Transactional
    public Object submit(String body,HttpServletRequest request){
        if(body == null){
            return ResponseUtil.badArgument();
        }
        String openid = JacksonUtil.parseString(body,"openid");
        String amount = JacksonUtil.parseString(body,"amount");
        String deviceid = JacksonUtil.parseString(body,"deviceid");
        BigDecimal acturalAmount = new BigDecimal(amount);
//      将充值金额转换为分
        int fee = acturalAmount.multiply(new BigDecimal(100)).intValue();
        Integer orderId = null;
        NewenergyOrder order = null;
        order = new NewenergyOrder();
        order.setRegister_id(deviceid);
        order.setAmount(acturalAmount.intValue());
        String plot_num = newenergyOrderService.findByRegisterId(deviceid);
        Double plot_factor = newenergyOrderService.findByPlotNum(plot_num);
        Double recharge_volumn = acturalAmount.doubleValue()/plot_factor;
        order.setOrderSn(newenergyOrderService.generateOrderSn());
        WxPayMpOrderResult result = null;
        try{
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo(order.getOrderSn());
            orderRequest.setOpenid(openid);
            orderRequest.setBody("订单："+order.getOrderSn());
            orderRequest.setTotalFee(fee);
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddr(request));
            result = wxPayService.createOrder(orderRequest);
            newenergyOrderService.add(order,null);
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
        NewenergyOrder order = newenergyOrderService.findBySn(orderSn);
        if (order == null){
            return WxPayNotifyResponse.fail("订单不存在 sn="+orderSn);
        }

        //检查这个订单是否被处理过

        //检查支付订单金额
        if(!totalFee.equals(order.getAmount().toString())){
            return WxPayNotifyResponse.fail(order.getOrderSn()+":支付金额不符合 totalFee="+totalFee);
        }

        order.setTransaction_id(payId);
        order.setRecharge_time(LocalDateTime.now());
        newenergyOrderService.update(order,null);

        //TODO 发送邮件和短信通知，这里采用异步发送

        return ResponseUtil.ok();
    }
}
