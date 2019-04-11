package newenergy.db.service;

import newenergy.db.domain.BatchRelative;
import newenergy.db.repository.BatchRelativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchRelativeService {

    @Autowired
    private BatchRelativeRepository batchRelativeRepository;

    public List<BatchRelative> findByBatchRecordIdAndState(int batchRecoreId, int state){
//        batchRecord，state默认值为-1，若检测到-1，返回所有类型，否则，返回指定类型的关系记录
        if ((batchRecoreId==-1)&&(state==-1)){
            return batchRelativeRepository.findAll();
        }else if (batchRecoreId==-1){
            return batchRelativeRepository.findAllByState(state);
        }else if (state==-1){
            return batchRelativeRepository.findAllByBatchRecordId(batchRecoreId);
        }else {
            return batchRelativeRepository.findAllByBatchRecordIdAndState(batchRecoreId ,state);
        }

    }
}
