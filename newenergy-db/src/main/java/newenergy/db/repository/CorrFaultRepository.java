package newenergy.db.repository;

import newenergy.db.domain.CorrFault;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by HUST Corey on 2019-03-27.
 */
public interface CorrFaultRepository extends JpaRepository<CorrFault,Integer> {
    public CorrFault findFirstByFaultNum(Integer fault_num);
}
