package newenergy.wx.product.pojo;

import java.time.LocalDateTime;

/**
 * 自定义用户授权token
 * @author yangq
 * @date 2019-04-18
 */
public class UserToken {
    //用户唯一标识
    private String openId;
    //自定义用户授权token
    private String token;
    //token过期时间（2h）
    private LocalDateTime expireTime;
    //token生成时间
    private LocalDateTime updateTime;
    //微信昵称
    private String nickname;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
