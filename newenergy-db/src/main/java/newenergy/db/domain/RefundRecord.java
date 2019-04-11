package newenergy.db.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_record")
public class RefundRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String registerId;
    private int refundAmount;
    private BigDecimal refundVolume;
    private LocalDateTime refundTime;
    private int recordId;
    private int checkId;
    private int rechargeId;
    private int state;
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

    public int getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(int refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundVolume() {
        return refundVolume;
    }

    public void setRefundVolume(BigDecimal refundVolume) {
        this.refundVolume = refundVolume;
    }

    public LocalDateTime getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(LocalDateTime refundTime) {
        this.refundTime = refundTime;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(int rechargeId) {
        this.rechargeId = rechargeId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
