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

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/batchRecord")
@Validated
public class BatchRecordController {

    @Autowired
    BatchRecordService batchRecordService;

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    ManualRecordService manualRecordService;

    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public BatchRecord queryBatchRecordById(@RequestParam Integer id){
        System.out.println(id);
        return batchRecordService.queryById(id);
    }

    @RequestMapping(value = "/queryByCompany", method = RequestMethod.GET)
    public List<BatchRecord> list(@RequestParam(defaultValue = "") String company,
                                  @RequestParam(defaultValue = "0") Integer safeDelete){
        System.out.println(company);
        if (company.equals(""))
            return batchRecordService.findAll(safeDelete);
        else
            return batchRecordService.queryByCompanyAndSafeDelete(company,safeDelete);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestParam Integer operatorId,
                      @RequestParam Integer ip,
                      @RequestBody BatchAndRecharge batchAndRecharge){
        BatchRecord batchRecord = batchRecordService.addBatchRecord(batchAndRecharge.getBatchRecord(),operatorId);
        for (RechargeRecord rechargeRecord:
             batchAndRecharge.getRechargeRecords()) {
            rechargeRecord.setBatchRecordId(batchRecord.getId());
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




}
