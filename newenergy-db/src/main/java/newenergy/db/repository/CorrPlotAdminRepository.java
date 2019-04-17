package newenergy.db.repository;

import newenergy.db.domain.CorrPlotAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by HUST Corey on 2019-04-11.
 */
public interface CorrPlotAdminRepository extends JpaRepository<CorrPlotAdmin,Integer>,
                                                JpaSpecificationExecutor<CorrPlotAdmin> {
    public CorrPlotAdmin findFirstByPlotNumAndSafeDelete(String plotNum, Integer safeDelete);
    public List<CorrPlotAdmin> findAllByMonitorIdAndSafeDelete(Integer monitorId, Integer safeDelete);
}
