package newenergy.wx.api.service;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyOrder;
import newenergy.db.domain.Resident;
import newenergy.db.service.NewenergyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static newenergy.wx.api.util.WxResponseCode.ORDER_PAY_FAIL;

@Service
public class WxOrderService {

    @Autowired
    private NewenergyOrderService newenergyOrderService;

    @Transactional
    public Object submit(String body){
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

        WxPayMpOrderResult result = null;
        try{
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
            orderRequest.setOutTradeNo();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail(ORDER_PAY_FAIL, "订单不能支付");
        }
        return ResponseUtil.ok();
    }
    @Transactional
    public Object prepay(String body, HttpServletRequest request){
        return ResponseUtil.ok();
    }
}
