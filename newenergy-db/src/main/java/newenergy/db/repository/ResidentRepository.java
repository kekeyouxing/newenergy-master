package newenergy.db.repository;

import newenergy.db.domain.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Integer>, JpaSpecificationExecutor<Resident> {
    Resident findFirstByRegisterId(String register_id);
    Resident findFirstByUserNameAndRegisterIdAndSafeDelete(String username, String registerId, Integer safeDelete);
    /**
     * by Zeng Hui
     */
    Resident findFirstByRegisterIdAndSafeDelete(String registerId, Integer safeDelete);
    /**
     * by Zeng Hui
     */
    List<Resident> findAllByUserNameAndSafeDelete(String username, Integer safeDelete);
    /**
     * by Zeng Hui
     */
    List<Resident> findAllByPlotNumAndSafeDelete(String plotNum, Integer safeDelete);
}
