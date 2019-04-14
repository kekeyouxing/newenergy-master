package newenergy.admin.controller;

import newenergy.db.domain.*;
import newenergy.db.service.FaultRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by HUST Corey on 2019-04-13.
 */
@RestController
@RequestMapping("admin/fault")
public class FaultRecordController {
    @Autowired
    private FaultRecordService faultRecordService;

    /**
     *  信息确认
     * @param id
     * @param registerId
     * @return
     */
    @RequestMapping(value = "userinfo",method = RequestMethod.POST)
    public Map<String,Object> userinfo(Integer id, String registerId){
        Resident resident = faultRecordService.getResident(registerId);
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrPlotAdmin corrPlotAdmin = faultRecordService.getCorrPlotAdmin(resident.getPlotNum());
        /**
         * TODO  根据售后人员的userid获得售后人员姓名
         */
        Map<String,Object> map = new HashMap<>();
        map.put("registerId",registerId);
        map.put("username",resident.getUserName());
        map.put("addressDtl",String.format("%s小区%d栋%d单元",
                corrAddress.getAddressPlot(),
                corrAddress.getAddressBlock(),
                corrAddress.getAddressUnit()));
        map.put("roomNum",resident.getRoomNum());
        map.put("phone",resident.getPhone());
        //暂时使用用户id代替用户姓名
        map.put("servicerName",corrPlotAdmin.getServicerId());
        return map;
    }

    /**
     * 确认新增故障记录
     * @param id
     * @param registerId
     * @param phenomeon
     * @return
     */
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public Integer addRecord(Integer id, String registerId, String phenomeon){
        FaultRecord faultRecord = new FaultRecord();
        faultRecord.setRegisterId(registerId);
        faultRecord.setMonitorId(id);
        Resident resident = faultRecordService.getResident(registerId);
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrPlotAdmin corrPlotAdmin = faultRecordService.getCorrPlotAdmin(resident.getPlotNum());
        faultRecord.setServicerId(corrPlotAdmin.getServicerId());
        faultRecord.setFaultTime(LocalDateTime.now());
        faultRecord.setState(0);
        faultRecord.setPhenomenon(phenomeon);
        FaultRecord result = faultRecordService.addRecord(faultRecord);
        if(Objects.isNull(result))
            return 1;
        /**
         * TODO 发送消息到售后人员的微信公众号
         */
        return 0;
    }


    /**
     * 查看用户详情（售后记录明细表）：
     * @param id
     * @param registerId
     * @return
     */
    @RequestMapping(value = "user/dtl",method = RequestMethod.POST)
    public Map<String,Object> userDtl(Integer id, String registerId){
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> userinfo = new HashMap<>();
        Resident resident = faultRecordService.getResident(registerId);
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrType corrType = faultRecordService.getCorrType(resident.getTypeNum());
        userinfo.put("registerId",registerId);
        userinfo.put("username",resident.getUserName());
        userinfo.put("addressDtl",String.format("%s小区%d栋%d单元",
                corrAddress.getAddressPlot(),
                corrAddress.getAddressBlock(),
                corrAddress.getAddressUnit()));
        userinfo.put("roomNum",resident.getRoomNum());
        userinfo.put("phone",resident.getPhone());

        userinfo.put("typeDtl",corrType.getTypeDtl());
        userinfo.put("receiveTime",resident.getReceiveTime());
        LocalDateTime guaranteeTime = resident.getReceiveTime().plusYears(faultRecordService.warranty);
        userinfo.put("guaranteeTime",guaranteeTime);
        Integer isWarranty = LocalDateTime.now().isBefore(guaranteeTime)?1:0;
        //isWarranty：1保内，0保外
        userinfo.put("guaranteeState",isWarranty);
        userinfo.put("plotDtl",corrAddress.getAddressPlot());
        ret.put("userinfo",userinfo);

        List<Map<String,Object>> records = new ArrayList<>();
        /**
         * TODO 获得该登记号的所有售后记录
         */

        return ret;
    }



    /**
     * 所有人员的售后记录明细
     * @param id
     * @param page
     * @param limit
     * @param registerId
     * @param username
     * @return
     */
    @RequestMapping(value = "list/users",method = RequestMethod.POST)
    public Map<String,Object> listUsers(Integer id, Integer page, Integer limit, String registerId, String username){
        return null;
    }

}
