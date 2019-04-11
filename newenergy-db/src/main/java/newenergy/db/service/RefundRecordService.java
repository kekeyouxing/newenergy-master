package newenergy.db.service;

import newenergy.db.domain.RefundRecord;
import newenergy.db.repository.RefundRecordRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefundRecordService extends LogicOperation<RefundRecord>{

    @Autowired
    private RefundRecordRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public RefundRecord addRefundRecord(RefundRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public RefundRecord updateRefundRecord(RefundRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteRefundRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

    public List<RefundRecord> queryAll(){
        return repository.findAll();
    }

    public void save(RefundRecord RefundRecord){
        repository.save(RefundRecord);
    }

    public void deleteById(int id){
        repository.deleteById(id);
    }
}
