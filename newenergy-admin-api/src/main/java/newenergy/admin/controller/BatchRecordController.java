package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.admin.util.IpUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.BatchRecord;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.Resident;
import newenergy.db.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/batchRecord")
@Validated
public class BatchRecordController {

    @Autowired
    BatchRecordService batchRecordService;

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

    @Autowired
    ResidentService residentService;

    @Autowired
    CorrPlotService corrPlotService;

    @Autowired
    CorrAddressService corrAddressService;

    @Autowired
    NewenergyAdminService adminService;

//    2.1 根据小区获取小区对应的用户，接收数据，小区编号，按照栋数排序
    @RequestMapping(value = "/findUserInfoByPlotNum", method = RequestMethod.GET)
    public Object findUserInfo(@RequestParam String plotNum){
        List<Resident> residents = residentService.findByPlotNum(plotNum);
        List<UserInfo> userInfos = new ArrayList<>();
        for (Resident resident:
             residents) {
                    userInfos.add(new UserInfo(resident.getRegisterId(),
                    resident.getUserName(),
                    corrAddressService.findAddressDtlByAddressNum(resident.getAddressNum()),
                    resident.getRoomNum()));
        }

        HashMap<String,List<UserInfo>> result = new HashMap<>();
        result.put("list",userInfos);
        return result;
    }

//        2.2 添加批量充值记录,及充值记录
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestBody BatchAndRecharge batchAndRecharge,
                      HttpServletRequest request,
                      @AdminLoginUser NewenergyAdmin user){
        batchAndRecharge.getBatchRecord().setRechargeTime(LocalDateTime.now());
        batchAndRecharge.getBatchRecord().setBatchAdmin(user.getId());
        batchAndRecharge.getBatchRecord().setState(0);
        BatchRecord batchRecord = batchRecordService.addBatchRecord(batchAndRecharge.getBatchRecord(),user.getId());
        for (RechargeRecord rechargeRecord:
                batchAndRecharge.getRechargeRecords()) {
//            根据注册id查询批量小区编号，然后根据小区编号查询充值系数
            BigDecimal plotFactor = corrPlotService.findPlotFacByPlotNum(residentService.findPlotNumByRegisterid(rechargeRecord.getRegisterId(),0));
            rechargeRecord.setRechargeVolume(new BigDecimal(rechargeRecord.getAmount()).divide(plotFactor,2, RoundingMode.UP));
            rechargeRecord.setBatchRecordId(batchRecord.getId());
            rechargeRecord.setOrderSn(rechargeRecordService.generateOrderSn());
            rechargeRecord.setRechargeTime(LocalDateTime.now());
            rechargeRecord.setState(0);
            rechargeRecord.setReviewState(0);
            rechargeRecord.setDelegate(1);
            rechargeRecord.setPlotNum(residentService
                    .fingByRegisterId(rechargeRecord.getRegisterId())
                    .getPlotNum());
            rechargeRecord.setUserName(user.getRealName());
            rechargeRecord.setUserPhone(residentService
                    .fingByRegisterId(rechargeRecord.getRegisterId())
                    .getPhone());
            rechargeRecordService.addRechargeRecord(rechargeRecord,batchAndRecharge.getBatchRecord().getBatchAdmin());
        }
        manualRecordService.add(user.getId(),IpUtil.getIpAddr(request),0,batchRecord.getId());
        Map<String,Integer> state = new HashMap<>();
//        0代表正常、其他代表异常
        state.put("state",0);
        return state;
    }

    //    添加批量充值记录
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    public String add(@RequestBody MultipartFile file){
        if (!file.isEmpty()) {
            try {
                String name = UUID.randomUUID().toString();
//                判断路径是否存在，不存在则新建一个目录
//                部署需重新配置
                File file1 = new File("f:\\images");
                if (!file1.exists()){
                    file1.mkdir();
                }
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File("f:\\images\\"+name+".jpg")));//保存图片到目录下，部署续重新配置
                out.write(file.getBytes());
                out.flush();
                out.close();
//                部署续重新配置，将localhost改为ip
                return "218.197.229.6:8080/admin/image/"+name+".jpg";
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
        } else {
            return "上传失败，因为文件是空的.";
        }

    }


    private static class BatchAndRecharge {

        private BatchRecord batchRecord;

        private List<RechargeRecord> rechargeRecords;

        public BatchRecord getBatchRecord() {
            return batchRecord;
        }

        public void setBatchRecord(BatchRecord batchRecord) {
            this.batchRecord = batchRecord;
        }

        public List<RechargeRecord> getRechargeRecords() {
            return rechargeRecords;
        }

        public void setRechargeRecords(List<RechargeRecord> rechargeRecords) {
            this.rechargeRecords = rechargeRecords;
        }
    }

    private static class UserInfo{
        String registerId;
        String username;
        String addressDtl;
        String roomNum;

        public UserInfo(String registerId,String username,String addressDtl,String roomNum){
            this.registerId = registerId;
            this.username = username;
            this.roomNum = roomNum;
            this.addressDtl = addressDtl;
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
