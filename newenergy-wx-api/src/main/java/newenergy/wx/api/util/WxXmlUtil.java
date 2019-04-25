package newenergy.wx.api.util;

/**
 * Created by HUST Corey on 2019-04-25.
 */
public class WxXmlUtil {
    public static String getCdata(String origin){
        return "<![CDATA[" + origin + "]]>";
    }
}
