package newenergy.wx.api.controller;

import newenergy.core.util.JacksonUtil;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by HUST Corey on 2019-04-22.
 */
@RestController
@RequestMapping("wx")
public class MsgController {
    /**
     * TODO 使用中控服务器来获取
     */
    private String access_token = "20_H-P_EjcMvv3qvXjrGsGU0OQnqW5gvS5BFFki-n9mqeA4OHNQhw33Htha6eMi6GWpVEJxtZdezPrtD4A0sWpA-BiIapwAB3uJ2WvsB1pcM5i1hAvnWksn0nlEAHK_WNWFQyInEjTnWIbcEMUAHSXfADARCH";
    private String appid = "wx0ecc55fde07a32af";
    private String appsecret = "204968f0b20dd9d7551eea63b797a459";
    private String sendurl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    private RestTemplate restTemplate = new RestTemplate();
    @RequestMapping(value = "forward")
    public String forward(){
        return "Successful Forward";
    }
    static class MsgRet{
        Integer errcode;
        String errmsg;
        String msgid;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }
    }
    static class MsgTemplate{
        String title;
        String key1;
        String value1;
        String key2;
        String value2;
        String end;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getKey1() {
            return key1;
        }

        public void setKey1(String key1) {
            this.key1 = key1;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getKey2() {
            return key2;
        }

        public void setKey2(String key2) {
            this.key2 = key2;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
    static class MsgBody{
        String touser;
        String template_id;
        String url;
        String appid;
        MsgTemplate data;

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public MsgTemplate getData() {
            return data;
        }

        public void setData(MsgTemplate data) {
            this.data = data;
        }
    }
    @RequestMapping(value = "send",method = RequestMethod.POST)
    public MsgRet sendMsg(@RequestBody MsgBody msgBody) throws URISyntaxException {
        return restTemplate.postForObject(new URI(sendurl+access_token),msgBody,MsgRet.class);

    }

    static class AccessTokenRet{
        String access_token;
        Integer expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }
    }
    private String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    @RequestMapping(value = "access_token")
    public String getAccessToken(){
        String url = String.format(accessTokenUrl,appid,appsecret);
        AccessTokenRet ret = restTemplate.getForObject(url,AccessTokenRet.class);
        if(ret != null)
            access_token = ret.getAccess_token();
        return access_token;
    }

}
