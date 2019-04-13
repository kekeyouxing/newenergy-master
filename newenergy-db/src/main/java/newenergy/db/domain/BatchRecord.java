package newenergy.db.domain;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_record")
public class BatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String company;
    private LocalDateTime rechargeTime;
    private Integer batchAdmin;
    private LocalDateTime safeChangedTime;
    private Integer safeChangedUserid;
    private Integer safeDelete;
    private Integer safeParent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public LocalDateTime getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(LocalDateTime rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public Integer getBatchAdmin() {
        return batchAdmin;
    }

    public void setBatchAdmin(Integer batchAdmin) {
        this.batchAdmin = batchAdmin;
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

    public void setSafeParent(Integer safeParent) {
        this.safeParent = safeParent;
    }

    public LocalDateTime getSafeChangedTime() {
        return safeChangedTime;
    }

    public void setSafeChangedTime(LocalDateTime safeChangedTime) {
        this.safeChangedTime = safeChangedTime;
    }
}
