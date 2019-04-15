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


    /**
     * 按照id查找
     * @param id 待查id
     */
    public RefundRecord findById(int id){
        if (repository.findAllById(id).size()==0){
            return null;
        }else {
            return repository.findAllById(id).get(0);
        }
    }

//    根据注册id,审核状态查询退款记录,当注册id为空,状态为-1时,查询所有
    public List<RefundRecord> findByCondition(String registerId,int state,int safeDelete){
        if (registerId.equals("")&&(state==-1)){
            return repository.findAllBySafeDelete(safeDelete);
        }else if (registerId.equals("")){
            return repository.findAllByStateAndSafeDelete(state,safeDelete);
        }else if (state==-1){
            return repository.findAllByRegisterIdAndSafeDelete(registerId,safeDelete);
        }else {
            return repository.findAllByRegisterIdAndStateAndSafeDelete(registerId,state,safeDelete);
        }
    }

}
