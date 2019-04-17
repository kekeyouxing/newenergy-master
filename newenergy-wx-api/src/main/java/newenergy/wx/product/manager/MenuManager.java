//package newenergy.wx.product.manager;
//
//import newenergy.core.service.WxProductService;
//import newenergy.wx.product.menu.*;
//import newenergy.wx.product.pojo.Token;
//import newenergy.wx.product.util.CommonUtil;
//import newenergy.wx.product.util.MenuUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
///**
// * 菜单管理器类
// *
// * @author yangq
// * @date 2019-04-15
// */
//@Component
//public class MenuManager implements ApplicationRunner {
//    private static Logger log = LoggerFactory.getLogger(MenuManager.class);
//
//    @Autowired
//    private WxProductService wxProductService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        Token token = CommonUtil.getAccessToken(wxProductService.getWxProductConfig().getAppId(),wxProductService.getWxProductConfig().getAppSecret());
//        if (null != token){
//            boolean result = MenuUtil.createMenu(getMenu(),token.getAccessToken());
//            if (result)
//                log.info("菜单创建成功");
//            else
//                log.info("菜单创建失败");
//        }
//    }
//
//    private static Menu getMenu(){
//        ViewButton btn11 = new ViewButton();
//        btn11.setName("用户绑定");
//        btn11.setType("view");
//        btn11.setUrl("www.baidu.com");
//
//        ViewButton btn12 = new ViewButton();
//        btn12.setName("用户充值");
//        btn12.setType("view");
//        btn12.setUrl("www.baidu.com");
//
//        ViewButton btn21 = new ViewButton();
//        btn21.setName("余额查询");
//        btn21.setType("view");
//        btn21.setUrl("www.baidu.com");
//
//        ViewButton btn22 = new ViewButton();
//        btn22.setName("充值记录");
//        btn22.setType("view");
//        btn22.setUrl("www.baidu.com");
//
//        ComplexButton mainBtn1 = new ComplexButton();
//        mainBtn1.setName("用户");
//        mainBtn1.setSubButton(new Button[]{btn11,btn12});
//
//        ComplexButton mainBtn2 = new ComplexButton();
//        mainBtn2.setName("查询");
//        mainBtn2.setSubButton(new Button[]{btn21,btn22});
//
//        Menu menu = new Menu();
//        menu.setButton(new Button[]{mainBtn1,mainBtn2});
//
//        return menu;
//    }
//}
