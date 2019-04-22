package newenergy.admin.controller;

import newenergy.db.constant.AdminConstant;
import newenergy.db.constant.DeviceRequireConstant;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.constant.ResultConstant;
import newenergy.db.domain.*;
import newenergy.db.predicate.CorrPlotAdminPredicate;
import newenergy.db.predicate.DeviceRequirePredicate;
import newenergy.db.repository.NewenergyAdminRepository;
import newenergy.db.repository.NewenergyRoleRepository;
import newenergy.db.service.*;
import newenergy.db.predicate.FaultRecordPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by HUST Corey on 2019-04-13.
 */
@RestController
@RequestMapping("admin/fault")
public class FaultRecordController {
    @Autowired
    private FaultRecordService faultRecordService;

    @Autowired
    private CorrPlotAdminService corrPlotAdminService;

    @Autowired
    private DeviceRequireService deviceRequireService;

    private static class UserinfoDTO{
        Integer id;
        String registerId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }
    }
    /**
     *  信息确认
     *  id
     *  registerId
     * @return
     */
    @RequestMapping(value = "userinfo",method = RequestMethod.POST)
    public Map<String,Object> userinfo(@RequestBody UserinfoDTO dto){
        Integer id = dto.getId();
        String registerId = dto.getRegisterId();
        Map<String,Object> ret = new HashMap<>();
        Map<String,Object> map = new HashMap<>();
        Resident resident = faultRecordService.getResident(registerId);
        if(resident==null) return map;
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrPlotAdmin corrPlotAdmin = faultRecordService.getCorrPlotAdmin(resident.getPlotNum());
        map.put("registerId",registerId);
        map.put("username",resident.getUserName());
        String addressDtl = "";
        if(corrAddress!=null)
            addressDtl = corrAddress.getAddressDtl();
        map.put("addressDtl",addressDtl);
        map.put("roomNum",resident.getRoomNum());
        map.put("phone",resident.getPhone());
        //暂时使用用户id代替用户姓名
        Integer serviceId = corrPlotAdmin.getServicerId();
        NewenergyAdmin admin = faultRecordService.getNewenergyAdmin(serviceId);
        String servicerName = null;
        if(admin != null) servicerName = admin.getRealName();
        map.put("servicerName",servicerName);
        ret.put("userinfo",map);

        Map<String,Object> info = new HashMap<>();
        info.put("area",resident.getArea());
        info.put("buyTime",resident.getBuyTime());
        CorrType corrType = faultRecordService.getCorrType(resident.getTypeNum());
        info.put("typeDtl",corrType.getTypeDtl());
        info.put("ratedFlow",resident.getRatedFlow());
        info.put("deviceNum",resident.getDeviceNum());
        info.put("deviceSeq",resident.getDeviceSeq());
        info.put("installTime",resident.getInstallTime());
        info.put("receiveTime",resident.getReceiveTime());
        String pumpNum = resident.getPumpNum();
        CorrPump corrPump = pumpNum==null?null:faultRecordService.getCorrPump(pumpNum);
        String pumpDtl = corrPump==null?"":corrPump.getPumpDtl();
        info.put("pumpDtl",pumpDtl);

        ret.put("info",info);
        return ret;
    }

    private static class AddDTO{
        Integer id;
        String registerId;
        String phenomeon;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getPhenomeon() {
            return phenomeon;
        }

        public void setPhenomeon(String phenomeon) {
            this.phenomeon = phenomeon;
        }
    }
    /**
     * 确认新增故障记录
     *  id
     *  registerId
     *  phenomeon
     * @return
     */
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public Integer addRecord(@RequestBody AddDTO dto){
        Integer id = dto.getId();
        String registerId = dto.getRegisterId();
        String phenomeon = dto.getPhenomeon();

        FaultRecord faultRecord = new FaultRecord();
        faultRecord.setRegisterId(registerId);
        faultRecord.setMonitorId(id);
        Resident resident = faultRecordService.getResident(registerId);
        if(resident == null) return ResultConstant.ERR;
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


    private static class UserDtlDTO{
        Integer id;
        String registerId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }
    }
    /**
     * 查看用户详情（售后记录明细表）：
     *  id
     *  registerId
     * @return
     */
    @RequestMapping(value = "userdtl",method = RequestMethod.POST)
    public Map<String,Object> userDtl(@RequestBody UserDtlDTO dto){
        Integer id = dto.getId();
        String registerId = dto.getRegisterId();
        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> userinfo = new HashMap<>();
        Resident resident = faultRecordService.getResident(registerId);
        if(resident==null) return ret;
        CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
        CorrType corrType = faultRecordService.getCorrType(resident.getTypeNum());
        userinfo.put("registerId",registerId);
        userinfo.put("username",resident.getUserName());
        String addressDtl = "";
        if(corrAddress != null)
            addressDtl = corrAddress.getAddressDtl();
        userinfo.put("addressDtl",addressDtl);
        userinfo.put("roomNum",resident.getRoomNum());
        userinfo.put("phone",resident.getPhone());

        userinfo.put("typeDtl",corrType.getTypeDtl());
        userinfo.put("receiveTime",resident.getReceiveTime());
        LocalDate guaranteeTime = resident.getReceiveTime().plusYears(faultRecordService.warranty);
        userinfo.put("guaranteeTime",guaranteeTime);
        Integer isWarranty = LocalDate.now().isBefore(guaranteeTime)?1:0;
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
            Integer serviceId = record.getServicerId();
            NewenergyAdmin admin = faultRecordService.getNewenergyAdmin(serviceId);
            String servicerName = null;
            if(admin != null) servicerName = admin.getRealName();
            tmp.put("servicer",servicerName);
            tmp.put("remark",record.getResult());
            records.add(tmp);
        });
        ret.put("records",records);

        return ret;
    }

    /**
     * test
     * id
     * @return
     */
