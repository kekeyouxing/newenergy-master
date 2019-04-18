package newenergy.wx.product.controller;

import newenergy.core.service.WxProductService;
import newenergy.wx.product.manager.UserTokenManager;
import newenergy.wx.product.pojo.SNSUserInfo;
import newenergy.wx.product.pojo.UserToken;
import newenergy.wx.product.pojo.WeixinOauth2Token;
import newenergy.wx.product.util.AdvancedUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信网页授权
 * @author yangq
 * @date 2019-04-18
 */
@RestController
@RequestMapping("/wx/OAuth")
public class OAuthController {

    @Autowired
    private WxProductService wxProductService;

//    private String appId = wxProductService.getWxProductConfig().getAppId();
//    private String appSecret = wxProductService.getWxProductConfig().getAppSecret();
    @GetMapping("query")
    public void doQuery(@RequestParam(value = "code")String code) throws ServletException,IOException {
//        request.setCharacterEncoding("gb2312");
        if (!"authdeny".equals(code)){
            String appId = wxProductService.getWxProductConfig().getAppId();
            String appSecret = wxProductService.getWxProductConfig().getAppSecret();
            //获取网页授权access_token
            WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2AccessToken(appId,appSecret,code);
            //网页授权接口访问凭证
            String accessToken = weixinOauth2Token.getAccessToken();
            //用户标识
            String openId = weixinOauth2Token.getOpenId();
            //获取用户信息
            SNSUserInfo snsUserInfo = AdvancedUtil.getSNSUserInfo(accessToken,openId);

            Map<Object,Object> result = new HashMap<>();
            UserToken userToken = UserTokenManager.generateToken(openId);
            result.put("token",userToken.getToken());
            result.put("userInfo",snsUserInfo);
        }
    }
}
