package newenergy.db.repository;

import newenergy.db.domain.CorrType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrTypeRepository extends JpaRepository<CorrType, Integer>, JpaSpecificationExecutor<CorrType> {
    public List<CorrType> findAllBySafeDelete(Integer safe_delete);
}
