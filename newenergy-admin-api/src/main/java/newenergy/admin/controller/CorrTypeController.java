package newenergy.admin.controller;

import newenergy.admin.util.ExcelExport;
import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.CorrPump;
import newenergy.db.domain.CorrType;
import newenergy.db.service.CorrTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 获取机型信息列表
     * @param typeDtl
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Object list(String typeDtl,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        Page<CorrType> pageType = corrTypeService.querySelective(typeDtl, page-1, limit);
        List<CorrType> corrTypes = pageType.getContent();
        Long total = pageType.getTotalElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("corrType", corrTypes);
        return ResponseUtil.ok(data);
    }

    /**
     * 获取机型下拉框选项
     * @return
     */
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

    /**
     * 新增机型信息
     * @param corrType
     * @param userid
     * @return
     */
    @PostMapping("create")
    public Object create(@RequestBody CorrType corrType,Integer userid) {
        List<CorrType> corrTypes = corrTypeService.findAll();
        List<String> typeNums = new ArrayList<>();
        for(CorrType corrType1: corrTypes) {
            if(corrType1.getTypeDtl().equals(corrType.getTypeDtl())){
                return ResponseUtil.fail(1,"数据已存在");
            }
            typeNums.add(corrType1.getTypeNum());
        }
        int i;
        for(i=0; i<typeNums.size(); i++) {
            String num = getNumCode.getTwoNum(i);
            if(num.compareTo(typeNums.get(i))<0) {
                corrType.setTypeNum(num);
                break;
            }
        }
        if(i==typeNums.size()){
            corrType.setTypeNum(getNumCode.getTwoNum(i));
        }
        CorrType corrType1 = corrTypeService.addCorrType(corrType, userid);
        return ResponseUtil.ok(corrType1);
    }

    /**
     * 修改
     * @param corrType
     * @param userid
     * @return
     */
    @PostMapping("/update")
    public Object update(@RequestBody CorrType corrType, Integer userid) {
        corrTypeService.updateCorrType(corrType, userid);
        return ResponseUtil.ok();
    }

    /**
     * 删除
     * @param userid
     * @return
     */
    @GetMapping("/delete")
    public Object delete(@RequestParam Integer id, Integer userid) {
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        corrTypeService.deleteCorrType(id, userid);
        return ResponseUtil.ok();
    }


    @GetMapping("/download")
    public void download(HttpServletResponse response){
        String[] headers = new String[]{"机型编号","安装机型","额定流量(T/h)","备注"};

        List<CorrType> types = corrTypeService.findAll();
        List<String[]> values = Obj2String(types);

        ExcelExport excel = new ExcelExport(headers, values);

        excel.exportExcel("机型信息表", response);
    }

    private List<String[]> Obj2String(List<CorrType> types) {
        List<String[]> values = new ArrayList<>();
        if(types!=null && types.size()!=0){
            for(CorrType type : types){
                Double ratedFlow = type.getRatedFlow();
                String ratedFlowStr = "";
                if(ratedFlow != null){
                    ratedFlowStr = ratedFlow.toString();
                }
                String[] corrStr = new String[]{type.getTypeNum(), type.getTypeDtl(), ratedFlowStr};
                values.add(corrStr);
            }
        }

        return values;
    }
}
