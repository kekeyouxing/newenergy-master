package newenergy.db.repository;

import newenergy.db.domain.NewenergyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NewenergyOrderRepository extends JpaRepository<NewenergyOrder,Integer> {
    @Query(value = "select plot_num from Resident where register_id = ?1")
    String findByRegisterId(String register_id);

    @Query(value = "select plot_factor from CorrPlot where plot_num = ?1")
    Double findPlotFactorByPlotNum(String plot_num);
}
