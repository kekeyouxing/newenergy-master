package newenergy.db.domain;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_credential")
public class BatchCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String company;
    private LocalDateTime rechargeTime;
    private String imgUrl;
    private int batchRecordId;
    private LocalDateTime safeChangedTime;
    private int safeChangedUserId;
    private int safeDelete;
    private int safeParent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getBatchRecordId() {
        return batchRecordId;
    }

    public void setBatchRecordId(int batchRecordId) {
        this.batchRecordId = batchRecordId;
    }

    public int getSafeChangedUserId() {
        return safeChangedUserId;
    }

    public void setSafeChangedUserId(int safeChangedUserId) {
        this.safeChangedUserId = safeChangedUserId;
    }

    public int getSafeDelete() {
        return safeDelete;
    }

    public void setSafeDelete(int safeDelete) {
        this.safeDelete = safeDelete;
    }

    public int getSafeParent() {
        return safeParent;
    }

    public void setSafeParent(int safeParent) {
        this.safeParent = safeParent;
    }

    public LocalDateTime getSafeChangedTime() {
        return safeChangedTime;
    }

    public void setSafeChangedTime(LocalDateTime safeChangedTime) {
        this.safeChangedTime = safeChangedTime;
    }
}
