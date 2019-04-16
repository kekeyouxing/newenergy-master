package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.Resident;
import newenergy.db.service.RechargeRecordService;
import newenergy.db.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                          @RequestParam List<Integer> interval,
                          @RequestParam String year,
                          @RequestParam String month) {
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        for(Resident resident: residents) {

        }
        return null;
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
                              @RequestParam String page,
                              @RequestParam String limit) {
        return null;
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
