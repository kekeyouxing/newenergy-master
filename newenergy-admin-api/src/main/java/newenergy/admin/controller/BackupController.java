package newenergy.admin.controller;

import newenergy.db.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by HUST Corey on 2019-05-28.
 */
@RestController
@RequestMapping("admin/backup")
public class BackupController {
    @Value("${backup.basedir}")
    String basedir;
    @Value("${backup.loadname}")
    String loadname;
    @Value("${backup.encloadname}")
    String encLoadName;
    @Autowired
    BackupService backupService;

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public boolean uploadFile(@RequestParam(value="file") MultipartFile file){
        if(file.isEmpty())
            return false;
        File uploadDir = new File(basedir);
        if (!uploadDir.exists()){
            if(!uploadDir.mkdir()) return false;
        }
        File dest = new File(basedir+encLoadName);

        if(!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();
        try{
            file.transferTo(dest);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }

        return true;

    }
    @RequestMapping(value = "load", method = RequestMethod.POST)
    public boolean loadBackup(){
        try{
            backupService.loadBackup();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
