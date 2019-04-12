package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Entity
@Table(
        name = "fault_record",
        indexes = {
                @Index(columnList = "registerId"),
                @Index(columnList = "monitorId"),
                @Index(columnList = "servicerId"),
                @Index(columnList = "state"),
                @Index(columnList = "result")
        }
)
public class FaultRecord {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 14)
    private String registerId;

    private Integer monitorId;
    private Integer servicerId;
    private LocalDateTime faultTime;
    private LocalDateTime responseTime;
    private LocalDateTime finishTime;
    private Integer state;
    private Integer result;
    private String phenomenon;
    private String solution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
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

    public LocalDateTime getFaultTime() {
        return faultTime;
    }

    public void setFaultTime(LocalDateTime faultTime) {
        this.faultTime = faultTime;
    }

    public LocalDateTime getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(LocalDateTime responseTime) {
        this.responseTime = responseTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
