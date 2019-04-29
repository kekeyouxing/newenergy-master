package newenergy.wx.product.manager;

import newenergy.core.service.WxProductService;
import newenergy.wx.product.menu.*;
import newenergy.wx.product.pojo.Token;
import newenergy.wx.product.util.CommonUtil;
import newenergy.wx.product.util.MenuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 菜单管理器类
 *
 * @author yangq
 * @date 2019-04-15
 */
@Component
public class MenuManager implements ApplicationRunner {
    private static Logger log = LoggerFactory.getLogger(MenuManager.class);
    private static String baseUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
    private static String domainName = "http://wp86h5.natappfree.cc/";
    @Autowired
    private WxProductService wxProductService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Token token = CommonUtil.getAccessToken("wx56acef520e1b0030","3848a4749c6337c86f3dcf42b3d21d2a");
//        Token token = CommonUtil.getAccessToken(wxProductService.getWxProductConfig().getAppId(),wxProductService.getWxProductConfig().getAppSecret());
        if (null != token){
            boolean result = MenuUtil.createMenu(getMenu(),token.getAccessToken());
            if (result)
                log.info("菜单创建成功");
            else
                log.info("菜单创建失败");
        }
    }

    private Menu getMenu(){

        String URI = baseUrl.replace("SCOPE","snsapi_userinfo").replace("APPID", "wx56acef520e1b0030");

//        String URI = baseUrl.replace("SCOPE","snsapi_userinfo").replace("APPID",wxProductService.getWxProductConfig().getAppId());
        String bindUrl = URI.replace("REDIRECT_URI",CommonUtil.urlEncodeUTF8(domainName+"wx/OAuth/userBind"));
        String rechargeUrl = URI.replace("REDIRECT_URI",CommonUtil.urlEncodeUTF8(domainName+"wx/OAuth/recharge"));
        ViewButton btn11 = new ViewButton();
        btn11.setName("用户绑定");
//        btn11.setName("test11");
        btn11.setType("view");
//        btn11.setUrl("http://www.baidu.com");
        btn11.setUrl(bindUrl);

        ViewButton btn12 = new ViewButton();
        btn12.setName("用户充值");
//        btn12.setName("test12");
        btn12.setType("view");
//        btn12.setUrl("http://www.baidu.com");
        btn12.setUrl(rechargeUrl);

        ViewButton btn21 = new ViewButton();
        btn21.setName("余额查询");
//        btn21.setName("test21");
        btn21.setType("view");
//        btn21.setUrl("http://www.baidu.com");
        btn21.setUrl(domainName+"#/checkBalance");

        ViewButton btn22 = new ViewButton();
        btn22.setName("充值记录");
//        btn21.setName("test22");
        btn22.setType("view");
//        btn22.setUrl("http://www.baidu.com");
        btn22.setUrl(domainName+"#/rechargeRecords");

        ComplexButton mainBtn1 = new ComplexButton();
        mainBtn1.setName("用户");
//        mainBtn1.setName("test1");
        mainBtn1.setSub_button(new Button[]{btn11,btn12});

        ComplexButton mainBtn2 = new ComplexButton();
        mainBtn2.setName("查询");
//        mainBtn1.setName("test2");
        mainBtn2.setSub_button(new Button[]{btn21,btn22});

        Menu menu = new Menu();
        menu.setButton(new Button[]{mainBtn1,mainBtn2});

        return menu;
    }
}
