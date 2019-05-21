package newenergy.admin.controller;

import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
public class TestController {
    public static class Test{
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
    @RequestMapping(value="test",method = RequestMethod.POST)
    public void test(@RequestBody Map<String,Object> request){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(request.get("data"));
        System.out.println(LocalDateTime.parse((String)request.get("dateTime"),df));
    }
}
