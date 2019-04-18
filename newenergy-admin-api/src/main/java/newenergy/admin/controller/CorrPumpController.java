package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrPump;
import newenergy.db.service.CorrPlotService;
import newenergy.db.service.CorrPumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/corrPump")
public class CorrPumpController {
    @Autowired
    private CorrPumpService corrPumpService;

    @Autowired
    private CorrPlotService corrPlotService;

    GetNumCode getNumCode = new GetNumCode();

    //根据机房信息查找纪录
    @GetMapping("/list")
    public Object list(String pump_dlt,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrPump> pagePump = corrPumpService.querySelective(pump_dlt, page, limit);
        List<CorrPump> corrPumps = pagePump.getContent();
        int total = pagePump.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrPump", corrPumps);
        return ResponseUtil.ok(data);
    }

    //获取机房信息下拉框选项
    @GetMapping("/options")
    public Object options() {
        List<CorrPump> corrPumps = corrPumpService.findAll();
        List<Map<String, Object>> options = new ArrayList<>(corrPumps.size());
        for(CorrPump corrPump : corrPumps) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", corrPump.getPumpNum());
            option.put("plot", corrPump.getPlot());
            option.put("pump", corrPump.getPump());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    //增加机房信息
    @PostMapping("/create")
    //改成小区+机房，数据库多加一个字段，编号为4位
    public Object create(@RequestBody CorrPump corrPump, @RequestParam Integer userid){
        String plot_num = corrPlotService.findPlotNum(corrPump.getPlot());
        String pump_num = plot_num + getNumCode.getTwoNum(corrPump.getPump());
        corrPump.setPumpNum(pump_num);
        CorrPump corrPump1 = corrPumpService.addCorrPump(corrPump, userid);
        return ResponseUtil.ok(corrPump1);
    }

    //修改机房信息
    @PostMapping("/update")
    public Object update(@RequestBody CorrPump corrPump, @RequestParam Integer userid) {
        corrPumpService.updateCorrPump(corrPump, userid);
        return ResponseUtil.ok();
    }

    //删除记录
    @PostMapping("/delete")
    public Object delete(@RequestBody CorrPump corrPump, @RequestParam Integer userid) {
        Integer id = corrPump.getId();
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        corrPumpService.deleteCorrPump(id, userid);
        return ResponseUtil.ok();
    }
}

