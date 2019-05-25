package newenergy.wx.api.controller;

import newenergy.wx.api.service.WxCommonService;
import newenergy.wx.api.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 微信充值
 * @author yangq
 */
@RestController
@RequestMapping("/wx/order")
public class WxOrderController {
    @Autowired
    private WxOrderService wxOrderService;
    @Autowired
    private WxCommonService wxCommonService;

    /**
     * @author yangq
     * @param body
     * @param request
     * @return
     */
//    @PostMapping("submit")
//    public Object submit(@OAuthUser String openId, @Recharge String nickname, @RequestBody String body, HttpServletRequest request){
//        return wxOrderService.submit(openId,nickname,body,request);
//    }
    @PostMapping("submit")
    public Object submit(@RequestBody String body, HttpServletRequest request){
        return wxOrderService.submit(body,request);
    }

    @PostMapping("pay-notify")
    public Object payNotify(HttpServletRequest request){
        return wxOrderService.payNotify(request);
    }

    @PostMapping("refund")
    public Object refund(@RequestBody Map<String,Object> body){

        return wxOrderService.refund(body);
    }

    @PostMapping("refund-notify")
    public Object refundNotify(HttpServletRequest request){
        return wxOrderService.refundNotify(request);
    }

    /**
     * @author yangq
     * @param body
     * @return 注册号对应用户名
     */
    @PostMapping("query")
    public Object queryUser(@RequestBody String body){
        return wxCommonService.query(body);
    }
}
