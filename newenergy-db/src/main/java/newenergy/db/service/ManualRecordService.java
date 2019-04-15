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

//    查询所有批量充值记录
    public List<ManualRecord> queryAll(){
        return manualRecordRepository.findAll();
    }

//    保存批量充值记录
    public void save(ManualRecord manualRecord){
        manualRecordRepository.save(manualRecord);
    }

}
