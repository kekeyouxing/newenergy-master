package newenergy.admin.background.communicate.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public final class StateDtl {
    /**
     * key : shift
     * value : detail
     *
     */
    public final static Map<Integer,String> stateMap;
    static{
        stateMap = new HashMap<>();
        stateMap.put(0,"电流2保护");
        stateMap.put(1,"系统2低压开关故障");
        stateMap.put(2,"系统2高压开关故障");
        stateMap.put(3,"热水温度传感器故障");
        stateMap.put(4,"系统1低压开关故障");
        stateMap.put(5,"系统1高压开关故障");
        stateMap.put(6,"缺相逆相保护");
        stateMap.put(7,"空调水流开关开路");
        stateMap.put(8,"电流1保护");
        stateMap.put(9,"水源出水传感器1故障");
        stateMap.put(10,"压缩机1排气温度探头故障");
        stateMap.put(11,"空调出水传感器故障");
        stateMap.put(12,"回水传感器故障");
        stateMap.put(13,"环境温度传感器故障");
    }
}
