package newenergy.admin.controller;

import newenergy.admin.background.service.StorageService;
import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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
    @Autowired
    StorageService storageService;

    @RequestMapping(value="testrefund/{id}/{volume}")
    public void test(@PathVariable(value = "id") Integer id,
                       @PathVariable(value = "volume")BigDecimal volume){
        System.out.println(id+","+volume);
        storageService.addRefundWater(id,volume);
    }
}
