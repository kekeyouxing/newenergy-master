package newenergy.db.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "corr_address")
public class CorrAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //地址编号，aabbc，aa是小区编号00-zz，bb是楼栋数00-zz，c是单元号0-z，索引
    @Column(length = 5)
    private String addressNum;

    //小区
    private String addressPlot;

    //具体地址信息，楼栋和单元
    private Integer addressBlock;

    private Integer addressUnit;

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

    public String getAddressNum() {
        return addressNum;
    }

    public void setAddressNum(String addressNum) {
        this.addressNum = addressNum;
    }

    public String getAddressPlot() {
        return addressPlot;
    }

    public void setAddressPlot(String addressPlot) {
        this.addressPlot = addressPlot;
    }

    public Integer getAddressBlock() {
        return addressBlock;
    }

    public void setAddressBlock(Integer addressBlock) {
        this.addressBlock = addressBlock;
    }

    public Integer getAddressUnit() {
        return addressUnit;
    }

    public void setAddressUnit(Integer addressUnit) {
        this.addressUnit = addressUnit;
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
