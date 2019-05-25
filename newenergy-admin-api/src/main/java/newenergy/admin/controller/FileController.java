package newenergy.admin.controller;

import newenergy.db.service.BatchRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by HUST Corey on 2019/2/19.
 */
@RestController
@RequestMapping("/admin/file")
public class FileController {

    @Autowired
    BatchRecordService batchRecordService;

    private String uploadDir = "C:/images/";
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFile(@RequestParam(value="file") MultipartFile file){
        String filename;
        if(file.isEmpty())
            return null;
        File file1 = new File("c:\\images");
        if (!file1.exists()){
            file1.mkdir();
        }
        String originName = file.getOriginalFilename();
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        filename = UUID.randomUUID() + suffix;

        File dest = new File(uploadDir + filename);
        if(!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();
        try{
            file.transferTo(dest);
        }catch (IOException e){
            return null;
        }
        return filename;
    }

    @RequestMapping(value = "/credential",method = RequestMethod.GET)
    public boolean downloadFile(@RequestParam Integer id,
                                HttpServletResponse response){
        try {
            String filename = batchRecordService.queryById(id).getImgUrl();
            String path = uploadDir + filename;
            File file = new File(path);
            OutputStream outputStream = response.getOutputStream();
            response.setContentType("application/x-download");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename="+filename);
            outputStream.write(FileCopyUtils.copyToByteArray(file));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
