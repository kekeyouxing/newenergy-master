package newenergy.admin.controller;

import newenergy.admin.background.service.StorageService;
import newenergy.db.constant.AdminConstant;
import newenergy.db.domain.DeviceRequire;
import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.predicate.AdminPredicate;
import newenergy.db.service.BackupService;
import newenergy.db.service.NewenergyAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
public class TestController {
    @Autowired
    BackupService backupService;
    @Autowired
    NewenergyAdminService newenergyAdminService;

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
    @RequestMapping("role/test")
    public void roleTest(){
        List<NewenergyAdmin> admins = newenergyAdminService.findAdmin("servicer123");
        if( !admins.isEmpty() ){
            Integer[] roleids = admins.get(0).getRoleIds();
            String email = admins.get(0).getEmail();
            System.out.println("servicer123:");
            System.out.println("email:"+email);
            for (Integer roleid : roleids) {
                System.out.print(roleid+",");
            }
            System.out.println();
        }

        List<NewenergyAdmin> admins2 = newenergyAdminService.findAllByRoleIds(new Integer[]{AdminConstant.ROLE_BACKUP});
        NewenergyAdmin admin2 = null;
        if(!admins2.isEmpty())
            admin2 = admins2.get(0);
        if(admin2 != null){
            Integer[] roleids = admin2.getRoleIds();
            String email = admin2.getEmail();
            System.out.println("finded");
            System.out.println("username:"+admin2.getUsername());
            System.out.println("email:"+email);
            for(Integer roleid : roleids){
                System.out.print(roleid+",");
            }
            System.out.println();
        }

    }
}
