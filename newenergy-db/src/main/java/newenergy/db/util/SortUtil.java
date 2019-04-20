package newenergy.db.util;

import org.apache.logging.log4j.util.PropertySource;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-04-20.
 */
public class SortUtil {
    /**
     * 汉字排序
     */
    static class ChineseCompator implements Comparator<Map<String,Object>>{
        private String propertyName;
        private Collator chineseCollator;
        ChineseCompator(String propertyName){
            this.propertyName = propertyName;
            this.chineseCollator = Collator.getInstance(Locale.CHINA);
        }
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            if(StringUtilCorey.emptyCheck(propertyName)
                    || !o1.containsKey(propertyName)
                    || !o2.containsKey(propertyName)){
                return o1.hashCode() - o2.hashCode();
            }
            Object v1 = o1.get(propertyName);
            Object v2 = o2.get(propertyName);
            if(String.class.isInstance(v1) && String.class.isInstance(v2)){
                return chineseCollator.compare((String)v1,(String)v2);
            }
            return o1.hashCode() - o2.hashCode();
        }
    }
    public static void sortByChinese(List<Map<String,Object>> list, String propertyName){
        ChineseCompator chineseCompator = new ChineseCompator(propertyName);
        list.sort(chineseCompator);
    }

}
