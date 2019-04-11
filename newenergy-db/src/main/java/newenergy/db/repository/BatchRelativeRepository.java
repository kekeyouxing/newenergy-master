package newenergy.db.repository;

import newenergy.db.domain.BatchRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchRelativeRepository extends JpaRepository<BatchRelative,Integer>{

 List<BatchRelative> findAllByBatchRecordId(Integer batch_record_id);

 List<BatchRelative> findAllByState(Integer state);

  List<BatchRelative> findAllByBatchRecordIdAndState(Integer batch_record_id, Integer state);


}
