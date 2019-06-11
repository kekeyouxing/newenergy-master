package newenergy.core.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by HUST Corey on 2019-04-25.
 */
public class TimeUtil {
    public static Long getUTCSeconds(LocalDateTime localDateTime){
        return localDateTime==null?null:localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
    public static Long getSeconds(LocalDateTime localDateTime){
        return localDateTime==null?null:localDateTime.toEpochSecond(ZoneOffset.of("+8"));
    }
    public static Long getCurUTCSeconds(){
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }
    public static Long getCurSeconds(){
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }
    public static LocalDateTime getNow(){
        return LocalDateTime.now(ZoneOffset.of("+8"));
    }
    public static LocalDateTime getUTCNow(){
        return LocalDateTime.now();
    }
    public static boolean inMonth(int year , int month,  LocalDateTime time){
        if(year < 1970 || month < 1 || month > 12) return false;
        LocalDateTime start = LocalDateTime.of(year,month,1,0,0);
        LocalDateTime end = start.plusMonths(1);
        return time.toEpochSecond(ZoneOffset.UTC) >= start.toEpochSecond(ZoneOffset.UTC)
                && time.toEpochSecond(ZoneOffset.UTC) < end.toEpochSecond(ZoneOffset.UTC);
    }
    public static String getString(LocalDateTime localDateTime){
        if(localDateTime == null) return "";
        return String.format("%d-%02d-%02d %02d:%02d:%02d",
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond());
    }
    public static String getDateString(LocalDateTime localDateTime){
        if(localDateTime == null) return "";
        return  String.format("%d%02d%02d",
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth());
    }
}
