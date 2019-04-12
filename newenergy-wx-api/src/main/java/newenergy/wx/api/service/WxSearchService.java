package newenergy.wx.api.service;

import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RemainWater;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.RemainWaterService;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static newenergy.admin.util.AdminResponseCode.USER_INVALID_NAME;

@Service
public class WxSearchService {

    @Autowired
    private RemainWaterService remainWaterService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    /**
     * 获取剩余水量的信息
     * @param body
     * @return Map<String, Object>对象
     *         "updateTime" : updateTime
     *         "remainVolume" : remainVolume
     */
    @Transactional
    public Object remainWaterInfo(String body) {
        String username = JacksonUtil.parseString(body, "username");
        String registerId = JacksonUtil.parseString(body, "registerId");
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(registerId)) {
            return ResponseUtil.badArgument();
        }
        boolean verifyResult = residentService.verifyUserNameAndRegisterId(username, registerId);
        if(!verifyResult){
            return ResponseUtil.fail(USER_INVALID_NAME, "用户名或登记号不正确");
        }
        Map<String, Object> data = new HashMap<>();
        RemainWater remainWater = remainWaterService.findByRegisterId(registerId);
        LocalDateTime updateTime = remainWater.getUpdateTime();
        BigDecimal remainVolume = remainWater.getRemainVolume();
        data.put("updateTime", updateTime);
        data.put("remainVolume", remainVolume);
        return ResponseUtil.ok(data);
    }

    /**
     * 查询充值记录
     * @param body
     * @return Map<String, Object>对象
     *         "updateTime" : updateTime
     *         "remainVolume" : remainVolume
     *         "rechargeRecords" : List<Map<String, Object>>
     *             "amount" : amount
     *             "rechargeTime" : rechargeTime
     *             "username" : username
     *             "state" : state
     *             "delegate" : delegate
     */
    @Transactional
    public Object rechargeRecordInfo(String body) {
        Map<String, Object> remainWaterResult = (Map<String, Object>) remainWaterInfo(body);
        if(0 != (Integer) remainWaterResult.get("errno")) {
            return ResponseUtil.fail();
        }
        String registerId = JacksonUtil.parseString(body, "registerId");
        Integer year = JacksonUtil.parseInteger(body, "year");
        Integer startMonth = JacksonUtil.parseInteger(body, "startMonth");
        Integer endMonth = JacksonUtil.parseInteger(body, "endMonth");
        LocalDateTime startDateTime = LocalDateTime.of(year, startMonth, 1, 0, 0, 0);
        LocalDate endDate = LocalDate.of(year, endMonth, 1);
        Integer lastDayOfEndMonth = endDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        LocalDateTime endDateTime = LocalDateTime.of(year, endMonth, lastDayOfEndMonth, 23, 59, 59);
        List<RechargeRecord> rechargeRecords = rechargeRecordService.findByRegisterIdAndTime(registerId, startDateTime, endDateTime);
        List<Map<String, Object>> rechargeRecordsData = new ArrayList<>();
        for (RechargeRecord rechargeRecord : rechargeRecords) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("amount", rechargeRecord.getAmount());
            temp.put("rechargeTime", rechargeRecord.getRechargeTime());
            temp.put("rechargeVolume", rechargeRecord.getRechargeVolume());
            temp.put("username", rechargeRecord.getUserName());
            temp.put("state", rechargeRecord.getState());
            temp.put("delegate", rechargeRecord.getDelegate());
            rechargeRecordsData.add(temp);
        }
        Map<String, Object> data = (Map<String, Object>) remainWaterResult.get("data");
        data.put("rechargeRecords", rechargeRecordsData);
        return ResponseUtil.ok(data);
    }
}
