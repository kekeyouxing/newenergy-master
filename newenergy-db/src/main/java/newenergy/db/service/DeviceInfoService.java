package newenergy.db.service;

import newenergy.db.domain.DeviceInfo;
import newenergy.db.repository.DeviceInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by HUST Corey on 2019-03-27.
 */
@Service
public class DeviceInfoService {
    @Autowired
    private DeviceInfoRepository repository;
    public DeviceInfo getDeviceInfo(String registerId){
        return repository.findFirstByRegisterId(registerId);
    }
    public DeviceInfo addDeviceInfo(DeviceInfo info){
        return repository.save(info);
    }

    public void updateDevice(String deviceNum, Integer active, Integer faultNum){
        DeviceInfo info = repository.findFirstByDeviceNum(deviceNum);
        if(Objects.nonNull(active)){
            info.setActive(active);
        }
        if(Objects.nonNull(faultNum)){
            info.setFaultNum(faultNum);
        }
        info.setUpdateTime(LocalDateTime.now());
        repository.save(info);
    }
    public DeviceInfo setMonitor(String registerId, Integer userid){
        DeviceInfo info = repository.findFirstByRegisterId(registerId);
        info.setMonitorId(userid);
        return repository.save(info);
    }
    public DeviceInfo setServicer(String registerId, Integer userid){
        DeviceInfo info = repository.findFirstByRegisterId(registerId);
        info.setServicerId(userid);
        return repository.save(info);
    }

}
