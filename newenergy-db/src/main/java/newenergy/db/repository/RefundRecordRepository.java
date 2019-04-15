package newenergy.db.repository;

import newenergy.db.domain.RefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRecordRepository extends JpaRepository<RefundRecord,Integer>{

    List<RefundRecord> findAllById(Integer id);

    List<RefundRecord> findAllByRegisterIdAndStateAndSafeDelete(String register_id,Integer state,Integer safe_delete);

    List<RefundRecord> findAllByRegisterIdAndSafeDelete(String register_id,Integer safe_delete);

    List<RefundRecord> findAllByStateAndSafeDelete(Integer state,Integer safe_delete);

    List<RefundRecord> findAllBySafeDelete(Integer state);


}
