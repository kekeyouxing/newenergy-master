package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.Resident;
import newenergy.db.domain.StatisticConsume;
import newenergy.db.domain.StatisticPlotRecharge;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin/data")
@Validated
public class DataStatisticsController {

    @Autowired
    ResidentService residentService;

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    StatisticConsumeService statisticConsumeService;

    @Autowired
    StatisticPlotRechargeService statisticPlotRechargeService;

    @Autowired
    CorrPlotService corrPlotService;

    /**
     * 获取消费统计表数据
     * @param plotNum  小区编号
     * @param interval   间隔区间
     * @param year   年
     * @param month  月
     * @return
     */
    @GetMapping("/getConsumeData")
    public Object getConsumeData(@RequestParam String plotNum,
                          @RequestParam BigDecimal[] interval,
                          @RequestParam Integer year,
                          @RequestParam Integer month) {
        if(plotNum==null||year==null||month==null||interval.length==0) {
            return ResponseUtil.badArgument();
        }
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        Integer[] households = new Integer[interval.length+1];
        for(int j=0; j<households.length; j++) {
            households[j]=0;
        }
        String[] proportion = new String[interval.length+1];
        LocalDate curTime = LocalDate.of(year, month, 1).plusMonths(1);
        for(Resident resident: residents) {
            StatisticConsume statisticConsume = statisticConsumeService.findByRegisterIdAndUpdateTime(resident.getRegisterId(), curTime);
            if(statisticConsume==null){
                continue;
            }
            BigDecimal curUsed = statisticConsume.getCurUsed();
            if(curUsed.compareTo(interval[0])==-1) {
                households[0]+=1;
            }
            if((curUsed.compareTo(interval[interval.length-1])==1)
                    ||(curUsed.compareTo(interval[interval.length-1])==0)) {
                households[interval.length] += 1;
            }
            for(int i=1; i<interval.length; i++) {
                if(((curUsed.compareTo(interval[i-1])==1)||(curUsed.compareTo(interval[i-1])==0))
                        &&(curUsed.compareTo(interval[i])==-1)) {
                    households[i]+=1;
                }
            }
        }
        DecimalFormat nf = (DecimalFormat) NumberFormat.getCurrencyInstance();
        nf.applyPattern("00%");
        nf.setMaximumFractionDigits(2);
        for (int i=0; i<households.length; i++) {
            proportion[i] = nf.format((double)households[i]/residents.size());
        }
        List<Map<String, Object>> data = new ArrayList<>();
        for(int j=0; j<households.length; j++) {
            Map<String, Object> info = new HashMap<>();
            info.put("households", households[j]);
            info.put("proportion", proportion[j]);
            data.add(info);
        }
        return ResponseUtil.ok(data);
    }


    /**
     * 获取小区充值及消费月报表
     * @param year
     * @param month
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/getPlotData")
    public Object getPlotData(@RequestParam Integer year,
                              @RequestParam Integer month,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer limit,
                              String plotNum) {
        if(year==null||month==null) {
            return ResponseUtil.badArgument();
        }
        LocalDate curTime = LocalDate.of(year, month, 1).plusMonths(1);
        Page<StatisticPlotRecharge> pagePlotRecharges = statisticPlotRechargeService.curPlotRecharge(curTime, plotNum, page-1, limit);
        List<StatisticPlotRecharge> plotRecharges = pagePlotRecharges.getContent();
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for(StatisticPlotRecharge plotRecharge : pagePlotRecharges) {
            Map<String, Object> info = new HashMap<>();
            info.put("plotDtl", corrPlotService.findByPlotNum(plotRecharge.getPlotNum()));
            info.put("amount", plotRecharge.getAmount());
            info.put("plotFactor", plotRecharge.getPlotFactor());
            info.put("curRecharge", plotRecharge.getRechargeVolume());
            info.put("curUsed", plotRecharge.getCurUsed());
            list.add(info);
        }
        data.put("total", pagePlotRecharges.getTotalElements());
        data.put("plotRecharges", list);
        return ResponseUtil.ok(data);
    }

    /**
     * 获取小区居民信息
     * @param plotNum   小区编号
     * @param registerId    登记号
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/getResidentByPlot")
    public Object getResidentByPlot(String plotNum,
                                    String registerId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer limit) {
        Page<Resident> residentPage = residentService.findByPlotNumAndRegisterId(plotNum, registerId, page-1, limit);
        List<Resident> residents = residentPage.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("total", residentPage.getTotalElements());
        data.put("resident", residents);
        return ResponseUtil.ok(data);
    }


    /**
     * 点击登记号，获取居民充值记录
     * @param registerId   登记号
     * @return   居民信息，充值记录
     */
    @GetMapping("/getRechargeRecord")
    public Object getRechargeRecord(@RequestParam String registerId) {
        Resident resident = residentService.fingByRegisterId(registerId);
        List<RechargeRecord> rechargeRecords = rechargeRecordService.findByRegisterId(registerId);
        Map<String, Object> data = new HashMap<>();
        data.put("resident", resident);
        data.put("rechargeRecords", rechargeRecords);
        return ResponseUtil.ok(data);
    }


    /**
     * 获取用户消费明细月报表
     * @param year
     * @param month
     * @param plotNum   按小区查询
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/getConsumeDetail")
    public Object getConsumeDetail(BigDecimal start,
                                   BigDecimal end,
                                   @RequestParam Integer year,
                                @RequestParam Integer month,
                                @RequestParam String plotNum,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer limit) {
        LocalDate curTime = LocalDate.of(year, month, 1).plusMonths(1);
        Page<StatisticConsume> pageConsume = statisticConsumeService.getCurConsume(page-1, limit, curTime, plotNum, start, end);
        List<StatisticConsume> listConsume = pageConsume.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageConsume.getTotalElements());
        data.put("consumeDetail", listConsume);
        return ResponseUtil.ok(data);
    }


}
