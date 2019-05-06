package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.Resident;
import newenergy.db.domain.StatisticConsume;
import newenergy.db.domain.StatisticPlotRecharge;
import newenergy.db.service.*;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    CorrTypeService corrTypeService;

    @Autowired
    CorrAddressService corrAddressService;

    @Autowired
    CorrPumpService corrPumpService;

    /**
     * 获取消费统计表数据

     * @return
     */
    @PostMapping("/getConsumeData")
    public Object getConsumeData(
                                 @RequestBody Map<String, Object> params) {
        String plotNum = (String) params.get("plotNum");
        Integer year = (Integer) params.get("year");
        Integer month = (Integer) params.get("month");
        List<Integer> intervalList = (ArrayList)params.get("interval");
        List<BigDecimal> interval = new ArrayList<>();
        for(int i=0; i<intervalList.size(); i++){
            interval.add(new BigDecimal(intervalList.get(i)));
        }
        if(plotNum==null||year==null||month==null||interval.size()==0) {
            return ResponseUtil.badArgument();
        }
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        if(residents.size()==0){
            return ResponseUtil.fail(1,"小区没居民哦");
        }
        Integer[] households = new Integer[interval.size()+1];
        for(int j=0; j<households.length; j++) {
            households[j]=0;
        }
        String[] proportion = new String[interval.size()+1];
        LocalDate curTime = LocalDate.of(year, month, 1).plusMonths(1);
        for(Resident resident: residents) {
            StatisticConsume statisticConsume = statisticConsumeService.findByRegisterIdAndUpdateTime(resident.getRegisterId(), curTime);
            if(statisticConsume==null){
                continue;
            }
            BigDecimal curUsed = statisticConsume.getCurUsed();
            if(curUsed.compareTo(interval.get(0))<0) {
                households[0]+=1;
            }
            if((curUsed.compareTo(interval.get(interval.size()-1))>=0)) {
                households[interval.size()] += 1;
            }
            for(int i=1; i<interval.size(); i++) {
                if(((curUsed.compareTo(interval.get(i-1))>=0))
                        &&(curUsed.compareTo(interval.get(i))<0)) {
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
     * @return
     */
    @PostMapping("/getPlotData")
    public Object getPlotData(@RequestBody Map<String, Object> params) {
        Integer year = (Integer)params.get("year");
        Integer month = (Integer)params.get("month");
        Integer page = (Integer)params.get("page");
        Integer limit = (Integer)params.get("limit");
        String plotNum = (String)params.get("plotNum");
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
     * @return
     */
    @PostMapping("/getResidentByPlot")
    public Object getResidentByPlot(@RequestBody Map<String, Object> params) {
        BigDecimal start = new BigDecimal(0);
        BigDecimal end = new BigDecimal(0);
        if(params.get("start")!=null){
            start = new BigDecimal((Integer) params.get("start"));
        }
        if(params.get("end")!=null){
            end = new BigDecimal((Integer) params.get("end"));
        }
        String plotNum = (String)params.get("plotNum");
        String registerId = (String)params.get("registerId");
        Integer page =(Integer)params.get("page");
        Integer limit = (Integer)params.get("limit");
        Page<Resident> residentPage = residentService.findByPlotNumAndRegisterId(plotNum, registerId, page-1, limit, start, end);
        List<Resident> residents = residentPage.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("total", residentPage.getTotalElements());
        List<Map<String, Object>> list = new ArrayList<>();
        for(Resident resident: residents){
            Map<String, Object> info = new HashMap<>();
            info.put("id", resident.getId());
            info.put("registerId",resident.getRegisterId());
            info.put("userName", resident.getUserName());
            info.put("phone", resident.getPhone());
            info.put("plotDtl", corrPlotService.findByPlotNum(residentService.findPlotNumByRegisterid(resident.getRegisterId(),0)));
            info.put("addressDtl", corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            info.put("roomNum", resident.getRoomNum());
            info.put("area", resident.getArea());
            info.put("buyTime", resident.getBuyTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("typeDtl", corrTypeService.findByTypeNum(resident.getTypeNum()).getTypeDtl());
            info.put("ratedFlow", corrTypeService.findByTypeNum(resident.getTypeNum()).getRatedFlow());
            info.put("deviceNum", resident.getDeviceNum());
            info.put("deviceSeq", resident.getDeviceSeq());
            info.put("pumpDtl", corrPumpService.findByPlotNum(resident.getPumpNum()).get(0).getPumpDtl());
            info.put("installTime", resident.getInstallTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("receiveTime", resident.getReceiveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("openid", resident.getOpenid());
            list.add(info);
        }
        data.put("resident", list);
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
        Map<String, Object> firstLine = new HashMap<>();
        firstLine.put("registerId", resident.getRegisterId());
        firstLine.put("userName", resident.getUserName());
        firstLine.put("phone", resident.getPhone());
        firstLine.put("typeDtl", corrTypeService.findByTypeNum(resident.getTypeNum()).getTypeDtl());
        firstLine.put("ratedFlow", corrTypeService.findByTypeNum(resident.getTypeNum()).getRatedFlow());
        firstLine.put("plotNum", corrPlotService.findByPlotNum(resident.getPlotNum()));
        Map<String, Object> secondLine = new HashMap<>();
        secondLine.put("deviceNum", resident.getDeviceNum());
        secondLine.put("addressDtl", corrAddressService.findByPlotNum(resident.getAddressNum()).get(0).getAddressDtl());
        secondLine.put("roomNum", resident.getRoomNum());
        secondLine.put("plotFactor", corrPlotService.findPlotFacByPlotNum(resident.getPlotNum()));
        data.put("firstLine", firstLine);
        data.put("secondLine", secondLine);
        List<Map<String, Object>> list = new ArrayList<>();
        for(RechargeRecord rechargeRecord: rechargeRecords){
            Map<String, Object> info = new HashMap<>();
            info.put("rechargeTime", rechargeRecord.getRechargeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            info.put("amount", rechargeRecord.getAmount());
            info.put("rechargeVolume", rechargeRecord.getRechargeVolume());
            info.put("remainVolume", rechargeRecord.getRemainVolume());
            info.put("updatedVolume", rechargeRecord.getUpdatedVolume());
            list.add(info);
        }
        data.put("rechargeRecords", list);
        return ResponseUtil.ok(data);
    }


    /**
     * 获取用户消费明细月报表
     * @return
     */
    @PostMapping("/getConsumeDetail")
    public Object getConsumeDetail(@RequestBody Map<String, Object> params) {
        Integer year = (Integer) params.get("year");
        Integer month = (Integer)params.get("month");
        String plotNum = (String) params.get("plotNum");
        Integer page = (Integer)params.get("page");
        Integer limit = (Integer)params.get("limit");
        if(year==null||month==null) {
            return ResponseUtil.badArgument();
        }
        LocalDate curTime = LocalDate.of(year, month, 1).plusMonths(1);
        Page<StatisticConsume> pageConsume = statisticConsumeService.getCurConsume(page-1, limit, curTime, plotNum);
        List<StatisticConsume> listConsume = pageConsume.getContent();
        Map<String, Object> data = new HashMap<>();
        data.put("total", pageConsume.getTotalElements());
        List<Map<String, Object>> list = new ArrayList<>();
        for(StatisticConsume consume: listConsume){
            Map<String, Object> info = new HashMap<>();
            info.put("registerId", consume.getRegisterId());
            info.put("lastRemain", consume.getLastRemain());
            info.put("curRecharge", consume.getCurRecharge());
            info.put("curRemain", consume.getCurRemain());
            info.put("curUsed", consume.getCurUsed());
            info.put("plotDtl", corrPlotService.findByPlotNum(residentService.findPlotNumByRegisterid(consume.getRegisterId(),0)));
            list.add(info);
        }
        data.put("consumeDetail", list);
        return ResponseUtil.ok(data);
    }


}
