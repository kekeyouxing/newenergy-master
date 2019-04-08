package newenergy.wx.api.service;

import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyOrder;
import newenergy.db.domain.Resident;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service
public class WxOrderService {
    @Transactional
    public Object submit(String body){
        if(body == null){
            return ResponseUtil.badArgument();
        }
        String openid = JacksonUtil.parseString(body,"openid");
        String amount = JacksonUtil.parseString(body,"amount");
        Integer deviceid = JacksonUtil.parseInteger(body,"deviceid");
        int fee = new BigDecimal(amount).multiply(new BigDecimal(100)).intValue();
        Integer orderId = null;
        NewenergyOrder order = null;
        order = new NewenergyOrder();
        order.setRegister_id(deviceid);
        order.setAmount(fee);
        Resident user = null;
        user = new Resident();
        String plot_num = user.getPlot_num();
        return ResponseUtil.ok();
    }
    @Transactional
    public Object prepay(String body, HttpServletRequest request){
        return ResponseUtil.ok();
    }
}
