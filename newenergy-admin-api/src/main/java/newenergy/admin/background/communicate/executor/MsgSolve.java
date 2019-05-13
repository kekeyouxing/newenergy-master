package newenergy.admin.background.communicate.executor;

import newenergy.admin.background.service.FaultService;
import newenergy.admin.background.service.WaterService;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by HUST Corey on 2019-05-07.
 */
@Component
public class MsgSolve {
    @Autowired
    private WaterService waterService;
    @Autowired
    private FaultService faultService;


    public  SolveResult solve(ParsingResult result){
        /**
         * TODO  逻辑处理
         */
//        System.out.println(result.legaled());
//        System.out.println(result.coldMod());
//        System.out.println(result.started());
//        System.out.println(result.fault());
//        System.out.println(result.remainWater());
//
//        System.out.println(result.rechargeWater());
//        System.out.println(result.deviceNum());
//        System.out.println(result.faultDtl());

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
         * 返回新增用水量
         */
        solveResult.setExtraWater(waterService.getExtraWater(result.deviceNum()));
        return solveResult;
    }
}
