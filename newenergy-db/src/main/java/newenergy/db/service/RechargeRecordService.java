package newenergy.db.service;

import newenergy.db.domain.RechargeRecord;
import newenergy.db.repository.RechargeRecordRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechargeRecordService extends LogicOperation<RechargeRecord>{

    @Autowired
    private RechargeRecordRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public RechargeRecord addRechargeRecord(RechargeRecord require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public RechargeRecord updateRechargeRecord(RechargeRecord require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteRechargeRecord(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

    public List<RechargeRecord> queryAll(){
        return repository.findAll();
    }

    public void save(RechargeRecord newenergyRechargeRecord){
        repository.save(newenergyRechargeRecord);
    }

    public void deleteById(int id){
        repository.deleteById(id);
    }

    public List<RechargeRecord> queryByRegisterId(String registerId){
        return repository.findByRegisterId(registerId);
    }

    public List<RechargeRecord> queryByState(int state){
        return repository.findByState(state);
    }

    public List<RechargeRecord> queryByRegisterIdAndState(String registerId,int state){
        return repository.findByRegisterIdAndState(registerId,state);
    }

    public RechargeRecord queryById(int id){
        if (repository.findById(id).size()==0){
            return null;
        }else {
            return repository.findById(id).get(0);
        }
    }
}
