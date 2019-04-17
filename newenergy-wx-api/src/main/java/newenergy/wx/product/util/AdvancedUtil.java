package newenergy.wx.product.util;

import net.sf.json.JSONObject;
import newenergy.wx.product.pojo.WeixinOauth2Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 高级接口工具类
 *
 * @author yangq
 * @date 2019-04-17
 */
public class AdvancedUtil {
    private static Logger log  = LoggerFactory.getLogger(AdvancedUtil.class);

    /**
     * 获取网页授权凭证
     * @param appId 公众账号唯一凭证
     * @param appSecret 公众账号的密钥
     * @param code
     * @return WeixinOauth2Token
     */
    public static WeixinOauth2Token getOauth2AccessToken(String appId,String appSecret,String code){
        WeixinOauth2Token wat = null;
        //拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        requestUrl = requestUrl.replace("APPID",appId);
        requestUrl = requestUrl.replace("SECRET",appSecret);
        requestUrl = requestUrl.replace("CODE",code);
        //获取网页授权凭证
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl,"GET",null);
        if(null!=jsonObject){
            try{
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInt("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            }catch (Exception e){
                wat = null;
                int errorCode =  jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}",errorCode,errorMsg);
            }
        }
        return wat;
    }

    /**
     * 刷新网页授权凭证
     *
     * @param appId 公众账号唯一标识
     * @param refreshToken
     * @return WeixinAouth2Token
     */
    public static WeixinOauth2Token refreshOauth2AccessToken(String appId,String refreshToken){
        WeixinOauth2Token wat = null;
        //拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
        requestUrl = requestUrl.replace("APPID",appId);
        requestUrl = requestUrl.replace("REFRESH_TOKEN",refreshToken);
        //刷新网页授权凭证
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl,"GET",null);
        if (null != jsonObject){
            try{
                wat = new WeixinOauth2Token();
                wat.setAccessToken(jsonObject.getString("access_token"));
                wat.setExpiresIn(jsonObject.getInt("expires_in"));
                wat.setRefreshToken(jsonObject.getString("refresh_token"));
                wat.setOpenId(jsonObject.getString("openid"));
                wat.setScope(jsonObject.getString("scope"));
            }catch (Exception e){
                wat = null;
                int errorCode = jsonObject.getInt("errcode");
                String errorMsg = jsonObject.getString("errmsg");
                log.error("刷新网页授权凭证失败 errcode:{} errmsg:{}",errorCode,errorMsg);
            }
        }
        return wat;
    }
}
