package newenergy.db.service;

import newenergy.db.domain.ExtraWater;
import newenergy.db.repository.ExtraWaterRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExtraWaterService {
    @Autowired
    private ExtraWaterRespository extraWaterRespository;

    public ExtraWater add(ExtraWater extraWater){
        extraWater.setAdd_time(LocalDateTime.now());
        return extraWaterRespository.save(extraWater);
    }
}
