package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "recharge_extra_water",
        indexes = @Index(columnList = "registerId")
)
public class ExtraWater {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 14)
    private String registerId;//登记号

    private BigDecimal addVolume;//新增水量

    private LocalDateTime addTime;//新增用水量时间

    private Integer recordId;//对应充值记录id

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

    public BigDecimal getAddVolume() {
        return addVolume;
    }

    public void setAddVolume(BigDecimal addVolume) {
        this.addVolume = addVolume;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public void setRecord_id(Integer record_id) {
        this.recordId = record_id;
    }

    public ExtraWater(String registerId, BigDecimal add_volume, Integer record_id) {
        this.registerId = registerId;
        this.addVolume = add_volume;
        this.recordId = record_id;
    }
}
