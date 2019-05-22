package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.admin.excel.ExcelAfterSale;
import newenergy.core.util.TimeUtil;
import newenergy.db.constant.FaultRecordConstant;
import newenergy.db.domain.CorrAddress;
import newenergy.db.domain.FaultRecord;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.Resident;
import newenergy.db.predicate.AdminPredicate;
import newenergy.db.predicate.FaultRecordPredicate;
import newenergy.db.service.FaultRecordService;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.util.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by HUST Corey on 2019-04-20.
 */
@RestController
@RequestMapping("admin/data")
public class StatServicerController {
    @Autowired
    private FaultRecordService faultRecordService;
    @Autowired
    private NewenergyAdminService newenergyAdminService;
    private static class ListServicerDTO{
        Integer id;
        Integer year;
        Integer month;
        Integer page;
        Integer limit;
        String username;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    /**
     * 公司用户id为6，查询2019年3月叫"张三"的维修人员的维修记录，查询第1页，每页有10条记录。
     * {
     *     id:"6",
     *     year:2019,
     *     month:3,
     *     page:1,
     *     limit:10,
     *     username:"张三"
     * }
     * @param dto
     * @return
     * {
     *     total:2,
     *     list:[
     *     {
     *          id:3,
     *          realName:张三,
     *          phone:"15012345678"
     *     },
     *     {
     *          id:4,
     *          realName:李四,
     *          phone:"18812345678"
     *     }
     *     ]
     *
     * }
     */
    @RequestMapping(value = "servicer/list",method = RequestMethod.POST)
    public Map<String,Object> listServicers(@RequestBody ListServicerDTO dto, @AdminLoginUser NewenergyAdmin user){
//        Integer id = dto.getId();
        Integer id = user.getId();
        Integer year = dto. getYear();
        Integer month = dto.getMonth();
        Integer page = dto.getPage();
        Integer limit = dto.getLimit();
        String username = dto.getUsername();

        Map<String,Object> ret = new HashMap<>();

        FaultRecordPredicate predicate = new FaultRecordPredicate();
        if(year != null && month != null && check(year,month))
            predicate.setFinishTime(LocalDateTime.of(year,month,1,0,0));
        predicate.setState(FaultRecordConstant.STATE_FINISH);
        Page<FaultRecord> allRes = faultRecordService.findByPredicate(predicate,null,null);
        Set<Integer> servicers = new HashSet<>();
        allRes.forEach(e->{
            Integer servicerid = e.getServicerId();
            if(servicerid != null)
                servicers.add(servicerid);
        });

        AdminPredicate adminPredicate = new AdminPredicate();
        adminPredicate.setRealName(username);
        adminPredicate.setIds(new ArrayList<>(servicers));
        Page<NewenergyAdmin> admins = newenergyAdminService
                .findByPredicateWithAlive(
                        adminPredicate,
                        PageRequest.of(page-1,limit),
                        null);

        ret.put("total",admins.getTotalElements());
        List<Map<String,Object>> list = new ArrayList<>();
        admins.forEach(admin->{
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("id",admin.getId());
            tmp.put("realName",admin.getRealName());
            tmp.put("phone",admin.getPhone());
            list.add(tmp);
        });
        SortUtil.sortByChinese(list,"realName");
        ret.put("list", list);

        return ret;
    }
    boolean check(Integer year, Integer month){
        return year>=1970 && month >= 1 && month <= 12;
    }

    private static class ServicerDtlDTO{
        Integer id;
        Integer servicerId;
        Integer year;
        Integer month;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getServicerId() {
            return servicerId;
        }