//    @Autowired
//    NewenergyRoleRepository newenergyRoleRepository;
//    @Autowired
//    NewenergyAdminRepository newenergyAdminRepository;
//    @RequestMapping(value = "test",method = RequestMethod.POST)
//    public Map<String,Object> test(Integer id){
//        Map<String,Object> ret = new HashMap<>();
//        NewenergyRole role = new NewenergyRole();
//        role.setEnable(true);
//        role.setDeleted(false);
//        role.setAddTime(LocalDateTime.now());
//        role.setUpdateTime(LocalDateTime.now());
//        role.setName("维修人员");
//        role.setDescription("接收故障提醒，维修设备");
//        NewenergyRole addedRole = newenergyRoleRepository.save(role);
//        NewenergyAdmin admin = new NewenergyAdmin();
//        admin.setDeleted(false);
//        admin.setAddTime(LocalDateTime.now());
//        admin.setUsername("servicer"+LocalDateTime.now().toString());
//        admin.setRealName("运营人员姓名"+LocalDateTime.now().toString());
//        admin.setPhone("13312340000");
//        Integer[] roleids = new Integer[]{
//                AdminConstant.ROLE_MONITOR
//        };
//        admin.setRoleIds(roleids);
//        newenergyAdminRepository.save(admin);
//        return ret;
//    }


    private static class ListUsersDTO{
        Integer id;
        Integer page;
        Integer limit;
        String registerId;
        String username;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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
    }
    /**
     * 所有用户的售后记录明细
     *  id
     *  page
     *  limit
     *  registerId
     *  username
     * @return
     */
    @RequestMapping(value = "list/users",method = RequestMethod.POST)
    public Map<String,Object> listUsers(@RequestBody ListUsersDTO dto){
        Integer id = dto.getId();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();
        String registerId = dto.getRegisterId();
        String username = dto.getUsername();

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
            String addressDtl = corrAddress==null?"":corrAddress.getAddressDtl();
            e.put("addressDtl",addressDtl);
            e.put("roomNum",resident.getRoomNum());
            e.put("phone",resident.getPhone());
            list.add(e);
        });
        ret.put("records",list);
        return ret;
    }
    private static class RecordsDTO{
        Integer id;
        Integer type;
        String registerId;
        String username;
        Integer page;
        Integer limit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

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
    @RequestMapping(value = "records",method = RequestMethod.POST)
    public Map<String,Object> listRecords(@RequestBody RecordsDTO dto){
        Integer id = dto.getId();
        Integer type = dto.getType();
        String registerId = dto.getRegisterId();
        String username = dto.getUsername();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();

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
            String addressDtl = "";
            if(corrAddress != null)
                addressDtl = corrAddress.getAddressDtl();
            tmp.put("addressDtl",addressDtl);
            tmp.put("roomNum",resident.getRoomNum());
            tmp.put("phone",resident.getPhone());
            NewenergyAdmin admin = faultRecordService.getNewenergyAdmin(record.getServicerId());
            String name = null, phone = null;
            if(admin != null){
                name = admin.getRealName();
                phone = admin.getPhone();
            }
            tmp.put("servicerName",name);
            tmp.put("servicerPhone",phone);
            Integer state = null;
            if( FaultRecordConstant.STATE_FINISH.equals(record.getState()) ){
                if( FaultRecordConstant.RESULT_SUCCESS.equals(record.getResult()) ) state = 3;//成功
                if( FaultRecordConstant.RESULT_TIMEOUT.equals(record.getResult()) ) state = 1;//超时未响应
                if( FaultRecordConstant.RESULT_FAILED.equals(record.getResult()) ) state = 4;//失败
            }else if( FaultRecordConstant.STATE_WAIT.equals(record.getState()) ){
                state = 0; //待响应
            }else if( FaultRecordConstant.STATE_DURING.equals(record.getState()) ){
                state = 2; //维修中
            }
            tmp.put("state",state);
            tmp.put("faultTime",record.getFaultTime());
            list.add(tmp);
        });
        ret.put("records",list);
        return ret;
    }
    private static class GroupSearchDTO{
        Integer id;
        String plotDtl;
        String monitorName;
        String servicerName;
        Integer page;
        Integer limit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotDtl() {
            return plotDtl;
        }

        public void setPlotDtl(String plotDtl) {
            this.plotDtl = plotDtl;
        }

        public String getMonitorName() {
            return monitorName;
        }

        public void setMonitorName(String monitorName) {
            this.monitorName = monitorName;
        }

        public String getServicerName() {
            return servicerName;
        }

        public void setServicerName(String servicerName) {
            this.servicerName = servicerName;
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
    @RequestMapping(value = "group/search",method = RequestMethod.POST)
    public Map<String,Object> groupSearch(@RequestBody GroupSearchDTO dto){
        Integer id = dto.getId();
        String plotDtl = dto.getPlotDtl();
        String monitorName = dto.getMonitorName();
        String servicerName = dto.getServicerName();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();

        Map<String,Object> ret = new HashMap<>();
        CorrPlotAdminPredicate predicate = new CorrPlotAdminPredicate();
        predicate.setPlotName(plotDtl);
        predicate.setMonitorName(monitorName);
        predicate.setServicerName(servicerName);
        Page<CorrPlotAdmin> res = corrPlotAdminService.findByPredicateWithAive(predicate,PageRequest.of(page-1,limit),Sort.by(Sort.Direction.ASC,"plotNum"));
        ret.put("total",res.getTotalElements());
        List<Map<String,Object>> list = new ArrayList<>();
        res.forEach(e->{
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("plotNum",e.getPlotNum());
            tmp.put("plotDtl",corrPlotAdminService.getPlotdtl(e.getPlotNum()));
            tmp.put("monitorName",corrPlotAdminService.getAdminName(e.getMonitorId()));
            tmp.put("servicerName",corrPlotAdminService.getAdminName(e.getServicerId()));
            list.add(tmp);
        });
        ret.put("list",list);
        return ret;
    }
    private static class GroupUserinfoDTO{
        Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
    @RequestMapping(value = "group/userinfo",method = RequestMethod.POST)
    public Map<String,Object> getAdminInfo(@RequestBody GroupUserinfoDTO dto){
        Integer id = dto.getId();
        Map<String,Object> ret = new HashMap<>();
        ret.put("servicers",corrPlotAdminService.getServicers());
        ret.put("monitors",corrPlotAdminService.getMonitors());
        return ret;
    }
    private static class GroupUpdateDTO{
        Integer id;
        String plotNum;
        Integer updateMonitor;
        Integer updateServicer;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotNum() {
            return plotNum;
        }

        public void setPlotNum(String plotNum) {
            this.plotNum = plotNum;
        }

        public Integer getUpdateMonitor() {
            return updateMonitor;
        }

        public void setUpdateMonitor(Integer updateMonitor) {
            this.updateMonitor = updateMonitor;
        }

        public Integer getUpdateServicer() {
            return updateServicer;
        }

        public void setUpdateServicer(Integer updateServicer) {
            this.updateServicer = updateServicer;
        }
    }
    @RequestMapping(value = "group/update",method = RequestMethod.POST)
    public CorrPlotAdmin updateAdminInfo(@RequestBody GroupUpdateDTO dto){
        Integer id = dto.getId();
        String plotNum = dto.getPlotNum();
        Integer updateMonitor = dto.getUpdateMonitor();
        Integer updateServicer = dto.getUpdateServicer();

        CorrPlotAdminPredicate predicate = new CorrPlotAdminPredicate();
        predicate.setPlotNum(plotNum);
        Page<CorrPlotAdmin> results = corrPlotAdminService.findByPredicateWithAive(predicate,null,null);
        if(results.getTotalElements() != 1) return null;
        CorrPlotAdmin corrPlotAdmin = results.get().findFirst().orElse(null);
        if(corrPlotAdmin == null) return null;
        corrPlotAdmin.setMonitorId(updateMonitor);
        corrPlotAdmin.setServicerId(updateServicer);
        return corrPlotAdminService.updateARecord(corrPlotAdmin,id);
    }
    private static class RequireSearchDTO{
        Integer id;String plotDtl;Integer page;Integer limit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getPlotDtl() {
            return plotDtl;
        }

        public void setPlotDtl(String plotDtl) {
            this.plotDtl = plotDtl;
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
    @RequestMapping(value = "require/search",method = RequestMethod.POST)
    public Map<String,Object> requireSearch(@RequestBody RequireSearchDTO requireSearchDTO){
        Map<String,Object> ret = new HashMap<>();
        DeviceRequirePredicate predicate = new DeviceRequirePredicate();
        predicate.setPlotDtl(requireSearchDTO.getPlotDtl());
        Page<DeviceRequire> plots = deviceRequireService.findByPredicateWithAive(predicate,
                PageRequest.of(requireSearchDTO.getPage()-1, requireSearchDTO.getLimit()),
                Sort.by(Sort.Direction.ASC,"plotNum"));
        DeviceRequire setting = deviceRequireService.getSetting();

        LocalDateTime updateTime = null;
        Integer updateLoop = null;
        if(setting != null){
            updateTime = setting.getUpdateTime();
            updateLoop = setting.getUpdateLoop();
        }
        ret.put("updateTime",updateTime);
        ret.put("updateLoop",updateLoop);
        List<Map<String,Object>> plotlist = new ArrayList<>();
        Stream<DeviceRequire> realPlots = plots.get().filter(plot->!plot.getPlotNum().equals(DeviceRequireConstant.SETTINGS));
        realPlots.forEach(plot->{
                Map<String,Object> tmp = new HashMap<>();
                tmp.put("plotDtl",deviceRequireService.getPlotDtl(plot.getPlotNum()));
                tmp.put("requireVolume",plot.getRequireVolume());
                plotlist.add(tmp);
            });
        ret.put("total",plotlist.size());
        ret.put("list",plotlist);
        return ret;
    }
    private static class RequireUpdateDTO{
        Integer id;
        Integer updateLoop;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getUpdateLoop() {
            return updateLoop;
        }

        public void setUpdateLoop(Integer updateLoop) {
            this.updateLoop = updateLoop;
        }
    }
    @RequestMapping(value = "require/update",method = RequestMethod.POST)
    public Integer requireUpdate(@RequestBody RequireUpdateDTO dto){
        Integer id = dto.getId();
        Integer updateLoop = dto.getUpdateLoop();

        DeviceRequire setting = deviceRequireService.getSetting();
        if(setting == null) return 1;
        setting.setUpdateLoop(updateLoop);
        return deviceRequireService.setSetting(setting,id)==null?1:0;
    }
    @RequestMapping(value = "require/start")
    public void startCron(){
        deviceRequireService.updateCron();
    }


}
