package newenergy.db.service;

import net.bytebuddy.TypeCache;
import newenergy.db.domain.ExtraWater;
import newenergy.db.repository.ExtraWaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public List<ExtraWater> findAll(){
        Sort sort = new Sort("addTime");
        return extraWaterRepository.findAll(sort);
    }

    public void deleteRecord(ExtraWater extraWater){
        extraWaterRepository.delete(extraWater);
    }
}
