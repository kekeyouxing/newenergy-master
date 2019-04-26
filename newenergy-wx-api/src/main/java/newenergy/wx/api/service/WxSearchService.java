package newenergy.wx.api.service;

import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RemainWater;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.RemainWaterService;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WxSearchService {

    @Autowired
    private RemainWaterService remainWaterService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    /**
     *
     * @param username
     * @param registerId
     * @return
     */
    @Transactional
    public boolean verify(String username, String registerId){
        return residentService.verifyUserNameAndRegisterId(username, registerId);
    }

    /**
     * 获取剩余水量的信息
     * @param registerId 登记号
     * @return RemainWater
     */
    @Transactional
    public RemainWater remainWaterInfo(String registerId){
        return remainWaterService.findByRegisterId(registerId);
    }

    /**
     * 查询充值记录
     * @param registerId 登记号
     * @param year 年份
     * @param startMonth 起始月份
     * @param endMonth 结束月份
     * @return List<RechargeRecord>
     */
    @Transactional
    public List<RechargeRecord> rechargeRecordInfo(String registerId, Integer year, Integer startMonth, Integer endMonth){
        LocalDateTime startDateTime = LocalDateTime.of(year, startMonth, 1, 0, 0, 0);
        LocalDate endDate = LocalDate.of(year, endMonth, 1);
        Integer lastDayOfEndMonth = endDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        LocalDateTime endDateTime = LocalDateTime.of(year, endMonth, lastDayOfEndMonth, 23, 59, 59);
        return rechargeRecordService.findByRegisterIdAndTime(registerId, startDateTime, endDateTime);
    }
}