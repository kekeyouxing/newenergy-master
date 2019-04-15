package newenergy.db.repository;

import newenergy.db.domain.ExtraWater;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtraWaterRepository extends JpaRepository<ExtraWater,Integer> {

    @Override
    List<ExtraWater> findAll(Sort sort);
}
