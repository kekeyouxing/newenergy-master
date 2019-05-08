package newenergy.admin.background.communicate.executor;

import java.math.BigDecimal;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class MsgSolve {
    public static SolveResult solve(ParsingResult result){
        /**
         * TODO  逻辑处理
         */
        System.out.println(result.legaled());
        System.out.println(result.coldMod());
        System.out.println(result.started());
        System.out.println(result.fault());
        System.out.println(result.remainWater());
        System.out.println(result.rechargeWater());
        System.out.println(result.deviceNum());
        System.out.println(result.faultDtl());
        SolveResult solveResult = new SolveResult();
        solveResult.setExtraWater(new BigDecimal(312.56));
        return solveResult;
    }
}
