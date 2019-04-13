package newenergy.db.domain;

import java.util.List;

public class BatchAndRecharge {

    private BatchRecord batchRecord;

    private List<RechargeRecord> rechargeRecords;

    public BatchRecord getBatchRecord() {
        return batchRecord;
    }

    public void setBatchRecord(BatchRecord batchRecord) {
        this.batchRecord = batchRecord;
    }

    public List<RechargeRecord> getRechargeRecords() {
        return rechargeRecords;
    }

    public void setRechargeRecords(List<RechargeRecord> rechargeRecords) {
        this.rechargeRecords = rechargeRecords;
    }
}
