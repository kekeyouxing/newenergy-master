package newenergy.wx.product.controller;

import newenergy.core.config.WxProductConfig;
import newenergy.core.service.WxProductService;
import newenergy.wx.product.manager.UserTokenManager;
import newenergy.wx.product.pojo.SNSUserInfo;
import newenergy.wx.product.pojo.UserToken;
import newenergy.wx.product.pojo.WeixinOauth2Token;
import newenergy.wx.product.util.AdvancedUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Value("${quwen.wx.app-id}")
    private String appId;
    @Value("${quwen.wx.app-secret}")
    private String appSecret;
//    @Value("${corey.domain}")
//    @Autowired
//    private WxProductService wxProductService;

//    private String appId = wxProductService.getWxProductConfig().getAppId();
//    private String appSecret = wxProductService.getWxProductConfig().getAppSecret();
    @GetMapping("userBind")
    public void doBind(@RequestParam(value = "code")String code, HttpServletResponse response) throws IOException {
//        request.setCharacterEncoding("gb2312");
        String token = null;
        if (!"authdeny".equals(code)){
//            String appId = wxProductService.getWxProductConfig().getAppId();
//            String appSecret = wxProductService.getWxProductConfig().getAppSecret();

            //获取网页授权access_token
            WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2AccessToken(appId,appSecret,code);
//            WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2AccessToken(wxProductConfig,code);
            //网页授权接口访问凭证
            String accessToken = weixinOauth2Token.getAccessToken();
            //用户标识
            String openId = weixinOauth2Token.getOpenId();
            //获取用户信息
            SNSUserInfo snsUserInfo = AdvancedUtil.getSNSUserInfo(accessToken,openId);
            //获取用户昵称
            String nickname = snsUserInfo.getNickname();


//            Map<Object,Object> result = new HashMap<>();
            UserToken userToken = UserTokenManager.generateTokenWithOpenId(openId);
            token = userToken.getToken();
//            result.put("token",userToken.getToken());
//            result.put("userInfo",snsUserInfo);
//            request.getRequestDispatcher("../../static/index.html").forward(request,response);
        }
        response.sendRedirect("/#/wx/userBind?token="+token);
//        response.sendRedirect("http://192.168.199.105:8080/#/userBind?token="+token);
//        request.getRequestDispatcher("/#/userBind").forward(request,response);
    }

    @GetMapping("recharge")
    public void doRecharge(@RequestParam(value = "code")String code, HttpServletResponse response) throws IOException {
        String token = null;
        if (!"authdeny".equals(code)){
//            String appId = "wx56acef520e1b0030";
//            String appSecret = "3848a4749c6337c86f3dcf42b3d21d2a";
//            String appId = wxProductService.getWxProductConfig().getAppId();
//            String appSecret = wxProductService.getWxProductConfig().getAppSecret();

            //获取网页授权access_token
            WeixinOauth2Token weixinOauth2Token = AdvancedUtil.getOauth2AccessToken(appId,appSecret,code);
            //网页授权接口访问凭证
            String accessToken = weixinOauth2Token.getAccessToken();
            //用户标识
            String openId = weixinOauth2Token.getOpenId();
            //获取用户信息
            SNSUserInfo snsUserInfo = AdvancedUtil.getSNSUserInfo(accessToken,openId);
            //获取用户昵称
            String nickname = snsUserInfo.getNickname();
//            String openId = "otRs353-307-p8aHZByLNAs3j03k";
//            String nickname = "zany";

            UserToken userToken = UserTokenManager.generateTokenWithNinameAndOpenId(openId,nickname);
            token = userToken.getToken();
        }
        response.sendRedirect("/#/wx/recharge?token="+token);
//        response.sendRedirect("http://192.168.199.105:8080/#/recharge?token="+token);
    }
}
