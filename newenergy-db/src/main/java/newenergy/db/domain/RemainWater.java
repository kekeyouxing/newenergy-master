package newenergy.db.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-04-08.
 */
@Entity
@Table(
        name = "recharge_remain_water",
        indexes = @Index(columnList = "registerId")
)
public class RemainWater {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 14)
    private String registerId;
    private LocalDateTime updateTime;
    private BigDecimal remainVolume;
    private BigDecimal curRecharge;
    private BigDecimal curFirstRemain;

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

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public BigDecimal getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(BigDecimal remainVolume) {
        this.remainVolume = remainVolume;
    }

    public BigDecimal getCurRecharge() {
        return curRecharge;
    }

    public void setCurRecharge(BigDecimal curRecharge) {
        this.curRecharge = curRecharge;
    }

    public BigDecimal getCurFirstRemain() {
        return curFirstRemain;
    }

    public void setCurFirstRemain(BigDecimal curFirstRemain) {
        this.curFirstRemain = curFirstRemain;
    }
}
