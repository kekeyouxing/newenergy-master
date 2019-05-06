package newenergy.core.util;

import java.util.List;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-23.
 */
public class RequestUtil {
    /**
     * 若properties的所有参数都存在，返回true，否则返回false
     * @param param 参数map
     * @param properties 参数名
     * @return
     */
    public static boolean checkMap(Map<String,Object> param, String[] properties){
        if(param == null)
            return false;
        boolean contains = true;
        for(String property : properties){
            if(!param.containsKey(property))
                contains = false;
        }
        return contains;
    }
}
