package newenergy.db.repository;

import newenergy.db.domain.BatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface BatchRecordRepository extends JpaRepository<BatchRecord,Integer>{

    List<BatchRecord> findAllByCompanyAndSafeDelete(String company, Integer safe_delete);

    List<BatchRecord> findAllById(Integer id);

    List<BatchRecord> findAllBySafeDelete(Integer safe_delete);

}
