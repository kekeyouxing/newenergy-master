package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "corr_plot")
public class CorrPlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //小区编号，从00到zz，索引
    @Column(length = 2)
    private String plotNum;

    //小区信息
    private String plotDtl;

    //充值系数，元/吨
    private Double plotFactor;

    //安全属性：添加时间
    private LocalDateTime safeChangedTime;

    //安全属性： 添加人id
    private Integer safeChangedUserid;

    //安全属性：是否删除，1删除，0未删除
    @Column(columnDefinition = "tinyint default 0")
    private Integer safeDelete;

    //安全属性：上次修改记录的id
    private  Integer sageParent;

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

    public String getPlotDtl() {
        return plotDtl;
    }

    public void setPlotDtl(String plotDtl) {
        this.plotDtl = plotDtl;
    }

    public Double getPlotFactor() {
        return plotFactor;
    }

    public void setPlotFactor(Double plotFactor) {
        this.plotFactor = plotFactor;
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

    public Integer getSageParent() {
        return sageParent;
    }

    public void setSageParent(Integer sageParent) {
        this.sageParent = sageParent;
    }
}
