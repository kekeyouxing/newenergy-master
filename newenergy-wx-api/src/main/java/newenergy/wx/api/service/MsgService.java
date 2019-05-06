package newenergy.wx.api.service;

import newenergy.core.config.WxTokenSetting;
import newenergy.core.util.TimeUtil;
import newenergy.db.constant.SafeConstant;
import newenergy.db.domain.CorrAddress;
import newenergy.db.domain.FaultRecord;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.Resident;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.service.FaultRecordService;
import newenergy.wx.template.AccessToken;
import newenergy.wx.template.AccessTokenCenter;
import newenergy.wx.template.Ret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HUST Corey on 2019-04-23.
 */
@Service
public class MsgService {
    @Autowired
    private WxTokenSetting setting;
    @Autowired
    private NewenergyAdminRepository newenergyAdminRepository;
    @Autowired
    private FaultRecordService faultRecordService;
    /**
     *
     * @return 可为""串
     */
    public String getAccessToken(){
        Ret<AccessToken> ret = AccessTokenCenter.getAccessToken(setting.getAppId(),setting.getAppSecret());
        return ret.ok()?ret.get().getAccess_token():"";
    }
    public String buildText(String toUserName, String fromUserName, String content){
        return String.format("<xml>\n" +
                        "  <ToUserName><![CDATA[%s]]></ToUserName>\n" +
                        "  <FromUserName><![CDATA[%s]]></FromUserName>\n" +
                        "  <CreateTime>%d</CreateTime>\n" +
                        "  <MsgType><![CDATA[text]]></MsgType>\n" +
                        "  <Content><![CDATA[%s]]></Content>\n" +
                        "</xml>",toUserName,fromUserName, TimeUtil.getCurSeconds(),content);
    }
    public String buildLink(String toUserName, String fromUserName, String linkName, String url){
        String alink = String.format("<a href=\"%s\">%s</a>",url,linkName);
        return buildText(toUserName,fromUserName,alink);
    }
    public boolean isBindAdmin(String openid){
        NewenergyAdmin admin = newenergyAdminRepository
                .findFirstByOpenidAndSafeDelete(openid, SafeConstant.SAFE_ALIVE);
        return admin!=null;
    }
    public int getBindState(String openid){
        return  isBindAdmin(openid)?1:0;
    }
    public FaultRecord getFaultRecords(Integer id){
        if(id == null) return null;
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setId(id);
        return faultRecordService.findOneByPredicate(predicate,null,null);
    }
    public FaultRecord updateRecord(FaultRecord record){
        return faultRecordService.updateRecord(record);
    }
    public Resident getResident(String registerId){
        return faultRecordService.getResident(registerId);
    }
    public CorrAddress getCorrAddress(String addressNum){
        return faultRecordService.getCorrAddress(addressNum);
    }
    public NewenergyAdmin getNewenergyAdmin(Integer id){
        return faultRecordService.getNewenergyAdmin(id);
    }
}
