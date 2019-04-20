package newenergy.db.util;

/**
 * Created by HUST Corey on 2019-04-19.
 */
public class StringUtilCorey {
    /**
     * 判断字符串是否为空或空串
     * @param str nullable
     * @return true:empty false:not empty
     */
    public static boolean emptyCheck(String str){
        return str == null || str.trim().equals("");
    }

    /**
     * 返回模糊搜索的模式串
     * @param origin
     * @return
     */
    public static String getMod(String origin){return "%"+origin+"%";}
}
