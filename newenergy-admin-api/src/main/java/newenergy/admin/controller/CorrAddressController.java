package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrAddress;
import newenergy.db.domain.CorrPlot;
import newenergy.db.service.CorrAddressService;
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
@RequestMapping("admin/corrAddress")
@Validated
public class CorrAddressController {
    @Autowired
    private CorrAddressService corrAddressService;

    @Autowired
    private CorrPlotService corrPlotService;

    GetNumCode getNumCode = new GetNumCode();

    //获取搜索列表
    @GetMapping("/list")
    public Object list(String address_dlt,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrAddress> pageAddress = corrAddressService.querySelective(address_dlt, page, limit);
        List<CorrAddress> corrAddresses = pageAddress.getContent();
        int total = pageAddress.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrAddress", corrAddresses);
        return ResponseUtil.ok(data);
    }

    //获取地址下拉框选项
    @GetMapping("/options")
    public Object options() {
        List<CorrAddress> corrAddresses = corrAddressService.findAll();
        List<Map<String, Object>> options = new ArrayList<>(corrAddresses.size());
        for(CorrAddress corrAddress: corrAddresses) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", corrAddress.getAddressNum());
            option.put("plot", corrAddress.getAddressPlot());
            option.put("block", corrAddress.getAddressBlock()+"栋"+corrAddress.getAddressUnit() + "单元");
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    //新增地址信息表数据
    @PostMapping("/create")
    public Object create(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        String plot_num = corrPlotService.fingPlotNum(corrAddress.getAddressPlot());
        String adress_num = getNumCode.getAddressNum(plot_num, corrAddress.getAddressBlock(), corrAddress.getAddressUnit());
        corrAddress.setAddressNum(adress_num);
        CorrAddress corrAddress1 = corrAddressService.addCorrAddress(corrAddress, userid);
        return ResponseUtil.ok(corrAddress1);
    }
}
