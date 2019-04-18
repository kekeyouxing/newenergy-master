package newenergy.db.service;

import newenergy.db.domain.RemainWater;
import newenergy.db.repository.RemainWaterRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RemainWaterService{
    @Autowired
    private RemainWaterRespository remainWaterRespository;

    /**
     * 根据登记号查找剩余水量的信息
     * @param registerId 登记号
     * @return RemainWater的一个实例
     */
    public RemainWater findByRegisterId(String registerId) {
        return remainWaterRespository.findFirstByRegisterId(registerId);
    }
    public void updateRemainWater(RemainWater remainWater){
        remainWaterRespository.saveAndFlush(remainWater);
    }

    public List<RemainWater> findAll() {
        return remainWaterRespository.findAll();
    }
}
