package newenergy.admin.controller;

import newenergy.db.domain.BatchRecord;
import newenergy.db.service.BatchRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batchRecord")
@Validated
public class BatchRecordController {

    @Autowired
    BatchRecordService batchRecordService;

    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public BatchRecord queryBatchRecordById(@RequestParam Integer id){
        System.out.println(id);
        return batchRecordService.queryById(id);
    }

    @RequestMapping(value = "/findByCompany", method = RequestMethod.GET)
    public List<BatchRecord> list(@RequestParam(defaultValue = "") String company,
                                  @RequestParam(defaultValue = "0") Integer safeDelete){

        return batchRecordService.queryByCompanyAndSafeDelete(company,safeDelete);
    }

}
