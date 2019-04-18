package newenergy.wx.api.web;

import newenergy.wx.annotation.OAuthUser;
import newenergy.wx.api.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信充值
 * @author yangq
 */
@RestController
@RequestMapping("/wx/order")
public class WxOrderController {
    @Autowired
    private WxOrderService wxOrderService;

    /**
     * @author yangq
     * 添加@OAuthUser注解
     * @param openId
     * @param body
     * @param request
     * @return
     */
    @PostMapping("submit")
    public Object submit(@OAuthUser String openId, @RequestBody String body, HttpServletRequest request){
        return wxOrderService.submit(openId,body,request);
    }

    @PostMapping("pay-notify")
    public Object payNotify(HttpServletRequest request, HttpServletResponse response){
        return wxOrderService.payNotify(request,response);
    }
}
