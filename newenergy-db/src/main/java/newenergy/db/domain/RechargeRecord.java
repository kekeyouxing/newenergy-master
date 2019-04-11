package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_record")
public class RechargeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private String registerId;
    private int amount;
    private LocalDateTime rechargeTime;
    private BigDecimal rechargeVolume;
    private BigDecimal remainVolume;
    private BigDecimal updatedVolume;
    private String userName;
    private String userPhone;
    private int state;
    private int delegate;
    private String transactionId;
    private LocalDateTime safeChangedTime;
    private int safeChangedUserId;
    private int safeDelete;
    private int safeParent;
    private int batchRecordId;
    private int reviewState;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDateTime getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(LocalDateTime rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public BigDecimal getRechargeVolume() {
        return rechargeVolume;
    }

    public void setRechargeVolume(BigDecimal rechargeVolume) {
        this.rechargeVolume = rechargeVolume;
    }

    public BigDecimal getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(BigDecimal remainVolume) {
        this.remainVolume = remainVolume;
    }

    public BigDecimal getUpdatedVolume() {
        return updatedVolume;
    }

    public void setUpdatedVolume(BigDecimal updatedVolume) {
        this.updatedVolume = updatedVolume;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDelegate() {
        return delegate;
    }

    public void setDelegate(int delegate) {
        this.delegate = delegate;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getSafeChangedTime() {
        return safeChangedTime;
    }

    public void setSafeChangedTime(LocalDateTime safeChangedTime) {
        this.safeChangedTime = safeChangedTime;
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

    public int getBatchRecordId() {
        return batchRecordId;
    }

    public void setBatchRecordId(int batchRecordId) {
        this.batchRecordId = batchRecordId;
    }

    public int getReviewState() {
        return reviewState;
    }

    public void setReviewState(int reviewState) {
        this.reviewState = reviewState;
    }
}
