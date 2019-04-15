package newenergy.wx.product.pojo;

/**
 * 微信通用接口凭证，access_token
 *
 * @author yangq
 * @date 2019-04-15
 */
public class Token {
    //获取到的凭证
    private String accessToken;
    //获取有效时间，单位，秒
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
