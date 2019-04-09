package newenergy.db.repository;

import newenergy.db.domain.RechargeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord,Integer>{


    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where rechargeRecord.id = :id " )
    List<RechargeRecord> findById(@Param("id") int id);

    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where rechargeRecord.registerId = :registerId " +
            "and rechargeRecord.safeDelete = 0 and rechargeRecord.state = :state")
    List<RechargeRecord> findByRegisterIdAndState(@Param("registerId") String registerId, @Param("state") int state);

    @Query("select  rechargeRecord from RechargeRecord rechargeRecord where " +
            " rechargeRecord.state = :state and rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findByState(@Param("state") int state);

    @Query("select rechargeRecord from RechargeRecord rechargeRecord where " +
            " rechargeRecord.registerId = :registerId and rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findByRegisterId(@Param("registerId") String registerId);

    @Query("select rechargeRecord from RechargeRecord rechargeRecord where " +
            " rechargeRecord.safeDelete = 0 ")
    List<RechargeRecord> findAll();

}
