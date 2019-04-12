package newenergy.db.repository;

import newenergy.db.domain.RemainWater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemainWaterRespository extends JpaRepository<RemainWater,Integer> {
    RemainWater findFirstByRegisterId(String registerId);
}
