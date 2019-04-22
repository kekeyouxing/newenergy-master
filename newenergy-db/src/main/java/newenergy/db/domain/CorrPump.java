package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "corr_pump")
public class CorrPump {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //所属机房编号，见数据对应表-机房
    @Column(length = 2)
    private String pumpNum;

    //机房所在小区
    private String plot;

    //机房信息
    private Integer pump;

    private String pumpDtl;

    //安全属性：添加时间
    private LocalDateTime safeChangedTime;

    //安全属性： 添加人id
    private Integer safeChangedUserid;

    //安全属性：是否删除，1删除，0未删除
    @Column(columnDefinition = "tinyint default 0")
    private Integer safeDelete;

    //安全属性：上次修改记录的id
    private  Integer safeParent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPumpNum() {
        return pumpNum;
    }

    public void setPumpNum(String pumpNum) {
        this.pumpNum = pumpNum;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Integer getPump() {
        return pump;
    }

    public void setPump(Integer pump) {
        this.pump = pump;
    }

    public String getPumpDtl() {
        return pumpDtl;
    }

    public void setPumpDtl(String pumpDtl) {
        this.pumpDtl = pumpDtl;
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

    public void setSafeParent(Integer sageParent) {
        this.safeParent = safeParent;
    }
}
