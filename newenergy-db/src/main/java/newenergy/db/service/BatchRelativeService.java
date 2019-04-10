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

    public List<BatchRelative> findAll(){
        return batchRelativeRepository.findAll();
    }

    public List<BatchRelative> findByBatchRecordId(int batchRecoreId){
        return batchRelativeRepository.findAllByBatchRecordId(batchRecoreId);
    }

    public List<BatchRelative> findByState(int state){
        return batchRelativeRepository.findAllByState(state);
    }

    public List<BatchRelative> findByBatchRecordIdAndState(int batchRecoreId, int state){
        return batchRelativeRepository.findAllByBatchRecordIdAndState(batchRecoreId ,state);
    }
}
