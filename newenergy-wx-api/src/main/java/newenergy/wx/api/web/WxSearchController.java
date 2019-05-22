package newenergy.wx.api.web;

import newenergy.core.util.ResponseUtil;
import newenergy.core.util.TimeUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RemainWater;
import newenergy.db.service.RemainWaterService;
import newenergy.db.service.ResidentService;
import newenergy.wx.api.service.WxSearchService;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static newenergy.admin.util.AdminResponseCode.USER_INVALID_NAME;
import static newenergy.admin.util.AdminResponseCode.NO_REMAIN_WATER;
import static newenergy.admin.util.AdminResponseCode.NO_RECHARGE_RECORD;

/**
 * 微信查询
 */
@RestController
@RequestMapping("/wx/search")
public class WxSearchController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RemainWaterService remainWaterService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WxSearchService wxSearchService;

    /**
     * 获取剩余水量的信息
     * @param registerId 登记号
     * @param username 用户名
     * @return Map<String, Object>对象
     *          "updateTime" : updateTime
     *          "remainVolume" : remainVolume
     */
    @GetMapping("/remainWaterInfo")
    public Object remainWaterInfo(@RequestParam String registerId,
                                  @RequestParam String username){
        logger.info("<remainWaterInfo> params : " + registerId+","+username);
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(registerId)){
            return ResponseUtil.badArgument();
        }
        if(!wxSearchService.verify(username, registerId)){
            return ResponseUtil.fail(USER_INVALID_NAME, "用户名或登记号不正确");
        }
        RemainWater remainWater = wxSearchService.remainWaterInfo(registerId);
        if(null == remainWater){
            return ResponseUtil.fail(NO_REMAIN_WATER, "暂时还没有剩余用水量");
        }
        LocalDateTime updateTime = remainWater.getUpdateTime();
        BigDecimal remainVolume = remainWater.getRemainVolume();
        Map<String, Object> data = new HashMap<>();
        data.put("updateTime", TimeUtil.getString(updateTime) );
        data.put("remainVolume", remainVolume);
        return ResponseUtil.ok(data);
    }

    /**
     * 查询充值记录
     * @param registerId 登记号
     * @param username 用户名
     * @param year 年份
     * @param startMonth 起始月份
     * @param endMonth 结束月份
     * @return Map<String, Object>对象
     *          "updateTime" : updateTime
     *          "remainVolume" : remainVolume
     *          "rechargeRecords" : List<Map<String, Object>>
     *            "amount" : amount
     *            "rechargeTime" : rechargeTime
     *            "username" : username
     *            "state" : state
     *            "delegate" : delegate
     */
    @GetMapping("/rechargeRecordInfo")
    public Object rechargeRecordInfo(@RequestParam String registerId,
                                     @RequestParam String username,
                                     @RequestParam Integer year,
                                     @RequestParam Integer startMonth,
                                     @RequestParam Integer endMonth){
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(registerId)){
            return ResponseUtil.badArgument();
        }
        if(!wxSearchService.verify(username, registerId)){
            return ResponseUtil.fail(USER_INVALID_NAME, "用户名或登记号不正确");
        }
        RemainWater remainWater = wxSearchService.remainWaterInfo(registerId);
//        if(null == remainWater){
//            return ResponseUtil.fail(NO_REMAIN_WATER, "暂时还没有剩余用水量");
//        }
        List<RechargeRecord> rechargeRecords = wxSearchService.rechargeRecordInfo(registerId, year, startMonth, endMonth);
            if(null == rechargeRecords || rechargeRecords.isEmpty()){
            return ResponseUtil.fail(NO_RECHARGE_RECORD, "暂时还没有充值记录");
        }
        List<Map<String, Object>> rechargeRecordsData = new ArrayList<>();
        for (RechargeRecord rechargeRecord : rechargeRecords) {
//            if (rechargeRecord.getState() == 2) continue;
            Map<String, Object> temp = new HashMap<>();
            temp.put("amount", rechargeRecord.getAmount());
            temp.put("rechargeTime", TimeUtil.getString( rechargeRecord.getRechargeTime()) );
            temp.put("rechargeVolume",rechargeRecord.getRechargeVolume());
            temp.put("username", rechargeRecord.getUserName());
            temp.put("state", rechargeRecord.getState());
            temp.put("delegate", rechargeRecord.getDelegate());
            rechargeRecordsData.add(temp);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("rechargeRecords", rechargeRecordsData);
        data.put("remainExisted",remainWater==null?0:1);
        if(remainWater!=null){
            data.put("updateTime", TimeUtil.getString( remainWater.getUpdateTime()) );
            data.put("remainVolume", remainWater.getRemainVolume());
        }

        return ResponseUtil.ok(data);
    }
}
