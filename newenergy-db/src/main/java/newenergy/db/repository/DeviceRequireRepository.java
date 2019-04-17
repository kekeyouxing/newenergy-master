package newenergy.db.repository;

import newenergy.db.domain.DeviceRequire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by HUST Corey on 2019-03-27.
 */
public interface DeviceRequireRepository extends JpaRepository<DeviceRequire,Integer> , JpaSpecificationExecutor<DeviceRequire> {
    DeviceRequire findFirstByPlotNum(String plotNum);
}
