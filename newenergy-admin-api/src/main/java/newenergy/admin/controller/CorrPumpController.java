package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrPump;
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
            option.put("label", corrPump.getPumpDlt());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    @PostMapping("/create")
    public Object create(@RequestBody CorrPump corrPump, @RequestParam Integer userid){
        List<CorrPump> corrPumps = corrPumpService.findAll();
        String pump_num = getNumCode.getTwoNum(corrPumps.size());
        corrPump.setPumpNum(pump_num);
        CorrPump corrPump1 = corrPumpService.addCorrPump(corrPump, userid);
        return ResponseUtil.ok(corrPump1);
    }
}
