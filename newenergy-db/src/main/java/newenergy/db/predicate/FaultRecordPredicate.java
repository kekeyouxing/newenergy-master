package newenergy.db.predicate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by HUST Corey on 2019-04-15.
 */
public  class FaultRecordPredicate{
    private Integer id;
    private String registerId;
    private String username;
    private Integer state;
    private Integer monitorId;
    private Integer servicerId;
    private List<String> plots;
    /**
     * 查询finishTime所在月的维修记录
     */
    private LocalDateTime finishTime;

    /**
     * 查询在faultTime之前的维修记录（即超时）
     */
    private LocalDateTime faultTime;

    public LocalDateTime getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(LocalDateTime faultTime) {
        this.faultTime = faultTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public List<String> getPlots() {
        return plots;
    }

    public void setPlots(List<String> plots) {
        this.plots = plots;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Integer monitorId) {
        this.monitorId = monitorId;
    }

    public Integer getServicerId() {
        return servicerId;
    }

    public void setServicerId(Integer servicerId) {
        this.servicerId = servicerId;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }
}