package newenergy.wx.api.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.core.config.ServerConfig;
import newenergy.core.util.RequestUtil;
import newenergy.db.constant.ResultConstant;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.Resident;
import newenergy.db.util.StringUtilCorey;
import newenergy.wx.api.dao.TextMsgXml;
import newenergy.wx.api.service.BindService;
import newenergy.wx.api.service.MsgService;
import newenergy.wx.api.util.WxXmlUtil;
import newenergy.wx.product.manager.UserTokenManager;
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
import java.util.*;

/**
 * Created by HUST Corey on 2019-04-08.
 */
@RestController
@RequestMapping("wx")
public class BindingController {
    @Autowired
    private MsgService msgService;
    @Autowired
    private BindService bindService;
    @Autowired
    private ServerConfig serverConfig;

    private final String greeting = "欢迎关注华工地热科技！";
    private final String binding = "我要绑定";
    private final String unbinding = "我要解绑";
    private final String searching = "查询维修记录";

    private final String bindingLabel = "点此绑定";
    private final String unbindingLabel = "点此解绑";
    private final String searchingLabel = "点此查看";

    private final String bindingUrl = "/#/companyUserBind";
    private final String unbindingUrl = "/#/companyUserBind";
    private final String searchingUrl = "/#/faultRecords";
    @RequestMapping("forward/bind")
    public String forward1(){
        return "绑定页面";
    }
    @RequestMapping("forward/unbind")
    public String forward2(){
        return "解绑页面";
    }
    @RequestMapping("forward/search")
    public String forward3(){
        return "查询页面";
    }
    /**
     * 微信token验证接口，
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @RequestMapping("bind/msg")
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
    @RequestMapping(value = "bind/msg",
            consumes = MediaType.TEXT_XML_VALUE,
            produces = MediaType.TEXT_XML_VALUE,
            method = RequestMethod.POST)
    public String returnMsg(@RequestBody TextMsgXml text){
        if("event".equals(text.getMsgType())
                && "subscribe".equals(text.getEvent())){
            return msgService.buildText(text.getFromUserName(),text.getToUserName(),greeting);
        }else if("text".equals(text.getMsgType())){
            String content = text.getContent();
            if(!binding.equals(content) && !unbinding.equals(content) && !searching.equals(content)) return "";
            String param = String.format("?token=%s&state=%d",UserTokenManager.generateTokenWithOpenid(text.getFromUserName()).getToken(),
                    msgService.getBindState(text.getFromUserName()));
            if(binding.equals(content)){
                String url = serverConfig.getDomain() + bindingUrl + param;
                return msgService.buildLink(text.getFromUserName(),text.getToUserName(),bindingLabel,url);
            }else if(unbinding.equals(content)){
                String url = serverConfig.getDomain() + unbindingUrl + param;
                return msgService.buildLink(text.getFromUserName(),text.getToUserName(),unbindingLabel,url);
            }else if(searching.equals(content)){
                String openid = text.getFromUserName();
                NewenergyAdmin admin = bindService.getAdminByOpenid(openid);
                if(admin == null) return "";
                String searchParam = String.format("?id=%d", admin.getId());
                String url = serverConfig.getDomain() + searchingUrl + searchParam;
                return msgService.buildLink(text.getFromUserName(),text.getToUserName(),searchingLabel,url);
            }
        }
        return "";
    }
    @RequestMapping(value = "bind/search", method = RequestMethod.POST)
    public Map<String,Object> searchBind(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        if(!RequestUtil.checkMap(body,new String[]{"token"})){
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        System.out.println((String)body.get("token")+","+openid);
        if(StringUtilCorey.emptyCheck(openid)){
            ret.put("list",list);
            return ret;
        }
        List<Resident> residents = bindService.getResidents(openid);
        residents.forEach(e->{
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("username",e.getUserName());
            tmp.put("registerId",e.getRegisterId());
            tmp.put("threshold",e.getThreshold());
            list.add(tmp);
        });
        ret.put("list",list);
        return ret;
    }
    @RequestMapping(value = "bind/user", method = RequestMethod.POST)
    public Map<String,Object> bind(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"token","registerId","threshold"})){
            ret.put("code",ResultConstant.ERR);
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        String registerId = (String)body.get("registerId");
        Integer threshold = Integer.valueOf((String)body.get("threshold"));
        Resident resident = bindService.getResident(registerId);
        if(StringUtilCorey.emptyCheck(openid)
                || resident == null) {
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        if(!StringUtilCorey.emptyCheck(resident.getOpenid())){
            ret.put("code",2);
            return ret;
        }
        resident.setOpenid(openid);
        resident.setThreshold(threshold);
        Resident result = bindService.updateResident(resident,null);
        if(result == null){
            ret.put("code",ResultConstant.ERR);
        }else{
            ret.put("code",ResultConstant.OK);
        }
        return ret;
    }
    @RequestMapping(value = "unbind/user",method = RequestMethod.POST)
    public Map<String,Object> unbind(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"registerId","token"})){
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        String registerId = (String)body.get("registerId");
        Resident resident = bindService.getResident(registerId);
        if(resident == null
                || StringUtilCorey.emptyCheck(openid)
                || !openid.equals(resident.getOpenid()) ){
            ret.put("code",ResultConstant.ERR);
            return ret;
        }
        resident.setOpenid("");
        Resident result = bindService.updateResident(resident,null);
        if(result == null){
            ret.put("code",ResultConstant.ERR);
        }else{
            ret.put("code",ResultConstant.OK);
        }
        return ret;
    }
    @RequestMapping(value = "threshold/update",method = RequestMethod.POST)
    public Map<String,Object> updateThreshold(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"updateThreshold","registerId","token"})){
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        String registerId = (String)body.get("registerId");
        Integer threshold = Integer.valueOf((String)body.get("updateThreshold"));
        Resident resident = bindService.getResident(registerId);
        if(resident == null
                || StringUtilCorey.emptyCheck(openid)
                || !openid.equals(resident.getOpenid())
                || threshold == null){
            ret.put("code",ResultConstant.ERR);
            return ret;
        }
        resident.setThreshold(threshold);
        Resident result = bindService.updateResident(resident,null);
        if(result == null){
            ret.put("code",ResultConstant.ERR);
        }else{
            ret.put("code",ResultConstant.OK);
        }
        return ret;
    }
    @RequestMapping(value = "bind/admin", method = RequestMethod.POST)
    public Map<String,Object> bindAdmin(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"token","username","password","state"})){
            ret.put("code",3);
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        if(StringUtilCorey.emptyCheck(openid) ){
            ret.put("code",3);
            return ret;
        }
        String username = (String)body.get("username");
        String password = (String)body.get("password");
        Integer state = Integer.valueOf((String)body.get("state"));
        if(state==null || !state.equals(0) && !state.equals(1) ){
            ret.put("code",3);
            return ret;
        }
        NewenergyAdmin admin = bindService.getAdmin(username,password);
        if(admin == null){
            ret.put("code",1);
            return ret;
        }
        if(state.equals(0) && !StringUtilCorey.emptyCheck(admin.getOpenid())){
            ret.put("code",2);
            return ret;
        }
        if(state.equals(0)){
            admin.setOpenid(openid);
        }
        if(state.equals(1)){
            admin.setOpenid("");
        }
        NewenergyAdmin result = bindService.updateAdmin(admin,null);
        if(result == null){
            ret.put("code",3);
        }else{
            ret.put("code",0);
        }
        return ret;
    }
}
