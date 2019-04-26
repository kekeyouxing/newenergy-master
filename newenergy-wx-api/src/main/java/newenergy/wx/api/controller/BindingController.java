package newenergy.wx.api.controller;

import newenergy.core.config.ServerConfig;
import newenergy.db.util.StringUtilCorey;
import newenergy.wx.api.dao.TextMsgXml;
import newenergy.wx.api.service.MsgService;
import newenergy.wx.api.util.WxXmlUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HUST Corey on 2019-04-08.
 */
@RestController
@RequestMapping("wx")
public class BindingController {
    @Autowired
    private MsgService msgService;
    @Autowired
    private ServerConfig serverConfig;

    private final String greeting = "欢迎关注华工地热科技！";
    private final String binding = "我要绑定";
    private final String unbinding = "我要解绑";
    private final String bindingLabel = "点此绑定";
    private final String unbindingLabel = "点此解绑";
    private final String bindingUrl = "/wx/forward/bind";
    private final String unbindingUrl = "/wx/forward/unbind";
    @RequestMapping("forward/bind")
    public String forward1(){
        return "绑定页面";
    }
    @RequestMapping("forward/unbind")
    public String forward2(){
        return "解绑页面";
    }
    /**
     * 微信token验证接口，
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @RequestMapping("bind")
    public String validation(String signature, String timestamp, String nonce, String echostr ){
        if(StringUtilCorey.emptyCheck(signature) || StringUtilCorey.emptyCheck(timestamp)
                || StringUtilCorey.emptyCheck(nonce) || StringUtilCorey.emptyCheck(echostr))
            return "";
        List<String> msgs = new ArrayList<>();
        msgs.add(nonce);
        msgs.add(timestamp);
        /**
         * TODO
         * 配置的Token
         */
        msgs.add("hgdr_hust");
        Collections.sort(msgs);
        String result = DigestUtils.sha1Hex(msgs.get(0)+msgs.get(1)+msgs.get(2));
        System.out.println("result:"+result);
        System.out.println("signature:"+signature);
        if(result.equals(signature)){
            return echostr;
        }else{
            return "";
        }
    }
    /**
     * 公司用户绑定
     */
    @RequestMapping(value = "bind",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE,
            method = RequestMethod.POST)
    public String returnMsg(@RequestBody TextMsgXml text){
        if("event".equals(text.getMsgType())
                && "subscribe".equals(text.getEvent())){
            return msgService.buildText(text.getFromUserName(),text.getToUserName(),greeting);
        }else if("text".equals(text.getMsgType())){
            String content = text.getContent();
            if(!binding.equals(content) && !unbinding.equals(content)) return "";
            String param = String.format("?openid=%s&state=%d",text.getFromUserName(),
                    msgService.getBindState(text.getFromUserName()));
            if(binding.equals(content)){
                String url = serverConfig.getDomain() + bindingUrl + param;
                return msgService.buildLink(text.getFromUserName(),text.getToUserName(),bindingLabel,url);
            }else{
                String url = serverConfig.getDomain() + unbindingUrl + param;
                return msgService.buildLink(text.getFromUserName(),text.getToUserName(),unbindingLabel,url);
            }
        }
        return "";

    }
}
