package newenergy.admin.background.communicate.executor;

import newenergy.admin.background.communicate.utils.NumberUtil;

import java.math.BigDecimal;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class SolveResult {
    private BigDecimal extraWater;

    public BigDecimal getExtraWater() {
        return extraWater;
    }

    public void setExtraWater(BigDecimal extraWater) {
        this.extraWater = extraWater;
    }

    public String replyMsg(){
        String extraWaterStr = NumberUtil.transformNum10to36(extraWater);
        if(extraWaterStr == null) extraWaterStr="";
        return "HGDR" + extraWaterStr + "FINL";
    }
}
