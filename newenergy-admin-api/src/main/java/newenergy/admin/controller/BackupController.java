package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.core.util.RequestUtil;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.service.BackupService;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.util.StringUtilCorey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
    @Autowired
    NewenergyAdminService newenergyAdminService;

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
    @RequestMapping(value = "email",method = RequestMethod.POST)
    public boolean updateEmail(@RequestBody Map<String,Object> request, @AdminLoginUser NewenergyAdmin admin){
        Integer userid = admin.getId();
        if( !RequestUtil.checkMap(request,new String[]{"email"}) ) return false;
        String email = (String)request.get("email");
        if(StringUtilCorey.emptyCheck(email)) return false;
        admin.setEmail(email);
        return newenergyAdminService.updateById(admin,userid)!=null;
    }
}
