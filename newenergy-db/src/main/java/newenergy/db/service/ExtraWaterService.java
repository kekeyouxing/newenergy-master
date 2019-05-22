package newenergy.db.service;

import net.bytebuddy.TypeCache;
import newenergy.db.domain.ExtraWater;
import newenergy.db.repository.ExtraWaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExtraWaterService {
    @Autowired
    private ExtraWaterRepository extraWaterRepository;

    public ExtraWater add(ExtraWater extraWater){
        extraWater.setAddTime(LocalDateTime.now());
        return extraWaterRepository.save(extraWater);
    }

    /**
     * 新增用水量记录
     * @param registerId 设备号
     * @param addVolume 新增水量
     * @param recordId 对应充值记录id
     * @param addAmount 充值金额
     * @return
     */
    public ExtraWater add(String registerId, BigDecimal addVolume, Integer recordId, Integer addAmount){
        ExtraWater extraWater = null;
        extraWater = new ExtraWater(registerId,addVolume,recordId,addAmount);
        extraWater.setAddTime(LocalDateTime.now());
        return extraWaterRepository.save(extraWater);
    }

    public List<ExtraWater> findAll(){
        Sort sort = Sort.by(Sort.Direction.ASC,"addTime");
//        Sort sort = new Sort("addTime");
        return extraWaterRepository.findAll(sort);
    }

    public void deleteRecord(ExtraWater extraWater){
        extraWaterRepository.delete(extraWater);
    }
}
