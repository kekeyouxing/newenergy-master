package newenergy.admin.controller;

import newenergy.db.domain.RechargeRecord;
import newenergy.db.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;
}
