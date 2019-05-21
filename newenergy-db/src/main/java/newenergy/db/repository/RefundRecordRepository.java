package newenergy.db.repository;

import newenergy.db.domain.RechargeRecord;
import newenergy.db.domain.RefundRecord;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRecordRepository extends JpaRepository<RefundRecord,Integer>,
        JpaSpecificationExecutor<RefundRecord> {

    RefundRecord findFirstById(Integer id);

    List<RefundRecord> findAll(Specification<RefundRecord> specification);

    List<RefundRecord> findAllByRecordIdAndSafeDelete(Integer recordRecord, Integer safeDelete);

    /**
     * By Zeng Hui
     */
    RefundRecord findFirstByIdAndSafeDelete(Integer id, Integer safeDelete);
    /**
     * By Zeng Hui
     */
    List<RefundRecord> findAllByRegisterIdAndSafeDelete(String registerId, Integer safeDelete);
}
