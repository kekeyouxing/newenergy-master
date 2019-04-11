package newenergy.db.service;

import newenergy.db.domain.BatchCredential;
import newenergy.db.repository.BatchCredentialRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchCredentialService extends LogicOperation<BatchCredential>{

    @Autowired
    private BatchCredentialRepository repository;


    /**
     * 添加记录
     * @param batchCredential 不包括id
     * @param userid 操作者用户id
     * @return
     */

    public BatchCredential addBatchCredential(BatchCredential batchCredential,Integer userid){
        return addRecord(batchCredential,userid, repository);
    }


    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public BatchCredential updateBatchCredential(BatchCredential require, Integer userid){
        return updateRecord(require,userid, repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteBatchCredential(Integer id, Integer userid){
        deleteRecord(id,userid, repository);
    }
    public List<BatchCredential> queryAll(){
        return repository.findAll();
    }

    public void save(BatchCredential credential){
        repository.save(credential);
    }

    public void deleteById(int id){
        repository.deleteById(id);
    }
}
