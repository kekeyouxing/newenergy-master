package newenergy.db.domain;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_record")
public class BatchRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private String company;
    private LocalDateTime rechargeTime;
    private int batchAdmin;
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

    public int getBatchAdmin() {
        return batchAdmin;
    }

    public void setBatchAdmin(int batchAdmin) {
        this.batchAdmin = batchAdmin;
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
