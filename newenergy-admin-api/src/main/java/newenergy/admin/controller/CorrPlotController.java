package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrPlot;
import newenergy.db.service.CorrPlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/corrPlot")
@Validated
public class CorrPlotController {
    @Autowired
    private CorrPlotService corrPlotService;

    GetNumCode getNumCode = new GetNumCode();

    //根据小区名获取列表
    @GetMapping("/list")
    public Object list(String plot_dtl,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrPlot> pagePlot = corrPlotService.querySelective(plot_dtl, page, limit);
        List<CorrPlot> corrPlots = pagePlot.getContent();
        int total = pagePlot.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrPlot", corrPlots);
        return ResponseUtil.ok(data);
    }

    //获取小区下拉框选项
    @GetMapping("options")
    public Object options() {
        List<CorrPlot> corrPlots = corrPlotService.findAll();

        List<Map<String, Object>> options = new ArrayList<>(corrPlots.size());
        for(CorrPlot corrPlot: corrPlots) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", corrPlot.getPlotNum());
            option.put("label", corrPlot.getPlotDtl());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    //新增小区信息
    @PostMapping("/create")
    public Object create(@RequestBody CorrPlot corrPlot, @RequestParam Integer userid) {
        List<CorrPlot> corrPlots = corrPlotService.findAll();
        String plot_num = getNumCode.getTwoNum(corrPlots.size());
        corrPlot.setPlotNum(plot_num);
        CorrPlot corrPlot1 = corrPlotService.addCorrPlot(corrPlot, userid);
        return ResponseUtil.ok(corrPlot1);
    }

}
