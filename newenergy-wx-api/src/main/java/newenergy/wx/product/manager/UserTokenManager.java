package newenergy.wx.product.manager;

import newenergy.core.util.CharUtil;
import newenergy.wx.product.pojo.UserToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义用户授权token管理器
 * @author yangq
 * @date 2019-04-18
 */
public class UserTokenManager {
    private static Map<String,UserToken> tokenMap = new HashMap<>();
    private static Map<String,UserToken> openIdMap = new HashMap<>();

    /**
     * 通过token获取openId
     * @param token
     * @return
     */
    public static String getOpenId(String token){
        UserToken userToken = tokenMap.get(token);
        if (userToken == null){
            return null;
        }
        if (userToken.getExpireTime().isBefore(LocalDateTime.now())) {
            tokenMap.remove(token);
            openIdMap.remove(userToken.getOpenId());
            return null;
        }
        return userToken.getOpenId();
    }

    /**
     * 生成token,并将token和openId绑定
     * @param openId
     * @return
     */
    public static UserToken generateToken(String openId){
        UserToken userToken = null;

        String token = CharUtil.getRandomString(32);
        while(tokenMap.containsKey(token)){
            token = CharUtil.getRandomString(32);
        }
        LocalDateTime update = LocalDateTime.now();
        LocalDateTime expire = update.plusHours(2);

        userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUpdateTime(update);
        userToken.setExpireTime(expire);
        userToken.setOpenId(openId);
        tokenMap.put(token,userToken);
        openIdMap.put(openId,userToken);

        return userToken;
    }

    /**
     * 移除无效token
     * @param openId
     */
    public static void removeToken(String openId){
        UserToken userToken = openIdMap.get(openId);
        String token = userToken.getToken();
        openIdMap.remove(openId);
        tokenMap.remove(token);
    }
}
