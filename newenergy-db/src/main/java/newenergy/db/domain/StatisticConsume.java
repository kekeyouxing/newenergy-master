package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistic_consume")
public class StatisticConsume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 14)
    private String registerId;

    private BigDecimal curRecharge;

    private BigDecimal lastRemain;

    private BigDecimal curRemain;

    private BigDecimal curUsed;

    private Integer curAmount;

    private LocalDateTime updateTime;

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

    public BigDecimal getCurRecharge() {
        return curRecharge;
    }

    public void setCurRecharge(BigDecimal curRecharge) {
        this.curRecharge = curRecharge;
    }

    public BigDecimal getLastRemain() {
        return lastRemain;
    }

    public void setLastRemain(BigDecimal lastRemain) {
        this.lastRemain = lastRemain;
    }

    public BigDecimal getCurRemain() {
        return curRemain;
    }

    public void setCurRemain(BigDecimal curRemain) {
        this.curRemain = curRemain;
    }

    public BigDecimal getCurUsed() {
        return curUsed;
    }

    public void setCurUsed(BigDecimal curUsed) {
        this.curUsed = curUsed;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getCurAmount() {
        return curAmount;
    }

    public void setCurAmount(Integer curAmount) {
        this.curAmount = curAmount;
    }
}
