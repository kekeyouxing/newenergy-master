package newenergy.db.repository;

import newenergy.db.domain.DeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by HUST Corey on 2019-03-27.
 */
public interface DeviceInfoRepository extends JpaRepository<DeviceInfo,Integer> {
    public DeviceInfo findFirstByRegisterId(String register_id);
}
