package newenergy.admin.background.communicate.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class NumberSymbol {
    public final static Map<Long, Character> numberSymbol;
    static {
        numberSymbol = new HashMap<>();
        for(long i = 0; i < 10; i++){
            numberSymbol.put(i , (char)('0' + i));
        }
        for(long i = 10; i < 36; i++){
            numberSymbol.put(i , (char)('A' + i - 10));
        }
    }
}
