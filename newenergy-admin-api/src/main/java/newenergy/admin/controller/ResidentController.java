package newenergy.admin.controller;

import newenergy.admin.util.GetNumCode;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.*;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private CorrPlotService corrPlotService;

    @Autowired
    private CorrTypeService corrTypeService;

    @Autowired
    private CorrPumpService corrPumpService;

    GetNumCode getNumCode = new GetNumCode();

    /**
     * 获取居民列表，可以根据姓名和小区地址查找
     * @param userName
     * @param address
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Object list(String userName,
                       String address,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
        List<String> addressNums = new ArrayList<>();
        if(!StringUtils.isEmpty(address)){
            addressNums = corrAddressService.queryAddress(address);
        }
        Page<Resident> pageResident = residentService.querySelective(userName, addressNums, page-1, limit);
        List<Resident> residentList = pageResident.getContent();
        Long total = pageResident.getTotalElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total",total);
        List<Map<String, Object>> list = new ArrayList<>();
        for(Resident resident: residentList){
            Map<String, Object> info = new HashMap<>();
            info.put("id", resident.getId());
            info.put("registerId",resident.getRegisterId());
            info.put("userName", resident.getUserName());
            info.put("phone", resident.getPhone());
            info.put("plotDtl", corrPlotService.findByPlotNum(residentService.findPlotNumByRegisterid(resident.getRegisterId(),0)));
            info.put("addressDtl", corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            info.put("roomNum", resident.getRoomNum());
            info.put("area", resident.getArea());
            info.put("buyTime", resident.getBuyTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("typeDtl", corrTypeService.findByTypeNum(resident.getTypeNum()).getTypeDtl());
            info.put("ratedFlow", corrTypeService.findByTypeNum(resident.getTypeNum()).getRatedFlow());
            info.put("deviceNum", resident.getDeviceNum());
            info.put("deviceSeq", resident.getDeviceSeq());
            info.put("pumpDtl", corrPumpService.findByPlotNum(resident.getPumpNum()).get(0).getPumpDtl());
            info.put("installTime", resident.getInstallTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("receiveTime", resident.getReceiveTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            info.put("openid", resident.getOpenid());
            list.add(info);
        }
        data.put("resident", list);
        return ResponseUtil.ok(data);
    }

    /**
     * 新增居民信息
//     * @param buyTime
//     * @param installTime
//     * @param receiveTime
     * @param userid
     * @return
     */
    @PostMapping("/create")
    public Object create(@RequestBody Map<String, Object> params,
                         Integer userid) {
        Resident resident = new Resident();
        resident.setUserName((String)params.get("userName"));
        resident.setPlotNum((String)params.get("plotNum"));
        resident.setAddressNum((String)params.get("addressNum"));
        resident.setRoomNum((String)params.get("roomNum"));
        resident.setTypeNum((String)params.get("typeNum"));
        resident.setPumpNum((String)params.get("pumpNum"));
        resident.setPhone((String)params.get("phone"));
        resident.setArea(new BigDecimal((String) params.get("area")));
        resident.setRatedFlow(new BigDecimal((String) params.get("ratedFlow")));
        resident.setDeviceNum((String)params.get("deviceNum"));
        resident.setOpenid((String)params.get("openid"));
        resident.setBuyTime(LocalDate.parse((String)params.get("buyTime")));
        resident.setInstallTime(LocalDate.parse((String)params.get("installTime")));
        resident.setReceiveTime(LocalDate.parse((String)params.get("receiveTime")));
        List<Resident> residents_device = residentService.queryDevice(resident.getAddressNum(), resident.getRoomNum());
        List<String> deviceSeqs = new ArrayList<>();
        for (Resident resident1:residents_device) {
            deviceSeqs.add(resident1.getDeviceSeq());
        }
        int i ;
        for(i=0; i<deviceSeqs.size(); i++) {
            String num = getNumCode.getOneNum(i);
            if(num.compareTo(deviceSeqs.get(i))<0) {
                resident.setDeviceSeq(num);
                break;
            }
        }
        if(i==deviceSeqs.size()){
            resident.setDeviceSeq(getNumCode.getOneNum(i));
        }
        //生成登记号
        resident.initRegisterId();
        Resident resident1 = residentService.addResident(resident, userid);
        return ResponseUtil.ok(resident1);
    }

    /**
     * 修改居民信息
     * @param userid
     * @return
     */
    @PostMapping("/update")
    public Object update(@RequestBody Map<String, Object> params,
                         Integer userid) {
        Resident resident = new Resident();
        resident.setId((Integer)params.get("id"));
        resident.setUserName((String)params.get("userName"));
        resident.setDeviceNum((String)params.get("deviceNum"));
        resident.setPhone((String)params.get("phone"));
        resident.setArea(new BigDecimal((String)params.get("area")));
        resident.setOpenid((String)params.get("openid"));
        resident.setBuyTime(LocalDate.parse((String)params.get("buyTime")));
        resident.setInstallTime(LocalDate.parse((String)params.get("installTime")));
        resident.setReceiveTime(LocalDate.parse((String)params.get("receiveTime")));
        residentService.updateResident(resident, userid);
        return ResponseUtil.ok();
    }

    /**
     * 删除居民信息
     * @param userid
     * @return
     */
    @GetMapping("/delete")
    public Object delete(@RequestParam Integer id, Integer userid) {
        if(id==null) {
            return ResponseUtil.badArgument();
        }
        residentService.deleteResident(id, userid);
        return ResponseUtil.ok();
    }

    /**
     * 批量删除
     * @param userid
     * @return
     */
    @PostMapping("/batchDelete")
    public Object batchDelete(@RequestBody Map<String, Object> params, Integer userid){
        List<Integer> ids = (ArrayList)params.get("ids");
        for(int i=0; i<ids.size(); i++) {
            residentService.deleteResident(ids.get(i), userid);
        }
        return ResponseUtil.ok();

    }

    @PostMapping("/findResidentInfo")
    public Object findResidentInfo(@RequestBody PostInfo postInfo){
        BigDecimal start = new BigDecimal(0);
        List<Resident> residents = residentService.findByPlotNumAndRegisterId(postInfo.getPlotNum(),
                postInfo.getRegisterId(),
                postInfo.getPage()-1,
                postInfo.getLimit(),start, start).getContent();
        List<ResultInfo> resultInfos = new ArrayList<>();
        for (Resident resident:
             residents) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRegisterId(resident.getRegisterId());
            resultInfo.setUsername(resident.getUserName());
            resultInfo.setAddressDtl(corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()));
            resultInfo.setRoomNum(resident.getRoomNum());
            resultInfos.add(resultInfo);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("total",residentService.findByPlotNumAndRegisterIdSize(postInfo.getPlotNum(),postInfo.getRegisterId()));
        result.put("list",resultInfos);
        return result;

    }

    public static class PostInfo{
        private Integer operatorId;
        private String plotNum;
        private String registerId;
        private Integer page;
        private Integer limit;

        public Integer getOperatorId() {
            return operatorId;
        }

        public void setOperatorId(Integer operatorId) {
            this.operatorId = operatorId;
        }

        public String getPlotNum() {
            return plotNum;
        }

        public void setPlotNum(String plotNum) {
            this.plotNum = plotNum;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }

    private static class ResultInfo{
        private String registerId;
        private String username;
        private String addressDtl;
        private String roomNum;

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAddressDtl() {
            return addressDtl;
        }

        public void setAddressDtl(String addressDtl) {
            this.addressDtl = addressDtl;
        }

        public String getRoomNum() {
            return roomNum;
        }

        public void setRoomNum(String roomNum) {
            this.roomNum = roomNum;
        }
    }

}
