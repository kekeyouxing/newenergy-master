package newenergy.db.repository;

import newenergy.db.domain.BatchRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRelativeRepository extends JpaRepository<BatchRelative,Integer>{

//    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.batchRecordId = :batchRecordId")
    List<BatchRelative> findAllByBatchRecordId(Integer batch_record_id);

//    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.state = :state")
    List<BatchRelative> findAllByState(Integer state);

//    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.state = :state" +
//            " and batchRelative.batchRecordId =:batchRecordId")
    List<BatchRelative> findAllByBatchRecordIdAndState(Integer batch_record_id, Integer state);


}
