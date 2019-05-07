package newenergy.admin.background.communicate.executor;

import newenergy.admin.background.communicate.constant.StateDtl;
import newenergy.admin.background.communicate.constant.StateShift;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUST Corey on 2019-05-07.
 */
public class ParsingResult {
    private boolean legaled;
    private String deviceNum;
    private BigDecimal remainWater;
    private BigDecimal rechargeWater;
    private int state;

    public void setLegaled(boolean legaled) {
        this.legaled = legaled;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public void setRemainWater(BigDecimal remainWater) {
        this.remainWater = remainWater;
    }

    public void setRechargeWater(BigDecimal rechargeWater) {
        this.rechargeWater = rechargeWater;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * 验证消息是否合法：长度，首尾标志
     * @return
     */
    public boolean legaled(){
        return legaled;
    }

    /**
     * 机器编码，字符串，4位36进制整数
     * 例如：00AZ
     * @return
     */
    public String deviceNum(){
        return deviceNum;
    }

    /**
     * 剩余流量，十进制小数，单位吨
     * 例如：1271.3
     * @return
     */
    public BigDecimal remainWater() {
        return remainWater;
    }

    /**
     * 累计充值流量，十进制小数，单位吨
     * 例如：1275.8
     * @return
     */
    public BigDecimal rechargeWater(){
        return rechargeWater;
    }

    /**
     * 模式是否为制冷
     * @return
     */
    public boolean coldMod(){
        return (state & (1 << StateShift.COLDMOD)) != 0;
    }

    /**
     * 是否开机
     * @return
     */
    public boolean started(){
        return (state & (1 << StateShift.STARTED)) != 0;
    }

    /**
     * 是否存在故障
     * @return
     */
    public boolean fault(){
        return (state & StateShift.FAULT_MASK) != 0;
    }

    /**
     * 返回故障消息
     * @return
     */
    public String faultDtl(){
        if(!fault()) return "";
        List<String> faults = new ArrayList<>();
        for(int i = StateShift.FAULT_START;
            i <= StateShift.FAULT_END;
            i++){
            if( (state & (1 << i)) != 0
                    && StateDtl.stateMap.containsKey(i)){
                faults.add(StateDtl.stateMap.get(i));
            }
        }
        return String.join(",",faults);
    }
}
