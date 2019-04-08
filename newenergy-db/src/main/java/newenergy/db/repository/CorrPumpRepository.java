package newenergy.db.repository;

import newenergy.db.domain.CorrPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrPumpRepository extends JpaRepository<CorrPump, Integer>, JpaSpecificationExecutor<CorrPump> {
    public List<CorrPump> findAllBySafeDelete(Integer safe_delete);
}
