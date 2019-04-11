package newenergy.db.repository;

import newenergy.db.domain.ManualRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualRecordRepository extends JpaRepository<ManualRecord,Integer>{
}
