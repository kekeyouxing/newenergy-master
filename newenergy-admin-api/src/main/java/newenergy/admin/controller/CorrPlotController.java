package newenergy.admin.controller;

import newenergy.admin.background.service.DeviceRequireService;
import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrAddress;
import newenergy.db.domain.CorrPlot;
import newenergy.db.domain.CorrPump;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.util.*;

@RestController
@RequestMapping("/admin/corrPlot")
@Validated
public class CorrPlotController {
    @Autowired
    private CorrPlotService corrPlotService;

    @Autowired
    private CorrAddressService corrAddressService;

    @Autowired
    private CorrPumpService corrPumpService;

    /**
     * by Zeng Hui
     */
    @Autowired
    private CorrPlotAdminService corrPlotAdminService;
    @Autowired
    private DeviceRequireService deviceRequireService;

    GetNumCode getNumCode = new GetNumCode();


    /**
     * 获取小区列表，可以根据小区名搜索
     * @param plotDtl
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Object list(String plotDtl,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrPlot> pagePlot = corrPlotService.querySelective(plotDtl, page-1, limit);
        List<CorrPlot> corrPlots = pagePlot.getContent();
        Long total = pagePlot.getTotalElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrPlot", corrPlots);
        return ResponseUtil.ok(data);
    }

    /**
     * 获取小区名称下拉框选项，按照小区名称首字母排序
     * @return
     */
    @GetMapping("options")
    public Object options() {
        List<CorrPlot> corrPlots = corrPlotService.findAll();
        Collections.sort(corrPlots, new Comparator<CorrPlot>() {
            @Override
            public int compare(CorrPlot o1, CorrPlot o2) {
                return Collator.getInstance(Locale.CHINESE).compare(o1.getPlotDtl(), o2.getPlotDtl());
            }
        });
        List<Map<String, Object>> options = new ArrayList<>(corrPlots.size());
        for(CorrPlot corrPlot: corrPlots) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", corrPlot.getPlotNum());
            option.put("label", corrPlot.getPlotDtl());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    /**
     * 新增小区表
     * @param corrPlot
     * @return
     */
    @PostMapping("/create")
    public Object create(@RequestBody CorrPlot corrPlot, Integer userid) {
        List<CorrPlot> corrPlots = corrPlotService.findAll();
        List<String> plotNums = new ArrayList<>();
        for (CorrPlot plot: corrPlots) {
            if(plot.getPlotDtl().equals(corrPlot.getPlotDtl())){
                return ResponseUtil.fail(1,"数据已存在");
            }
            plotNums.add(plot.getPlotNum());
        }
        int i;
        for(i=0; i<plotNums.size(); i++) {
            String num = getNumCode.getTwoNum(i);
            if(num.compareTo(plotNums.get(i))<0) {
                corrPlot.setPlotNum(num);
                break;
            }
        }
        if(i==plotNums.size())
        {
            corrPlot.setPlotNum(getNumCode.getTwoNum(i));
        }
//        Integer userid = admin.getId();
        CorrPlot corrPlot1 = corrPlotService.addCorrPlot(corrPlot, userid);

        /**
         * by Zeng Hui
         * 添加小区信息的时候，
         * 在"数据对应表-小区管理人员"和"实时流量需求表"添加记录
         */
        corrPlotAdminService.addARecord(corrPlot1,userid);
        deviceRequireService.addPlot(corrPlot.getPlotNum(),userid);

        return ResponseUtil.ok(corrPlot1);
    }

    /**
     * 修改小区表，地址和机房表相应更改
     * @param corrPlot
     * @param userid
     * @return
     */
    @PostMapping("/update")
    public Object update(@RequestBody CorrPlot corrPlot, Integer userid) {
        corrPlotService.updateCorrPlot(corrPlot, userid);
        List<CorrAddress> corrAddresses = corrAddressService.findByPlotNum(corrPlot.getPlotNum());
        for(CorrAddress corrAddress: corrAddresses){
            corrAddress.setAddressPlot(corrPlot.getPlotDtl());
            corrAddress.initAddressDtl();
            corrAddressService.updateCorrAddress(corrAddress, userid);
        }
        List<CorrPump> corrPumps = corrPumpService.findByPlotNum(corrPlot.getPlotNum());
        for(CorrPump corrPump: corrPumps) {
            corrPump.setPlot(corrPlot.getPlotDtl());
            corrPump.setPumpDtl(corrPlot.getPlotDtl()+corrPump.getPump()+"号机房");
            corrPumpService.updateCorrPump(corrPump, userid);
        }
        return ResponseUtil.ok();
    }


    /**
     * 删除一条小区纪录，地址和机房相应删除
     * @param userid
     * @return
     */
    @GetMapping("/delete")
    public Object delete(@RequestParam Integer id, Integer userid) {
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        CorrPlot corrPlot = corrPlotService.findById(id);
        corrPlotService.deleteCorrPlot(id, userid);
        List<CorrAddress> corrAddresses = corrAddressService.findByPlotNum(corrPlot.getPlotNum());
        for(CorrAddress corrAddress: corrAddresses) {
            corrAddressService.deleteCorrAddress(corrAddress.getId(), userid);
        }
        List<CorrPump> corrPumps = corrPumpService.findByPlotNum(corrPlot.getPlotNum());
        for(CorrPump corrPump: corrPumps) {
            corrPumpService.deleteCorrPump(corrPump.getId(), userid);
        }

        /**
         * by Zeng Hui
         * 添加小区信息的时候，
         * 在"数据对应表-小区管理人员"和"实时流量需求表"添加记录
         */
        corrPlotAdminService.deleteARecord(corrPlot.getPlotNum(),userid);
        deviceRequireService.deletePlot(corrPlot.getPlotNum(),userid);

        return ResponseUtil.ok();
    }

}
