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

    public List<BatchRecord> findAll(int safeDelete){
        return repository.findAllBySafeDelete(safeDelete);
    }

    public int save(BatchRecord newenergyBatchRecord){
         BatchRecord batchRecord = repository.save(newenergyBatchRecord);
         return batchRecord.getId();
    }

    public void deleteByid(int id){
        repository.deleteById(id);
    }

    public List<BatchRecord> queryByCompanyAndSafeDelete(String company, Integer safeDelete){
        return repository.findAllByCompanyAndSafeDelete(company,safeDelete);
    }

    public BatchRecord queryById(int id){
        if (repository.findAllById(id).size()==0){
            return null;
        }else {
            return repository.findAllById(id).get(0);
        }
    }




}