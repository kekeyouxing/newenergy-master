package newenergy.db.service;

import newenergy.db.domain.ManualRecord;
import newenergy.db.repository.ManualRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ManualRecordService {

    @Autowired
    private ManualRecordRepository manualRecordRepository;

//    查询所有人工操作记录
    public List<ManualRecord> queryAll(){
        return manualRecordRepository.findAll();
    }

//    保存批量充值记录
    public void add(Integer operatorId,Integer ip,Integer event,Integer recordId){
        ManualRecord manualRecord = new ManualRecord();
        manualRecord.setLaborId(operatorId);
        manualRecord.setEvent(event);
        manualRecord.setLaborIp(ip);
        manualRecord.setRecordId(recordId);
        manualRecord.setOperateTime(LocalDateTime.now());
        manualRecordRepository.save(manualRecord);
    }

}
