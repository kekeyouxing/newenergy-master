package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistic_plot_recharge")
public class StatisticPlotRecharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 2)
    private String plotNum;

    private Integer amount;

    private BigDecimal rechargeVolume;

    private BigDecimal plotFactor;

    private BigDecimal curUsed;

    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlotNum() {
        return plotNum;
    }

    public void setPlotNum(String plotNum) {
        this.plotNum = plotNum;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getRechargeVolume() {
        return rechargeVolume;
    }

    public void setRechargeVolume(BigDecimal rechargeVolume) {
        this.rechargeVolume = rechargeVolume;
    }

    public BigDecimal getPlotFactor() {
        return plotFactor;
    }

    public void setPlotFactor(BigDecimal plotFactor) {
        this.plotFactor = plotFactor;
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
}
