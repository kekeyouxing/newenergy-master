package newenergy.admin.background.communicate.utils;

import newenergy.admin.background.communicate.constant.NumberSymbol;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class NumberUtil {
    /**
     *
     * @param num 8位36进制数，前7位为整数部分，后1位为小数部分
     * @return
     */
    public static BigDecimal transformNum36to10(String num){
        if(num == null || num.length() != 8) return null;
        int sign = num.charAt(0)=='0'?1:-1;
        long prefix = transform36to10(num.substring(1,7));
        long suffix = transform36to10(num.substring(7));
        if(suffix >= 10) return null;
        BigDecimal prefixNum = new BigDecimal(prefix);
        BigDecimal suffixNum = new BigDecimal(suffix);
        suffixNum = suffixNum.divide(new BigDecimal(10));
        return prefixNum.add(suffixNum).multiply(new BigDecimal(sign));
    }

    /**
     * 返回8位36进制数字符串
     * @param num
     * @return
     */
    public static String transformNum10to36(BigDecimal num){
        char sign = num.signum()<0?'1':'0';
        num = num.abs();
        String prefix = transform10to36(num.longValue());
        String suffix = transform10to36(
                num.remainder(new BigDecimal(1))
                    .setScale(1,BigDecimal.ROUND_DOWN)
                    .multiply(new BigDecimal(10))
                    .longValue()
        );
        if(!suffix.isEmpty()) suffix = suffix.substring(0,1);
        String numStr = prefix + suffix;
        if(numStr.length() >= 8) return null;

        int padding = 8 - numStr.length() - 1;
        String[] paddingStr = new String[padding+1];
        Arrays.fill(paddingStr,"0");
        paddingStr[0] = String.valueOf(sign);
        return String.join("", paddingStr) + numStr;

    }
    public static long transform36to10(String num){
        return Long.valueOf(num,36);
    }
    public static String transform10to36(long num){
        List<Long> result = new ArrayList<>();
        if(num==0) result.add((long)0);
        while(num != 0){
            result.add(num % 36);
            num /= 36;
        }
        StringBuilder sb = new StringBuilder();
        result.forEach(e->{
            char ch = NumberSymbol.numberSymbol.get(e);
            sb.append(ch);
        });
       return sb.reverse().toString();
    }

}
