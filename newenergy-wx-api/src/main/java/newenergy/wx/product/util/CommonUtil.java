package newenergy.wx.product.util;

import newenergy.wx.product.pojo.Token;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * 通用工具类
 *
 * @author yangq
 * @date 2019-04-15
 */
public class CommonUtil {
    private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
    public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * 发送https请求
     *
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    public static JSONObject httpsRequest(String requestUrl,String requestMethod,String outputStr){
        JSONObject jsonObject = null;
        try{
            //创建SSLContext对象，并使用我们指定的信任管理器初始化
        }catch (Exception e){
            log.error("https请求异常：{}",e);
        }
        return jsonObject;
    }

    /**
     * 获取微信访问接口——access_token
     * @param appid
     * @param appsecret
     * @return
     */
    public static Token getAccessToken(String appid,String appsecret){
        Token token  = null;
        String requestUrl = token_url.replace("AppID",appid).replace("APPSECRET",appsecret);
        //发起GET请求获取凭证
        JSONObject jsonObject = httpsRequest(requestUrl,"GET",null);

        if (null != jsonObject){
            try{
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInt("expires_in"));
            }catch (JSONException e){
                token = null;
                log.error("获取token失败 errcode:{} errmsg:{}",jsonObject.getInt("errcode"),jsonObject.getString("errmsg"));
            }
        }
        return token;
    }

    /**
     * URL编码（utf-8）
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source){
        String result = source;
        try{
            result = java.net.URLEncoder.encode(source,"utf-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return result;
    }
}
