package newenergy.wx.template;

/**
 * Created by HUST Corey on 2019-04-22.
 */
public class AccessToken {
    String access_token;
    Long expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }
}
