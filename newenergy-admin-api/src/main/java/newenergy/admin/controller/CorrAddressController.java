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


    /**
     * 获取数据相关表-地址表全部纪录，可根据搜索条件查找
     * @param addressDtl
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Object list(String addressDtl,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrAddress> pageAddress = corrAddressService.querySelective(addressDtl, page-1, limit);
        List<CorrAddress> corrAddresses = pageAddress.getContent();
        int total = pageAddress.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrAddress", corrAddresses);
        return ResponseUtil.ok(data);
    }

    /**
     * 获取地址下拉框选项
     * @return
     */
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

    /**
     * 新增地址表
     * @param corrAddress
     * @param userid
     * @return
     */
    @PostMapping("/create")
    public Object create(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        String plotNum = corrPlotService.findPlotNum(corrAddress.getAddressPlot());
        String adressNum = getNumCode.getAddressNum(plotNum, corrAddress.getAddressBlock(), corrAddress.getAddressUnit());
        corrAddress.setAddressNum(adressNum);
        corrAddress.initAddressDtl();
        CorrAddress corrAddress1 = corrAddressService.addCorrAddress(corrAddress, userid);
        return ResponseUtil.ok(corrAddress1);
    }

    /**
     * 修改地址表
     * @param corrAddress
     * @param userid
     * @return
     */
    @PostMapping("/update")
    public Object update(@RequestBody CorrAddress corrAddress, @RequestParam Integer userid) {
        corrAddress.initAddressDtl();
        corrAddressService.updateCorrAddress(corrAddress, userid);
        return ResponseUtil.ok();
    }

    /**
     * 删除地址表数据
     * @param corrAddress
     * @param userid
     * @return
     */
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
