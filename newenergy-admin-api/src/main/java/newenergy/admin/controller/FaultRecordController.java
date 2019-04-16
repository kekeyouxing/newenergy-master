package newenergy.admin.controller;

import newenergy.db.domain.*;
import newenergy.db.service.FaultRecordService;
import newenergy.db.template.FaultRecordPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        map.put("addressDtl",faultRecordService.getCorrAddressStr(corrAddress));
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
    @RequestMapping(value = "userdtl",method = RequestMethod.POST)
    public Map<String,Object> userDtl(Integer id, String registerId){
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> userinfo = new HashMap<>();
        Resident resident = faultRecordService.getResident(registerId);
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrType corrType = faultRecordService.getCorrType(resident.getTypeNum());
        userinfo.put("registerId",registerId);
        userinfo.put("username",resident.getUserName());
        userinfo.put("addressDtl",faultRecordService.getCorrAddressStr(corrAddress));
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
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        predicate.setRegisterId(registerId);
        Page<FaultRecord> recordPage = faultRecordService.findByPredicate(predicate,null,Sort.by(Sort.Direction.DESC,"faultTime"));
        recordPage.get().forEach(record->{
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("faultTime",record.getFaultTime());
            tmp.put("phenomeon",record.getPhenomenon());
            tmp.put("solution",record.getSolution());
            tmp.put("finishTime",record.getFinishTime());
            /**
             * TODO 根据售后人员id获得售后人员姓名
             */
            tmp.put("servicer",record.getServicerId());
            tmp.put("remark",record.getResult());
            records.add(tmp);
        });
        ret.put("records",records);
        return ret;
    }

    /**
     * test
     * @param id
     * @return
     */
//    @RequestMapping(value = "test")
//    public Map<String,Object> test(Integer id, String registerId){
//        Map<String,Object> ret = new HashMap<>();
//        List<FaultRecord> list = new ArrayList<>();
//        FaultRecordPredicate predicate = new FaultRecordPredicate();
//        predicate.setRegisterId(registerId);
//        Specification<FaultRecord> cond = faultRecordService.addConditioin(predicate,null);
//        Page<FaultRecord> records = faultRecordService.findBySpecificate(cond,null,null);
//        records.forEach(list::add);
//        ret.put("list",list);
//        return ret;
//    }

    /**
     * 所有用户的售后记录明细
     * @param id
     * @param page
     * @param limit
     * @param registerId
     * @param username
     * @return
     */
    @RequestMapping(value = "list/users",method = RequestMethod.POST)
    public Map<String,Object> listUsers(Integer id, Integer page, Integer limit, String registerId, String username){
        Map<String,Object> ret = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        List<String> plots = faultRecordService.getPlotLimit(id);
        if(plots == null) {
            ret.put("total",0);
            return ret;
        }
        Page<Resident> residents = faultRecordService.getResidentsByPlots(plots,page-1,limit,registerId,username);
        Long total = residents.getTotalElements();
        ret.put("total",total);
        residents.forEach(resident->{
            Map<String,Object> e = new HashMap<>();
            e.put("registerId",resident.getRegisterId());
            e.put("username",resident.getUserName());
            String addressNum = resident.getAddressNum();
            CorrAddress corrAddress = faultRecordService.getCorrAddress(addressNum);
            e.put("addressDtl",faultRecordService.getCorrAddressStr(corrAddress));
            e.put("roomNum",resident.getRoomNum());
            e.put("phone",resident.getPhone());
            list.add(e);
        });
        ret.put("records",list);
        return ret;
    }
    @RequestMapping(value = "records",method = RequestMethod.POST)
    public Map<String,Object> listRecords(Integer id, Integer type, String registerId, String username,Integer page, Integer limit){
        Map<String,Object> ret = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        List<String> plots = faultRecordService.getPlotLimit(id);
        if(plots == null) {
            ret.put("total",0);
            return ret;
        }
        predicate.setPlots(plots);
        predicate.setRegisterId(registerId);
        predicate.setUsername(username);
        predicate.setState(type);
        Page<FaultRecord> records = faultRecordService.findByPredicate(predicate,
                PageRequest.of(page-1,limit),
                Sort.by(Sort.Direction.DESC,"faultTime"));
        ret.put("total",records.getTotalElements());
        records.forEach(record->{
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("registerId",record.getRegisterId());
            Resident resident = faultRecordService.getResident(record.getRegisterId());
            CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
            tmp.put("username",resident.getUserName());
            tmp.put("addressDtl",faultRecordService.getCorrAddressStr(corrAddress));
            tmp.put("roomNum",resident.getRoomNum());
            tmp.put("phone",resident.getPhone());
            /**
             * TODO 根据id获取维修人员姓名 和 联系方式
             */
            tmp.put("servicerName",record.getServicerId());
            tmp.put("servicerPhone",record.getServicerId());
            Integer state = null;
            if(record.getState() == 2){
                if(record.getResult() == 0) state = 3;//成功
                if(record.getResult() == 1) state = 1;//超时未响应
                if(record.getResult() == 2) state = 4;//失败
            }else if(record.getState() == 0){
                state = 0; //待响应
            }else if(record.getState() == 1){
                state = 2; //维修中
            }
            tmp.put("state",state);
            list.add(tmp);
        });
        ret.put("records",list);
        return ret;
    }

}
