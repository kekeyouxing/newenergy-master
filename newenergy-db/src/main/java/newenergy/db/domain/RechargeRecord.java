package newenergy.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_record")
public class RechargeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String registerId;
    private Integer amount;
    private LocalDateTime rechargeTime;
    private double rechargeVolume;
    private double remainVolume;
    private double updatedVolume;
    private String orderSn;
    private String userName;
    private String userPhone;
    private Integer state;
    private Integer delegate;
    private String transactionId;
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

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getDelegate() {
        return delegate;
    }

    public void setDelegate(Integer delegate) {
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

    public Integer getSafeChangedUserId() {
        return safeChangedUserid;
    }

    public void setSafeChangedUserId(Integer safeChangedUserId) {
        this.safeChangedUserid = safeChangedUserId;
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

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getSafeChangedUserid() {
        return safeChangedUserid;
    }

    public void setSafeChangedUserid(Integer safeChangedUserid) {
        this.safeChangedUserid = safeChangedUserid;
    }
}