        public void setServicerId(Integer servicerId) {
            this.servicerId = servicerId;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }
    }
    @RequestMapping(value = "servicer/dtl",method = RequestMethod.POST)
    public Map<String,Object> servicerDtl(@RequestBody ServicerDtlDTO dto, @AdminLoginUser NewenergyAdmin user){
//        Integer id = dto.getId();
        Integer id = user.getId();
        Integer servicerId = dto.getServicerId();
        Integer year = dto.getYear();
        Integer month = dto.getMonth();

        Map<String,Object> ret = new HashMap<>();
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        //维修人
        predicate.setServicerId(servicerId);
        //时间
        if(year != null && month != null && check(year,month))
            predicate.setFinishTime(LocalDateTime.of(year,month,1,0,0));
        //处理完成的
        predicate.setState(FaultRecordConstant.STATE_FINISH);
        Page<FaultRecord> allRes = faultRecordService.findByPredicate(predicate,
                null,
                Sort.by(Sort.Direction.DESC,"finishTime"));
        List<Map<String,Object>> list = new ArrayList<>();
        allRes.forEach(record -> {
            Map<String,Object> tmp = new HashMap<>();
            Resident resident = faultRecordService.getResident(record.getRegisterId());
            String roomNum = null, addressDtl = null;
            if(resident!=null){
                roomNum = resident.getRoomNum();
                CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
                addressDtl = corrAddress==null?null:corrAddress.getAddressDtl();
            }
            tmp.put("roomNum",roomNum);
            tmp.put("addressDtl",addressDtl);
            tmp.put("registerId",record.getRegisterId());
            tmp.put("phenomenon",record.getPhenomenon());
            tmp.put("faultTime", TimeUtil.getSeconds(record.getFaultTime()));
            tmp.put("responseTime",TimeUtil.getSeconds(record.getResponseTime()));
            tmp.put("finishTime",TimeUtil.getSeconds(record.getFinishTime()));
            tmp.put("solution",record.getSolution());
            tmp.put("state",record.getResult());
            list.add(tmp);
        });
        ret.put("list",list);
        return ret;
    }

    //
    @GetMapping("/afterSaleDownload")
    public void afterSaleDownload(HttpServletResponse response, @RequestParam String year,
                                  @RequestParam String month, @RequestParam String servicerName,
                                  @RequestParam String servicerId, @RequestParam String filename){

        int monthNum = Integer.parseInt(month);
        int servicerIdNum = Integer.parseInt(servicerId);
        int yearNum = Integer.parseInt(year);
        FaultRecordPredicate predicate = new FaultRecordPredicate();
        //维修人
        predicate.setServicerId(servicerIdNum);
        //时间
        if(year != null && month != null && check(yearNum,monthNum))
            predicate.setFinishTime(LocalDateTime.of(yearNum,monthNum,1,0,0));
        //处理完成的
        predicate.setState(FaultRecordConstant.STATE_FINISH);
        Page<FaultRecord> allRes = faultRecordService.findByPredicate(predicate, null, Sort.by(Sort.Direction.DESC,"finishTime"));
        List<String[]> list = new ArrayList<>();
        allRes.forEach(record -> {

            Resident resident = faultRecordService.getResident(record.getRegisterId());
            String roomNum = null, addressDtl = null;
            if(resident!=null){
                roomNum = resident.getRoomNum();
                CorrAddress corrAddress = faultRecordService.getCorrAddress(resident.getAddressNum());
                addressDtl = corrAddress==null?null:corrAddress.getAddressDtl();
            }
            LocalDateTime responseTime = record.getResponseTime();
            String responseTimeStr = "";
            if(responseTime!=null){
                responseTimeStr = responseTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            LocalDateTime finishTime = record.getResponseTime();
            String finishTimeStr = "";
            if(finishTime!=null){
                finishTimeStr = finishTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            LocalDateTime faultTime = record.getResponseTime();
            String faultTimeStr = "";
            if(faultTime!=null){
                faultTimeStr = faultTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            String[] strings = new String[]{record.getRegisterId(), addressDtl, roomNum, record.getPhenomenon(),
                    faultTimeStr, responseTimeStr, finishTimeStr,
                    record.getSolution(), record.getResult()+""};

            list.add(strings);
        });

        ExcelAfterSale excel = new ExcelAfterSale();
        int monthPlus = monthNum+1;
        excel.setTime(year+"-"+monthPlus+"-01");
        excel.setServicerId(servicerId);
        excel.setServicerName(servicerName);
        excel.createExcel(list);
        excel.exportExcel(filename,response);
    }
}
