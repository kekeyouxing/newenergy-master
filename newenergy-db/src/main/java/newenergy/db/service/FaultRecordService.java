package newenergy.db.service;

import newenergy.db.domain.FaultRecord;
import newenergy.db.repository.FaultRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class FaultRecordService {
    @Autowired
    private FaultRecordRepository repository;
    public List<FaultRecord> getRecordsByRegisterId(String registerId){
        return repository.findAllByRegisterId(registerId);
    }
    public List<FaultRecord> getRecordsByMonitorId(Integer id){
        return repository.findAllByMonitorId(id);
    }
    public List<FaultRecord> getRecordsByServiverId(Integer id){
        return repository.findAllByServicerId(id);
    }
    public List<FaultRecord> getRecordsByState(Integer state){
        return repository.findAllByState(state);
    }
    public List<FaultRecord> getRecordsByResult(Integer result){
        return repository.findAllByResult(result);
    }
    public FaultRecord addRecord(FaultRecord record){
        return repository.save(record);
    }
    /**
     * TODO 更新故障记录状态
     */
}
