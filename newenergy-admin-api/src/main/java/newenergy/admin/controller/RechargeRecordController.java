package newenergy.admin.controller;

import newenergy.db.domain.BatchRelative;
import newenergy.db.domain.RechargeRecord;
import newenergy.db.service.BatchRelativeService;
import newenergy.db.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rechargeRecord")
@Validated
public class RechargeRecordController {

    @Autowired
    RechargeRecordService rechargeRecordService;

    @Autowired
    BatchRelativeService batchRelativeService;


    @RequestMapping(value = "/findSingle", method = RequestMethod.GET)
    public RechargeRecord findById(@RequestParam Integer id){
        System.out.println(id);
        return rechargeRecordService.findById(id);
    }

//根据订单状态和注册号查充值记录
    @RequestMapping(value = "/findByRechargeRecord", method = RequestMethod.GET)
    public List<RechargeRecord> findByRegisterId(@RequestParam(defaultValue = "") String registerId,
                                                 @RequestParam(defaultValue = "0") Integer safeDelete,
                                                 @RequestParam(defaultValue = "-1") Integer state){

        if (registerId.equals("")&&(state==-1)){
//            若注册id为空，state为-1，则查询所有订单
            return rechargeRecordService.findAllBySafeDelete(safeDelete);
        }else if (registerId.equals("")){
//            若注册id为空，查询指定状态订单
            return rechargeRecordService.findBySafeDeleteAndState(safeDelete,state);
        }else if (state==-1){
//            若state为-1，查询所有指定注册id的订单
            return rechargeRecordService.findByRegisterIdAndSafeDelete(registerId,safeDelete);
        }else {
//            若registerid不为空，state不为-1，则返回指定注册id和状态的订单
            return rechargeRecordService.findByRegisterIdAndSafeDeleteAndState(registerId,safeDelete,state);
        }
    }

//    根据批量充值订单和审核状态查询批量充值记录
    @RequestMapping(value = "/findByBatchRecord", method = RequestMethod.GET)
    public List<RechargeRecord> findByBatchRelative(@RequestParam(defaultValue = "-1") Integer batchRecordId,
                                                    @RequestParam(defaultValue = "-1") Integer state){
        List<BatchRelative> relatives;
        List<RechargeRecord> records = new ArrayList<>();
        if ((batchRecordId==-1)&&(state==-1)){
//            若注册batchRecordId为空，state为-1，则查询所有订单
            relatives = batchRelativeService.findAll();
        }else if (batchRecordId==-1){
//            若注册batchRecordId为空，查询指定状态订单
            relatives = batchRelativeService.findByState(state);
        }else if (state==-1){
//            若state为-1，查询所有指定batchRecordId的订单
            relatives = batchRelativeService.findByBatchRecordId(batchRecordId);
        }else {
//            若batchRecordId不为空，state不为-1，则返回指定注册id和状态的订单
            relatives = batchRelativeService.findByBatchRecordIdAndState(batchRecordId,state);
        }
        for (BatchRelative batchRelative:relatives
                ) {
            records.add(rechargeRecordService.findById(batchRelative.getRechargeRecordId()));
        }
        return records;
    }
}
