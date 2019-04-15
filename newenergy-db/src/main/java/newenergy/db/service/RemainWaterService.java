package newenergy.db.service;

import newenergy.db.domain.RemainWater;
import newenergy.db.repository.RemainWaterRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RemainWaterService{
    @Autowired
    private RemainWaterRespository remainWaterRespository;

    public RemainWater findByRegisterId(String registerId){
        return remainWaterRespository.findFirstByRegisterId(registerId);
    }
    public void updateRemainWater(RemainWater remainWater){
        remainWaterRespository.saveAndFlush(remainWater);
    }

}
