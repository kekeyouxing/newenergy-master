package newenergy.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
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
    private double rechargeVolume;
    private double remainVolume;
    private double updatedVolume;
    private String userName;
    private String userPhone;
    private int state;
    private int delegate;
    private String transactionId;
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

    public double getRechargeVolume() {
        return rechargeVolume;
    }

    public void setRechargeVolume(double rechargeVolume) {
        this.rechargeVolume = rechargeVolume;
    }

    public double getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(double remainVolume) {
        this.remainVolume = remainVolume;
    }

    public double getUpdatedVolume() {
        return updatedVolume;
    }

    public void setUpdatedVolume(double updatedVolume) {
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
}
