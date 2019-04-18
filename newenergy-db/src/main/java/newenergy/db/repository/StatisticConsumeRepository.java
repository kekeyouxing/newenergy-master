package newenergy.db.repository;

import newenergy.db.domain.StatisticConsume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StatisticConsumeRepository extends JpaRepository<StatisticConsume, Integer>, JpaSpecificationExecutor<StatisticConsume> {
}
