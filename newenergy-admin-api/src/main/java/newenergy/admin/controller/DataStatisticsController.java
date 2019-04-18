package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.Resident;
import newenergy.db.domain.StatisticConsume;
import newenergy.db.domain.StatisticPlotRecharge;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.ResidentService;
import newenergy.db.service.StatisticConsumeService;
import newenergy.db.service.StatisticPlotRechargeService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                          @RequestParam String year,
                          @RequestParam String month) {
        if(plotNum==null||year==null||month==null||interval.length==0) {
            return ResponseUtil.badArgument();
        }
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        Integer[] households = new Integer[interval.length+1];
        String[] proportion = new String[interval.length+1];
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.parse(year +"-"+ month, df);

        for(Resident resident: residents) {
            StatisticConsume statisticConsume = statisticConsumeService.findByRegisterIdAndUpdateTime(resident.getRegisterId(), currentTime);
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
            proportion[i] = nf.format(households[i]/residents.size());
        }
        Map<String, Object> data = new HashMap<>();
        data.put("households", households);
        data.put("proportion", proportion);
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
    public Object getPlotData(@RequestParam String year,
                              @RequestParam String month,
                              @RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "10") Integer limit,
                              @RequestParam String plotNum) {
        if(year==null||month==null) {
            return ResponseUtil.badArgument();
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime curTime = LocalDateTime.parse(year +"-"+ month, df);
        Page<StatisticPlotRecharge> pagePlotRecharges = statisticPlotRechargeService.curPlotRecharge(curTime, plotNum, page, limit);
        List<StatisticPlotRecharge> plotRecharges = pagePlotRecharges.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("total", pagePlotRecharges.getTotalElements());
        data.put("plotRecharges", plotRecharges);
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
    public Object getResidentByPlot(@RequestParam String plotNum,
                                    @RequestParam String registerId,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "10") Integer limit) {
        Page<Resident> residentPage = residentService.findByPlotNumAndRegisterId(plotNum, registerId, page, limit);
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

}
