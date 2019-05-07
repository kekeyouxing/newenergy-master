package newenergy.admin.background.communicate.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import static newenergy.admin.background.communicate.utils.NumberUtil.transformNum36to10;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class MsgParsing {
    private static Logger log = LoggerFactory.getLogger(MsgParsing.class);

    public static ParsingResult parse(String msg){
        ParsingResult result = new ParsingResult();
        msg = msg.trim();
        boolean legaled = true;
        if(msg.length() != 32) legaled = false;
        if(!msg.startsWith("HGDR") || !msg.endsWith("FINL")) legaled = false;
        result.setLegaled(legaled);
        if(!legaled) {
            log.error("invalid msg format");
            return result;
        }
        msg = msg.substring(4,28);
        result.setDeviceNum(msg.substring(0,4));
        msg = msg.substring(4);
        result.setRemainWater(transformNum36to10(msg.substring(0,8)));
        msg = msg.substring(8);
        result.setRechargeWater(transformNum36to10(msg.substring(0,8)));
        msg = msg.substring(8);
        int state = 0;
        try {
            state = Integer.valueOf(msg,16);
        }catch (Exception e){
            e.printStackTrace();
            log.error("invalid number format in <state>");
        }
        result.setState(state);
        return result;
    }


}
