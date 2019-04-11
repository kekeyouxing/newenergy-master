package newenergy.db.service;

import newenergy.db.domain.RechargeRecord;
import newenergy.db.repository.RechargeRecordRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RechargeRecordService extends LogicOperation<RechargeRecord> {

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

    public List<RechargeRecord> findByRegisterIdAndSafeDeleteAndState(String registerId, int safeDelete, int state){
//        safeDelete默认为0，registerid默认“”，state默认-1，为默认值时，插叙你所有类型，否则，查询指定类型
        if (registerId.equals("")&&(state==-1)){
            return repository.findAllBySafeDelete(safeDelete);
        }else if (registerId.equals("")){
            return repository.findAllBySafeDeleteAndState(safeDelete,state);
        }else if (state==-1){
            System.out.println(registerId);
            return repository.findAllByRegisterIdAndSafeDelete(registerId,safeDelete);
        }else {
            return repository.findAllByRegisterIdAndSafeDeleteAndState(registerId,safeDelete,state);
        }

    }

    public RechargeRecord findById(int id){
        if (repository.findAllById(id).size()==0){
            return null;
        }else {
            return repository.findAllById(id).get(0);
        }
    }
}
