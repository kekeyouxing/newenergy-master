package newenergy.db.repository;

import newenergy.db.domain.RechargeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord,Integer>{


//    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where rechargeRecord.id = :id " )
    List<RechargeRecord> findAllById(Integer id);

//    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where rechargeRecord.registerId = :registerId " +
//            "and rechargeRecord.safeDelete = 0 and rechargeRecord.state = :state")
    List<RechargeRecord> findAllByRegisterIdAndSafeDeleteAndState(String register_id, Integer safe_delete, Integer state);

//    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where " +
//            " rechargeRecord.state = :state and rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findByRegisterIdAndSafeDelete(String register_id, Integer safe_delete);

//    @Query("select rechargeRecord from RechargeRecord rechargeRecord where " +
//            " rechargeRecord.registerId = :registerId and rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findBySafeDeleteAndState(Integer safe_delete, Integer state);

//    @Query("select rechargeRecord from RechargeRecord rechargeRecord where " +
//            " rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findAllBySafeDelete(Integer state);

    public RechargeRecord findFirstByOrderSn(String orderSn);

}
