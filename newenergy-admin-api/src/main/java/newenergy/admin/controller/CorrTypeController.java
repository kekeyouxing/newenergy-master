package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrType;
import newenergy.db.service.CorrTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/corrType")
@Validated
public class CorrTypeController {
    @Autowired
    private CorrTypeService corrTypeService;

    GetNumCode getNumCode = new GetNumCode();

    //获取所有纪录列表
    @GetMapping("/list")
    public Object list(String type_dtl,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrType> pageType = corrTypeService.querySelective(type_dtl, page, limit);
        List<CorrType> corrTypes = pageType.getContent();
        int total = pageType.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrType", corrTypes);
        return ResponseUtil.ok(data);
    }

    //获取机型下拉框选项
    @GetMapping("/options")
    public Object options() {
        List<CorrType> corrTypes = corrTypeService.findAll();
        List<Map<String, Object>> options = new ArrayList<>(corrTypes.size());
        for(CorrType corrType : corrTypes) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", corrType.getTypeNum());
            option.put("label", corrType.getTypeDtl());
            option.put("ratedFlow", corrType.getRatedFlow());
            options.add(option);
        }
        return ResponseUtil.ok(options);
    }

    //新增纪录
    @PostMapping("create")
    public Object create(@RequestBody CorrType corrType, @RequestParam Integer userid) {
        List<CorrType> corrTypes = corrTypeService.findAll();
        List<String> typeNums = new ArrayList<>();
        for(CorrType corrType1: corrTypes) {
            typeNums.add(corrType1.getTypeNum());
        }
        for(int i=0; i<1296; i++) {
            String num = getNumCode.getTwoNum(i);
            if(num.compareTo(typeNums.get(i))==-1) {
                corrType.setTypeNum(num);
            }
        }
        CorrType corrType1 = corrTypeService.addCorrType(corrType, userid);
        return ResponseUtil.ok(corrType1);
    }

    //修改记录
    @PostMapping("/update")
    public Object update(@RequestBody CorrType corrType, @RequestParam Integer userid) {
        corrTypeService.updateCorrType(corrType, userid);
        return ResponseUtil.ok();
    }

    //删除记录
    @PostMapping("/delete")
    public Object delete(@RequestBody CorrType corrType, @RequestParam Integer userid) {
        Integer id = corrType.getId();
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        corrTypeService.deleteCorrType(id, userid);
        return ResponseUtil.ok();
    }
}
