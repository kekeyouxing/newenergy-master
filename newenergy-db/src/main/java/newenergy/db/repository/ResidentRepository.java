package newenergy.db.repository;

import newenergy.db.domain.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Integer>, JpaSpecificationExecutor<Resident> {
    Resident findFirstByRegisterIdAndSafeDelete(String register_id,Integer safe_delete);

    Resident findFirstByUserNameAndRegisterIdAndSafeDelete(String username, String registerId, Integer safeDelete);

    List<Resident> findByPlotNumAndSafeDelete(String plotNum, Integer safeDelete);
}
