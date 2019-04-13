package newenergy.admin.controller;

import newenergy.db.domain.DeviceRequire;
import newenergy.db.service.DeviceRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
@RequestMapping("admin")
public class TestController {
    @Autowired
    DeviceRequireService requireService;
    @RequestMapping(value = "test")
    public String test(){
        return "hello world";
    }

}
