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

    private BigDecimal add_volume;//新增水量

    private LocalDateTime add_time;//新增用水量时间

    private Integer record_id;//对应充值记录id

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

    public BigDecimal getAdd_volume() {
        return add_volume;
    }

    public void setAdd_volume(BigDecimal add_volume) {
        this.add_volume = add_volume;
    }

    public LocalDateTime getAdd_time() {
        return add_time;
    }

    public void setAdd_time(LocalDateTime add_time) {
        this.add_time = add_time;
    }

    public Integer getRecord_id() {
        return record_id;
    }

    public void setRecord_id(Integer record_id) {
        this.record_id = record_id;
    }

    public ExtraWater(String registerId, BigDecimal add_volume, Integer record_id) {
        this.registerId = registerId;
        this.add_volume = add_volume;
        this.record_id = record_id;
    }
}
