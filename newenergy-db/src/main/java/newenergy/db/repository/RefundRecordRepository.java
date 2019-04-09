package newenergy.db.repository;

import newenergy.db.domain.RefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRecordRepository extends JpaRepository<RefundRecord,Integer>{
}
