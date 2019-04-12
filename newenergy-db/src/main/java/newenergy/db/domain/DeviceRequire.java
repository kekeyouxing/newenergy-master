package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Entity
@Table(
        name = "device_require",
        indexes = {
                @Index(columnList = "plotInfo")
        }
)
public class DeviceRequire {
    @Id
    @GeneratedValue
    private Integer id;
    private String plotInfo;

    private Double requireVolume;
    private LocalDateTime updateTime;

    private Integer updateLoop;

    private LocalDateTime safeChangedTime;
    private Integer safeChangedUserid;
    private Integer safeDelete;
    private Integer safeParent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlotInfo() {
        return plotInfo;
    }

    public void setPlotInfo(String plotInfo) {
        this.plotInfo = plotInfo;
    }

    public Double getRequireVolume() {
        return requireVolume;
    }

    public void setRequireVolume(Double requireVolume) {
        this.requireVolume = requireVolume;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateLoop() {
        return updateLoop;
    }

    public void setUpdateLoop(Integer updateLoop) {
        this.updateLoop = updateLoop;
    }

    public LocalDateTime getSafeChangedTime() {
        return safeChangedTime;
    }

    public void setSafeChangedTime(LocalDateTime safeChangedTime) {
        this.safeChangedTime = safeChangedTime;
    }

    public Integer getSafeChangedUserid() {
        return safeChangedUserid;
    }

    public void setSafeChangedUserid(Integer safeChangedUserid) {
        this.safeChangedUserid = safeChangedUserid;
    }

    public Integer getSafeDelete() {
        return safeDelete;
    }

    public void setSafeDelete(Integer safeDelete) {
        this.safeDelete = safeDelete;
    }

    public Integer getSafeParent() {
        return safeParent;
    }

    public void setSafeParent(Integer safeParent) {
        this.safeParent = safeParent;
    }

}
