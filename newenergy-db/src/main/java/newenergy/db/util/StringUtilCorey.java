package newenergy.db.util;

/**
 * Created by HUST Corey on 2019-04-19.
 */
public class StringUtilCorey {
    /**
     *
     * @param str nullable
     * @return true:empty false:not empty
     */
    public static boolean emptyCheck(String str){
        return str == null || str.trim().equals("");
    }
}
