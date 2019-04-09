package newenergy.db.service;

import newenergy.db.domain.BatchRelative;
import newenergy.db.repository.BatchRelativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchRelativeService {

    @Autowired
    private BatchRelativeRepository BatchRelativeRepository;

    public List<BatchRelative> queryAll(){
        return BatchRelativeRepository.findAll();
    }

    public void save(BatchRelative BatchRelative){
        BatchRelativeRepository.save(BatchRelative);
    }

    public void deleteById(int id){
        BatchRelativeRepository.deleteById(id);
    }

    public List<BatchRelative> queryByBatchRecoreId(int batchRecoreId){
        return BatchRelativeRepository.queryAllByBatchRecordId(batchRecoreId);
    }

    public List<BatchRelative> queryByState(int state){
        return BatchRelativeRepository.queryAllByState(state);
    }

    public List<BatchRelative> queryByBatchRecoreIdAndState(int state,int batchRecordId){
        return BatchRelativeRepository.queryAllByStateAndBatchRecordId(state,batchRecordId);
    }
}
