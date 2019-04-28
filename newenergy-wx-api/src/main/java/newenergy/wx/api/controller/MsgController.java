package newenergy.wx.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import newenergy.core.config.ServerConfig;
import newenergy.core.util.RequestUtil;
import newenergy.core.pojo.MsgRet;
import newenergy.core.util.TimeUtil;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.constant.ResultConstant;
import newenergy.db.domain.CorrAddress;
import newenergy.db.domain.FaultRecord;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.Resident;
import newenergy.db.util.StringUtilCorey;
import newenergy.wx.api.service.MsgService;
import newenergy.wx.product.manager.UserTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by HUST Corey on 2019-04-22.
 */
@RestController
@RequestMapping("wx")
public class MsgController {

    @Autowired
    MsgService msgService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ServerConfig serverConfig;
    /**
     * 发送模板消息的url
     */
    private final String sendurl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";


    /**
     * 故障消息提醒回调路径
     */
    private final String faultRecall = "/wx/forward/record";
    /**
     * 故障消息上报回调路径
     */
    private final String reportRecall =  "/wx/forward/report";

    /**
     * 故障提醒消息id
     */
    private final String faultMsgId = "FiSumxdmorUPGgTh8BHmX7hlkxOOASYN3DFTItJjxc4";
    /**
     * 流量预警消息id
     */
    private final String warnMsgId = "e1dLYCj_s17-wEbFvNm3lyPLEN8lQRcf5h38XmXftWQ";
    /**
     * 故障超时上报消息id
     */
    private final String faultReportId = "nOD-gDlv1w8_bZN5V5NreUgFPA-J1OruL3nAyIb1WSE";

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "forward/record")
    public String forward(){
        return "故障消息";
    }
    @RequestMapping(value = "forward/report")
    public String forward2(){
        return "故障上报";
    }


    @RequestMapping(value = "fault/send",method = RequestMethod.POST)
    public MsgRet faultMsg(@RequestBody Map<String,Object> body) throws Exception{
        //输入参数

        if(!RequestUtil.checkMap(body,
                new String[]{"touser","phenomenon","address","phone","partName","faultId"})) return null;
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("touser",body.get("touser"));
        jsonMap.put("template_id",faultMsgId);
        String param = String.format("?faultId=%d&token=%s",
                body.get("faultId"),
                UserTokenManager.generateToken((String)body.get("touser")) );
        jsonMap.put("url",serverConfig.getDomain() + faultRecall + param);
        Map<String,Object> subBody = new HashMap<>();
        Map<String,Object> pheno = new HashMap<>();
        pheno.put("value",body.get("phenomenon"));
        pheno.put("color","#173177");
        Map<String,Object> address = new HashMap<>();
        address.put("value",body.get("address"));
        address.put("color","#173177");
        Map<String,Object> phone = new HashMap<>();
        phone.put("value",body.get("phone"));
        phone.put("color","#173177");
        Map<String,Object> partName = new HashMap<>();
        partName.put("value",body.get("partName"));
        partName.put("color","#173177");
        subBody.put("phenomenon",pheno);
        subBody.put("address",address);
        subBody.put("phone",phone);
        subBody.put("partName",partName);
        jsonMap.put("data",subBody);
        return restTemplate.postForObject(sendurl+msgService.getAccessToken(),objectMapper.writeValueAsBytes(jsonMap),MsgRet.class);
    }

    @RequestMapping(value = "threshold/send", method = RequestMethod.POST)
    public MsgRet thresholdMsg(@RequestBody Map<String,Object> body) throws Exception{
        if(!RequestUtil.checkMap(body,
                new String[]{"touser","remainWater","updateTime"})) return null;
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("touser",body.get("touser"));
        jsonMap.put("template_id",warnMsgId);
        Map<String,Object> subBody = new HashMap<>();
        Map<String,Object> remainWater = new HashMap<>();
        remainWater.put("color","#173177");
        remainWater.put("value",body.get("remainWater"));
        Map<String,Object> updateTime = new HashMap<>();
        updateTime.put("color","#173177");
        updateTime.put("value",body.get(updateTime));
        subBody.put("remainWater",remainWater);
        subBody.put("updateTime",updateTime);
        jsonMap.put("data",subBody);
        return restTemplate.postForObject(sendurl+msgService.getAccessToken(),objectMapper.writeValueAsBytes(jsonMap),MsgRet.class);
    }
    @RequestMapping(value = "report/send",method = RequestMethod.POST)
    public MsgRet reportMsg(@RequestBody Map<String,Object> body) throws Exception{
        if(!RequestUtil.checkMap(body,
                new String[]{"touser","faultTime","address","servicer","faultId"})) return null;
        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("touser",body.get("touser"));
        jsonMap.put("template_id",faultReportId);
        String param = String.format("?faultId=%d&token=%s",
                body.get("faultId"),
                UserTokenManager.generateToken((String)body.get("touser")) );
        jsonMap.put("url",serverConfig.getDomain() + reportRecall + param);
        Map<String,Object> subBody = new HashMap<>();
        Map<String,Object> faultTime = new HashMap<>();
        faultTime.put("value",body.get("faultTime"));
        faultTime.put("color","#173177");
        Map<String,Object> address = new HashMap<>();
        address.put("value",body.get("address"));
        address.put("color","#173177");
        Map<String,Object> servicer = new HashMap<>();
        servicer.put("value",body.get("servicer"));
        servicer.put("color","#173177");
        subBody.put("faultTime",faultTime);
        subBody.put("address",address);
        subBody.put("servicer",servicer);
        jsonMap.put("data",subBody);
        return restTemplate.postForObject(sendurl+msgService.getAccessToken(),objectMapper.writeValueAsBytes(jsonMap),MsgRet.class);
    }
    @RequestMapping(value = "fault/dtl",method = RequestMethod.POST)
    public Map<String,Object> faultDtl(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"faultId","token"})){
            return null;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        if(StringUtilCorey.emptyCheck(openid)) return ret;
        Integer faultId = (Integer)body.get("faultId");
        FaultRecord record = msgService.getFaultRecords(faultId);
        if(record == null) return null;
        Resident resident = msgService.getResident(record.getRegisterId());
        if(resident == null) return null;
        CorrAddress corrAddress = msgService.getCorrAddress(resident.getAddressNum());
        String addressDtl = corrAddress==null?null:corrAddress.getAddressDtl();
        NewenergyAdmin admin = msgService.getNewenergyAdmin(record.getServicerId());
        ret.put("registerId",record.getRegisterId());
        ret.put("phenomenon",record.getPhenomenon());
        ret.put("username",resident.getUserName());
        ret.put("roomNum",resident.getRoomNum());
        ret.put("phone",resident.getPhone());
        ret.put("addressDtl",addressDtl);
        //不是故障维修人员，不在等待中
        if(!FaultRecordConstant.STATE_WAIT.equals(record.getState())){
            if(FaultRecordConstant.RESULT_TIMEOUT.equals(record.getResult()))
                ret.put("state",0);
            if(FaultRecordConstant.RESULT_FAILED.equals(record.getResult()))
                ret.put("state",1);
            ret.put("servicerName",admin.getRealName());
            ret.put("servicerPhone",admin.getPhone());
        }
        return ret;
    }
    @RequestMapping(value = "fault/ensure",method = RequestMethod.POST)
    public Map<String,Object> faultEnsure(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"token","faultId"})){
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        if(StringUtilCorey.emptyCheck(openid)){
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        Integer faultId = (Integer)body.get("faultId");
        FaultRecord record = msgService.getFaultRecords(faultId);
        if(record == null){
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        record.setState(FaultRecordConstant.STATE_DURING);
        record.setResponseTime(TimeUtil.getUTCNow());
        FaultRecord result = msgService.updateRecord(record);
        if(result == null){
            ret.put("code",ResultConstant.ERR);
        }else{
            ret.put("code",ResultConstant.OK);
        }
        return ret;
    }
    @RequestMapping(value = "fault/feedback", method = RequestMethod.POST)
    public Map<String,Object> faultFeedback(@RequestBody Map<String,Object> body){
        Map<String,Object> ret = new HashMap<>();
        if(!RequestUtil.checkMap(body,new String[]{"token","faultId","feedback","finish"})){
            ret.put("code",ResultConstant.ERR);
            return ret;
        }
        String openid = UserTokenManager.getOpenId((String)body.get("token"));
        if(StringUtilCorey.emptyCheck(openid)){
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        Integer faultId = (Integer)body.get("faultId");
        FaultRecord record = msgService.getFaultRecords(faultId);
        if(record == null){
            ret.put("code", ResultConstant.ERR);
            return ret;
        }
        record.setState(FaultRecordConstant.STATE_FINISH);
        record.setFinishTime(TimeUtil.getUTCNow());
        record.setSolution((String)body.get("feedback"));
        int finish = (int)body.get("finish");
        if(finish == 0){
            record.setResult(FaultRecordConstant.RESULT_SUCCESS);
        }
        if(finish == 1){
            record.setResult(FaultRecordConstant.RESULT_FAILED);
        }
        FaultRecord result = msgService.updateRecord(record);
        if(result == null){
            ret.put("code",ResultConstant.ERR);
        }else{
            ret.put("code",ResultConstant.OK);
        }
        return ret;
    }
}
