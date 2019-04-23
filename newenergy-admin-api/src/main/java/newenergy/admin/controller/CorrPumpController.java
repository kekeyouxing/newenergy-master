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

    /**
     * 获取机房信息列表
     * @param pumpDtl
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Object list(String pumpDtl,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrPump> pagePump = corrPumpService.querySelective(pumpDtl, page-1, limit);
        List<CorrPump> corrPumps = pagePump.getContent();
        int total = pagePump.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrPump", corrPumps);
        return ResponseUtil.ok(data);
    }

    /**
     * 获取机房信息下拉框选项
     * @return
     */
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

    /**
     * 增加机房信息
     * @param corrPump
     * @param userid
     * @return
     */
    @PostMapping("/create")
    //改成小区+机房，数据库多加一个字段，编号为4位
    public Object create(@RequestBody CorrPump corrPump, @RequestParam Integer userid){
        String plotNum = corrPlotService.findPlotNum(corrPump.getPlot());
        String pumpNum = plotNum + getNumCode.getTwoNum(corrPump.getPump());
        corrPump.setPumpNum(pumpNum);
        corrPump.setPumpDtl(corrPump.getPlot()+corrPump.getPump()+"号机房");
        CorrPump corrPump1 = corrPumpService.addCorrPump(corrPump, userid);
        return ResponseUtil.ok(corrPump1);
    }

    /**
     * 修改机房信息（问题同地址表，修改是否需要修改编号)
     * @param corrPump
     * @param userid
     * @return
     */
    @PostMapping("/update")
    public Object update(@RequestBody CorrPump corrPump, @RequestParam Integer userid) {
        corrPumpService.updateCorrPump(corrPump, userid);
        return ResponseUtil.ok();
    }

    /**
     * 删除机房信息
     * @param corrPump
     * @param userid
     * @return
     */
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

