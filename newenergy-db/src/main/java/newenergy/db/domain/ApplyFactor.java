package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-04-08.
 */
@Entity
@Table(name = "apply_plot_factor")
public class ApplyFactor {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 2)
    private String plotNum;
    private BigDecimal originFactor;
    private BigDecimal updateFactor;
    private Integer laborId;
    private LocalDateTime applyTime;
    private Integer state;

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

    public BigDecimal getOriginFactor() {
        return originFactor;
    }

    public void setOriginFactor(BigDecimal originFactor) {
        this.originFactor = originFactor;
    }

    public BigDecimal getUpdateFactor() {
        return updateFactor;
    }

    public void setUpdateFactor(BigDecimal updateFactor) {
        this.updateFactor = updateFactor;
    }

    public Integer getLaborId() {
        return laborId;
    }

    public void setLaborId(Integer laborId) {
        this.laborId = laborId;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
