package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/resident")
@Validated
public class ResidentController {
    @Autowired
    private ResidentService residentService;

    @Autowired
    private CorrAddressService corrAddressService;

    GetNumCode getNumCode = new GetNumCode();

    //获取居民信息列表，包含带搜索条件查询
    @GetMapping("/list")
    public Object list(String user_name,
                       String address,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
        List<String> address_nums = corrAddressService.queryAddress(address);
        Page<Resident> pageResident = residentService.querySelective(user_name, address_nums, page, limit);
        List<Resident> residentList = pageResident.getContent();
        int total = pageResident.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total",total);
        data.put("resident", residentList);

        return ResponseUtil.ok(data);
    }

    //新增居民信息
    @PostMapping("/create")
    public Object create(@RequestBody Resident resident, @RequestParam Integer userid) {
        //生成登记号
        resident.initRegisterId();
        List<Resident> residents_device = residentService.queryDevice(resident.getAddressNum(), resident.getRoomNum());
        List<String> deviceSeqs = new ArrayList<>();
        for (Resident resident1:residents_device) {
            deviceSeqs.add(resident1.getDeviceSeq());
        }
        for(int i=0; i<36; i++) {
            String num = getNumCode.getOneNum(i);
            if(num.compareTo(deviceSeqs.get(i))==-1) {
                resident.setDeviceSeq(num);
            }
        }
        Resident resident1 = residentService.addResident(resident, userid);
        return ResponseUtil.ok(resident1);
    }

    //修改居民信息
    @PostMapping("/update")
    public Object update(@RequestBody Resident resident, @RequestParam Integer userid) {
        residentService.updateResident(resident, userid);
        return ResponseUtil.ok();

    }

    //删除居民信息
    @PostMapping("/delete")
    public Object delete(@RequestBody Resident resident, @RequestParam Integer userid) {
        Integer id = resident.getId();
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        residentService.deleteResident(id, userid);
        return ResponseUtil.ok();
    }


}
