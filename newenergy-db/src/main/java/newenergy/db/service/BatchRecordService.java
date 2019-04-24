package newenergy.db.service;

import newenergy.db.domain.BatchRecord;
import newenergy.db.repository.BatchRecordRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchRecordService extends LogicOperation<BatchRecord> {

    @Autowired
    private BatchRecordRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public BatchRecord addBatchRecord(BatchRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public BatchRecord updateBatchRecord(BatchRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteBatchRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

//    查询所有批量充值记录
    public List<BatchRecord> findAll(){
        return repository.findAllBySafeDelete(0);
    }

//   根据公司查询批量充值记录
    public List<BatchRecord> queryByPlotNumAndSafeDelete(String plotNum){
        return repository.findAllByPlotNumAndSafeDelete(plotNum,0);
    }

//    根据id查询批量充值记录
    public BatchRecord queryById(int id){
        return repository.findFirstById(id);
    }


}
