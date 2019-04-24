package newenergy.admin.controller;

import newenergy.admin.util.IpUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.BatchRecord;
import newenergy.db.domain.RechargeRecord;
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
import java.util.List;
import java.util.UUID;

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

//    根据id查询批量充值记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public BatchRecord queryBatchRecordById(@RequestParam Integer id){
        System.out.println(id);
        return batchRecordService.queryById(id);
    }

//    根据小区信息查询批量充值记录,若无公司名,则查询所有充值记录
    @RequestMapping(value = "findByCompany", method = RequestMethod.POST)
    public String list(HttpServletRequest request){
//        System.out.println(plotDtl);
        return IpUtil.getIpAddr(request);
//        if (plotDtl == null)
//            return batchRecordService.findAll();
//        else{
//            return batchRecordService.queryByPlotNumAndSafeDelete(corrPlotService.findPlotNum(plotDtl));
//        }

    }

//    添加批量充值记录,及充值记录
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestParam Integer operatorId,
                      @RequestBody BatchAndRecharge batchAndRecharge,
                      HttpServletRequest request){
        batchAndRecharge.getBatchRecord().setRechargeTime(LocalDateTime.now());
        batchAndRecharge.getBatchRecord().setBatchAdmin(operatorId);
        BatchRecord batchRecord = batchRecordService.addBatchRecord(batchAndRecharge.getBatchRecord(),operatorId);
        for (RechargeRecord rechargeRecord:
             batchAndRecharge.getRechargeRecords()) {
//            根据注册id查询批量小区编号，然后根据小区编号查询充值系数
            BigDecimal plotFactor = corrPlotService.findPlotFacByPlotNum(residentService.findPlotNumByRegisterid(rechargeRecord.getRegisterId(),0),0);
            rechargeRecord.setRechargeVolume(new BigDecimal(rechargeRecord.getAmount()).divide(plotFactor,2, RoundingMode.UP));
            rechargeRecord.setBatchRecordId(batchRecord.getId());
            rechargeRecord.setOrderSn(rechargeRecordService.generateOrderSn());
            rechargeRecord.setRechargeTime(LocalDateTime.now());
            rechargeRecord.setState(0);
            rechargeRecord.setReviewState(0);
            rechargeRecord.setDelegate(1);
            rechargeRecordService.addRechargeRecord(rechargeRecord,operatorId);
        }
        manualRecordService.add(operatorId,123,0,batchRecord.getId());
        return IpUtil.getIpAddr(request);
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
}
