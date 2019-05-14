package newenergy.admin.controller;

import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
@RequestMapping("test")
public class TestController {
    @Autowired
    DeviceRequireService requireService;
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public DeviceRequire add(DeviceRequire require){
        return requireService.addDeviceRequire(require,11);
    }
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public DeviceRequire update(DeviceRequire require){
        return requireService.updateDeviceRequire(require,12);
    }
    @RequestMapping(value = "delete",method = RequestMethod.POST)
    public void delete(Integer recordid){
        requireService.deleteDeviceRequire(recordid,13);
    }
}
