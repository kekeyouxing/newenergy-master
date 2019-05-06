package newenergy.db.repository;

import newenergy.db.domain.FaultRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by HUST Corey on 2019-03-27.
 */
public interface FaultRecordRepository extends JpaRepository<FaultRecord,Integer>
                                                , JpaSpecificationExecutor<FaultRecord> {
}
