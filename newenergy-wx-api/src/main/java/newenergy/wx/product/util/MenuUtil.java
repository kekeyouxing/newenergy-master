package newenergy.wx.product.util;

import net.sf.json.JSONObject;
import newenergy.wx.product.menu.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义菜单工具类
 *
 * @author yangq
 * @data 2019-04-15
 */
public class MenuUtil {
    private static Logger log = LoggerFactory.getLogger(MenuUtil.class);

    //菜单创建（POST）
    public final static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    /**
     * 创建菜单
     * @param menu 菜单实例
     * @param accessToken 凭证
     * @return true成功 false失败
     */
    public static boolean createMenu(Menu menu,String accessToken){
        boolean result = false;
        String url = menu_create_url.replace("ACCESS_TOKEN",accessToken);
        //将菜单对象转换成json字符串
        String jsonMenu = JSONObject.fromObject(menu).toString();
        //发起POST请求创建菜单
        JSONObject jsonObject = CommonUtil.httpsRequest(url,"POST",jsonMenu);

        if (null != jsonObject){
            int errorCode = jsonObject.getInt("errcode");
            String errorMsg = jsonObject.getString("errmsg");
            if (0 == errorCode){
                result = true;
            }else{
                result = false;
                log.error("创建菜单失败 errcode:{} errmsg:{}",errorCode,errorMsg);
            }
        }
        return result;
    }
}
