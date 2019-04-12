package newenergy.db.service;

import newenergy.db.domain.CorrFault;
import newenergy.db.repository.CorrFaultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class CorrFaultService {
    @Autowired
    private CorrFaultRepository repository;
    public String getFaultDtl(Integer fault_num){
        CorrFault corrFault = repository.findFirstByFaultNum(fault_num);
        return corrFault.getFaultDtl();
    }
    /**
     *  使用sql文件导入数据
     */
}
