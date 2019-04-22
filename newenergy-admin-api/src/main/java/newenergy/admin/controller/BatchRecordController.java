package newenergy.admin.controller;

import com.google.gson.*;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.BatchAndRecharge;
import newenergy.db.domain.BatchRecord;
import newenergy.db.domain.ManualRecord;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.service.BatchRecordService;
import newenergy.db.service.ManualRecordService;
import newenergy.db.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

//    根据id查询批量充值记录
    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public BatchRecord queryBatchRecordById(@RequestParam Integer id){
        System.out.println(id);
        return batchRecordService.queryById(id);
    }

//    根据公司信息查询批量充值记录,若无公司名,则查询所有充值记录
    @RequestMapping(value = "/queryByCompany", method = RequestMethod.GET)
    public List<BatchRecord> list(@RequestParam(required = false) String company){
        System.out.println(company);
        if (company == null)
            return batchRecordService.findAll();
        else
            return batchRecordService.queryByCompanyAndSafeDelete(company);
    }

//    添加批量充值记录,及充值记录
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestParam Integer operatorId,
                      @RequestParam Integer ip,
                      @RequestBody BatchAndRecharge batchAndRecharge){
        batchAndRecharge.getBatchRecord().setRechargeTime(LocalDateTime.now());
        batchAndRecharge.getBatchRecord().setBatchAdmin(operatorId);
        BatchRecord batchRecord = batchRecordService.addBatchRecord(batchAndRecharge.getBatchRecord(),operatorId);
        for (RechargeRecord rechargeRecord:
             batchAndRecharge.getRechargeRecords()) {
            rechargeRecord.setBatchRecordId(batchRecord.getId());
            rechargeRecord.setOrderSn(rechargeRecordService.generateOrderSn());
            rechargeRecord.setRechargeTime(LocalDateTime.now());
            rechargeRecord.setState(0);
            rechargeRecord.setDelegate(1);
            rechargeRecordService.addRechargeRecord(rechargeRecord,operatorId);
        }
        addManualRecord(batchRecord.getId(),operatorId,ip,0);
        return ResponseUtil.ok();
    }

//    添加人工操作记
    private void addManualRecord(Integer recordId,Integer operatorId,Integer ip,Integer event){
        ManualRecord manualRecord = new ManualRecord();
        manualRecord.setRecordId(recordId);
        manualRecord.setOperateTime(LocalDateTime.now());
        manualRecord.setLaborIp(ip);
        manualRecord.setLaborId(operatorId);
        manualRecord.setEvent(event);
        manualRecordService.save(manualRecord);
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




}
