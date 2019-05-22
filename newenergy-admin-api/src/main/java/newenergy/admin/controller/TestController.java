package newenergy.admin.controller;

import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
public class TestController {

    RestTemplate restTemplate = new RestTemplate();
    String refundUrl = "http://localhost/wx/order/refund";

    @RequestMapping(value="testrefund")
    public Object test(){
        Map<String,Object> request = new HashMap<>();
        request.put("orderId",6);
        return restTemplate.postForObject(refundUrl,request,Object.class);
    }
}
