package newenergy.db.repository;

import newenergy.db.domain.NewenergyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewenergyOrderRepository extends JpaRepository<NewenergyOrder,Integer> {
//    @Query(value = "select plot_num from Resident r where r.register_id = ?1")
//    String findByRegisterId(String register_id);

//    @Query(value = "select plot_factor from CorrPlot c where c.plot_num = ?1")
//    Double findPlotFactorByPlotNum(String plot_num);

    public NewenergyOrder findFirstByOrderSn(String orderSn);
}
