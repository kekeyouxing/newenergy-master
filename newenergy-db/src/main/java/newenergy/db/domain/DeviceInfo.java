package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Entity
@Table(
        name = "device_info",
        indexes = {
                @Index(columnList = "registerId")
        }
)
public class DeviceInfo {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 14)
    private String registerId;
    private String deviceNum;
    private Integer active;
    private Integer faultNum;
    private LocalDateTime updateTime;
    private Integer monitorId;
    private Integer servicerId;

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }


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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getFaultNum() {
        return faultNum;
    }

    public void setFaultNum(Integer faultNum) {
        this.faultNum = faultNum;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
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
}
