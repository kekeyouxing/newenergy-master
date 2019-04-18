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
            option.put("label", corrAddress.getAddressPlot());
            option.put("block", corrAddress.getAddressBlock());
            option.put("unit", corrAddress.getAddressUnit());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    //新增地址信息表数据
    @PostMapping("/create")
    public Object create(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        String plot_num = corrPlotService.findPlotNum(corrAddress.getAddressPlot());
        String adress_num = getNumCode.getAddressNum(plot_num, corrAddress.getAddressBlock(), corrAddress.getAddressUnit());
        corrAddress.setAddressNum(adress_num);
        corrAddress.setAddressDtl(corrAddress.getAddressPlot()+corrAddress.getAddressBlock()+"栋"+corrAddress.getAddressUnit()+"单元");
        CorrAddress corrAddress1 = corrAddressService.addCorrAddress(corrAddress, userid);
        return ResponseUtil.ok(corrAddress1);
    }

    //修改地址信息表数据
    @PostMapping("/update")
    public Object update(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        corrAddressService.updateCorrAddress(corrAddress, userid);
        return ResponseUtil.ok();
    }

    //删除地址信息表数据
    @PostMapping("/delete")
    public Object delete(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        Integer id = corrAddress.getId();
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        corrAddressService.deleteCorrAddress(id, userid);
        return ResponseUtil.ok();
    }
}
