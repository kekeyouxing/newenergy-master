package newenergy.wx.api.controller;

import newenergy.core.config.WxTokenSetting;
import newenergy.wx.template.AccessToken;
import newenergy.wx.template.AccessTokenCenter;
import newenergy.wx.template.Ret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by HUST Corey on 2019-04-22.
 */
@RestController
@RequestMapping("wx")
public class WxTestController {
    @Autowired
    private WxTokenSetting setting;
    @RequestMapping("test")
    public String getAccessToken(){
        Ret<AccessToken> ret = AccessTokenCenter.getAccessToken(setting.getAppId(),setting.getAppSecret());
        return ret.ok()?ret.get().getAccess_token():String.valueOf(ret.getCode());
    }
}
