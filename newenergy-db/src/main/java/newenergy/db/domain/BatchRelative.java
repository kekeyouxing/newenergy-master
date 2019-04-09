package newenergy.db.domain;

import javax.persistence.*;

@Entity
@Table(name = "batch_relative")
public class BatchRelative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private int batchRecordId;
    private int rechargeRecordId;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBatchRecordId() {
        return batchRecordId;
    }

    public void setBatchRecordId(int batchRecordId) {
        this.batchRecordId = batchRecordId;
    }

    public int getRechargeRecordId() {
        return rechargeRecordId;
    }

    public void setRechargeRecordId(int rechargeRecordId) {
        this.rechargeRecordId = rechargeRecordId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
