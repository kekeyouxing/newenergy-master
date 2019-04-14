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
    public List<FaultRecord> findAllByRegisterId(String id);
    public List<FaultRecord> findAllByMonitorId(Integer id);
    public List<FaultRecord> findAllByServicerId(Integer id);
    public List<FaultRecord> findAllByState(Integer id);
    public List<FaultRecord> findAllByResult(Integer id);
}
