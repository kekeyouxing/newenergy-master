package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recharge_record")
public class RechargeRecord implements Cloneable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String registerId;
    private String plotNum;
    private Integer amount;
    private LocalDateTime rechargeTime;
    private BigDecimal rechargeVolume;
    private BigDecimal remainVolume;
    private BigDecimal updatedVolume;
    private String orderSn;
    private String userName;
    private String userPhone;
    private Integer checkId;
    private Integer state;
    private Integer delegate;
    private String transactionId;
    private LocalDateTime safeChangedTime;
    private Integer safeChangedUserid;
    private Integer safeDelete;
    private Integer safeParent;
    private Integer batchRecordId;
    private Integer reviewState;

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

    public String getPlotNum() {
        return plotNum;
    }

    public void setPlotNum(String plotNum) {
        this.plotNum = plotNum;
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

    public Integer getCheckId() {
        return checkId;
    }

    public void setCheckId(Integer checkId) {
        this.checkId = checkId;
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

    public Integer getBatchRecordId() {
        return batchRecordId;
    }

    public void setBatchRecordId(Integer batchRecordId) {
        this.batchRecordId = batchRecordId;
    }

    public Integer getReviewState() {
        return reviewState;
    }

    public void setReviewState(Integer reviewState) {
        this.reviewState = reviewState;
    }

    @Override
    public Object clone() throws CloneNotSupportedException{
        return (RechargeRecord)super.clone();
    }
}
