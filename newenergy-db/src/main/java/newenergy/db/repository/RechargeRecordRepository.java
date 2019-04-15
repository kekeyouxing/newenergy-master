package newenergy.db.repository;

import newenergy.db.domain.RechargeRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord,Integer>{

    List<RechargeRecord> findAllById(Integer id);

    List<RechargeRecord> findAllByRegisterIdAndSafeDeleteAndState(String register_id, Integer safe_delete, Integer state);

    List<RechargeRecord> findAllByRegisterIdAndSafeDelete(String register_id, Integer safe_delete);

    List<RechargeRecord> findAllBySafeDeleteAndState(Integer safe_delete, Integer state);

    List<RechargeRecord> findAllBySafeDelete(Integer state);

    List<RechargeRecord> findAllByBatchRecordIdAndSafeDelete(Integer batch_record_id,Integer safe_delete);

    List<RechargeRecord> findAllByReviewStateAndSafeDelete(Integer review_state,Integer safe_delete);

    List<RechargeRecord> findAllByBatchRecordIdAndReviewStateAndSafeDelete(Integer batch_record_id,Integer review_state,Integer safe_delete);

    RechargeRecord findFirstByOrderSn(String orderSn);

    List<RechargeRecord> findAll(Specification<RechargeRecord> specification, Sort sort);

}
