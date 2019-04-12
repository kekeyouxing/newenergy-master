package newenergy.db.service;

import newenergy.db.domain.DeviceRequire;
import newenergy.db.repository.DeviceRequireRepository;
import newenergy.db.template.LogicOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Created by HUST Corey on 2019-03-29.
 */
@Service
public class DeviceRequireService extends LogicOperation<DeviceRequire> {
    @Autowired
    private DeviceRequireRepository repository;

    /**
     * 添加记录
     * @param require 不包括id
     * @param userid 操作者用户id
     * @return
     */
    public DeviceRequire addDeviceRequire(DeviceRequire require, Integer userid){
        return addRecord(require,userid,repository);
    }

    /**
     * 逻辑修改
     * @param require 包括id
     * @param userid 操作者用户id
     * @return
     */
    public DeviceRequire updateDeviceRequire(DeviceRequire require, Integer userid){
        return updateRecord(require,userid,repository);
    }

    /**
     * 逻辑删除
     * @param id 待删除记录id
     * @param userid 操作者用户id
     */
    public void deleteDeviceRequire(Integer id, Integer userid){
        deleteRecord(id,userid,repository);
    }

}
