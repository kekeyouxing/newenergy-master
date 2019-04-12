package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "corr_type")
public class CorrType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //机型编号，从00到zz，索引
    @Column(length = 2)
    private String typeNum;

    //机型信息
    private String typeDtl;

    //额定流量，单位T/h
    private Double ratedFlow;

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

    public String getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(String typeNum) {
        this.typeNum = typeNum;
    }

    public String getTypeDtl() {
        return typeDtl;
    }

    public void setTypeDtl(String typeDtl) {
        this.typeDtl = typeDtl;
    }

    public Double getRatedFlow() {
        return ratedFlow;
    }

    public void setRatedFlow(Double ratedFlow) {
        this.ratedFlow = ratedFlow;
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
