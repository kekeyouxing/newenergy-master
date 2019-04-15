package newenergy.db.repository;

import newenergy.db.domain.CorrAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrAddressRepository extends JpaRepository<CorrAddress, Integer>, JpaSpecificationExecutor<CorrAddress> {
    public List<CorrAddress> findBySafeDelete(Integer safe_delete);
}
