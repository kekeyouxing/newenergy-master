package newenergy.wx.template;

/**
 * Created by HUST Corey on 2019-04-22.
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.JsonNull;
import newenergy.core.config.WxTokenSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * access token 获取中心
 */
public class AccessTokenCenter {

    private final static RestTemplate restTemplate = new RestTemplate();
    private final static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    private static AccessToken token = null;
    private static Long time = null;

    public synchronized static Ret<AccessToken> getAccessToken(String APPID, String  APPSECRET){
        int code = 0;
        Ret<AccessToken> ret = new Ret<>();
        Long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        if(token == null || time == null || 1.0 * (now-time) / token.getExpires_in() > 0.8){
            JsonNode json = restTemplate.getForObject(String.format(url,APPID,APPSECRET), JsonNode.class);
            if(json == null){
                code = 1;
            }else if(json.has("errcode")){
                code = json.get("errcode").asInt();
            }
            if(code == 0){
                AccessToken accessToken = new AccessToken();
                accessToken.setAccess_token(json.get("access_token").asText());
                accessToken.setExpires_in(json.get("expires_in").asLong());
                token = accessToken;
                time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            }
        }
        ret.set(token);
        ret.setCode(code);
        return ret;
    }

}
