package newenergy.admin.background.communicate.executor;

import newenergy.admin.background.service.FaultService;
import newenergy.admin.background.service.WaterService;
import newenergy.db.util.StringUtilCorey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HUST Corey on 2019-05-07.
 */
@Component
public class MsgSolve {
    @Autowired
    private WaterService waterService;
    @Autowired
    private FaultService faultService;

    private Logger logger = LoggerFactory.getLogger(MsgSolve.class);

    public  SolveResult solve(ParsingResult result){

        SolveResult solveResult = new SolveResult();
        if(result==null || !result.legaled()) return null;
        if(StringUtilCorey.emptyCheck(result.deviceNum())) return null;
        /**
         * 更新剩余水量
         */
        if(result.remainWater() != null){
            waterService.updateRemainWater(result.deviceNum(),result.remainWater());
        }
        /**
         * 存储需水量
         */
        waterService.updateRequireWater(result.deviceNum(),result.started());

        /**
         * 监控故障
         */
        if(result.fault())
            faultService.addFault(result.deviceNum(),result.faultDtl());

        /**
         * 返回新增用水量 和 退款水量
         */
        BigDecimal extraWater = waterService.getExtraWater(result.deviceNum());
        BigDecimal refundWater = waterService.getRefundWater(result.deviceNum());
        logger.info("机器编码："+result.deviceNum()+"；充值水量："+extraWater);
        logger.info("机器编码："+result.deviceNum()+"；退款水量："+refundWater);

        List<Integer> orders = waterService.getAllOrderIdByDeviceNum(result.deviceNum());
        //剩余水量 小于等于 退款水量时拒绝退款
        if(result.remainWater().compareTo(refundWater) <= 0){
            logger.info("机器编码："+result.deviceNum()+" 拒绝退款");
            for(Integer id : orders){
                waterService.labelFailed(id);
            }
            solveResult.setExtraWater(extraWater);
        }else{
            logger.info("机器编码："+result.deviceNum()+" 允许退款");
            for(Integer id : orders){
                waterService.labelSuccess(id);
            }
            solveResult.setExtraWater(extraWater.add(refundWater.negate()));


        }
        return solveResult;
    }


}
