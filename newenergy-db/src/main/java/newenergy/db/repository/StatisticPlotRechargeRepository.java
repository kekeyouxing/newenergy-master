package newenergy.db.repository;

import newenergy.db.domain.StatisticPlotRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticPlotRechargeRepository extends JpaRepository<StatisticPlotRecharge, Integer>, JpaSpecificationExecutor<StatisticPlotRecharge> {

}
