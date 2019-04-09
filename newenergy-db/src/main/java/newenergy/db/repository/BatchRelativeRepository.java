package newenergy.db.repository;

import newenergy.db.domain.BatchRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRelativeRepository extends JpaRepository<BatchRelative,Integer>{

    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.batchRecordId = :batchRecordId")
    List<BatchRelative> queryAllByBatchRecordId(@Param("batchRecordId") int batchRecordId);

    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.state = :state")
    List<BatchRelative> queryAllByState(@Param("state") int state);

    @Query("select batchRelative from BatchRelative batchRelative where batchRelative.state = :state" +
            " and batchRelative.batchRecordId =:batchRecordId")
    List<BatchRelative> queryAllByStateAndBatchRecordId(@Param("state") int state, @Param("batchRecordId") int batchRecordId);

}
