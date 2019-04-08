package newenergy.wx.api.web;

import newenergy.wx.api.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/wx/order")
public class WxOrderController {
    @Autowired
    private WxOrderService wxOrderService;

    @PostMapping("submit")
    public Object submit(@RequestBody String body,HttpServletRequest request){
        return wxOrderService.submit(body,request);
    }

    @PostMapping("prepay")
    public Object prepay(@RequestBody String body, HttpServletRequest request){
        return wxOrderService.prepay(body,request);
    }
}
