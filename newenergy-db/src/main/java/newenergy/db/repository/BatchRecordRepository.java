package newenergy.db.repository;

import newenergy.db.domain.BatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface BatchRecordRepository extends JpaRepository<BatchRecord,Integer>, JpaSpecificationExecutor<BatchRecord> {

    List<BatchRecord> findAllByPlotNumAndSafeDelete(String plotNum, Integer safe_delete);

    BatchRecord findFirstById(Integer id);

    List<BatchRecord> findAllBySafeDelete(Integer safe_delete);

}
