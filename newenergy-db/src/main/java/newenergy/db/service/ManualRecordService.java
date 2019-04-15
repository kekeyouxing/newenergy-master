package newenergy.db.service;

import newenergy.db.domain.ManualRecord;
import newenergy.db.repository.ManualRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManualRecordService {

    @Autowired
    private ManualRecordRepository manualRecordRepository;

    public List<ManualRecord> queryAll(){
        return manualRecordRepository.findAll();
    }

    public void save(ManualRecord manualRecord){
        manualRecordRepository.save(manualRecord);
    }

    public void deleteById(int id){
        manualRecordRepository.deleteById(id);
    }
}
