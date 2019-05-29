package newenergy.admin.controller;

import newenergy.admin.background.service.StorageService;
import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.service.BackupService;
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
    @Autowired
    BackupService backupService;

    @RequestMapping("backup/save")
    public void backupSave(){
        try {
            backupService.saveBackup();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("backup/load")
    public void backupLoad(){
        try {
            backupService.loadBackup();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequestMapping("backup/send")
    public void backupSend(){
        try {
            backupService.sendBackup("714676641@qq.com");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
