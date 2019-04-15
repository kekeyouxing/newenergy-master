package newenergy.admin.controller;

import newenergy.db.domain.BatchCredential;
import newenergy.db.service.BatchCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by HUST Corey on 2019-03-26.
 */
@RestController
@RequestMapping("/batchCredential")
public class BatchCredentialController {
    @Autowired
    BatchCredentialService batchCredentialService;

    @RequestMapping(value = "test")
    public String test(){
        return "hello world";
    }

    //    添加批量充值记录
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@RequestBody MultipartFile file,
                      @RequestParam String company,
                      @RequestParam String fileName,
                      @RequestParam Integer batchRecordId,
                      @RequestParam Integer operatorId){
        if (!file.isEmpty()) {
            try {
                String name = UUID.randomUUID()+fileName;
                File path = new File(ResourceUtils.getURL("classpath:").getPath());
//                判断路径是否存在，不存在则新建一个目录
                File file1 = new File(path.getAbsolutePath(),"\\imgs");
                if (!file1.exists()){
                    file1.mkdir();
                }
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(path.getAbsolutePath(),"\\imgs\\"+name+".jpg")));//保存图片到目录下
                out.write(file.getBytes());
                out.flush();
                out.close();
                String filename=path.getAbsolutePath()+"\\imgs\\"+name+".jpg";
                BatchCredential batchCredential = new BatchCredential();
                batchCredential.setRechargeTime(LocalDateTime.now());
                batchCredential.setCompany(company);
                batchCredential.setImgUrl(filename);
                batchCredential.setBatchRecordId(batchRecordId);
                batchCredentialService.addBatchCredential(batchCredential,operatorId);
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }

    }

    //    根据id查询批量充值图片
    @RequestMapping(value = "/getImage",produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public BufferedImage getImages(@RequestParam Integer id) throws IOException {
        BatchCredential batchCredential = batchCredentialService.findById(id);
        System.out.println(""+batchCredential.getImgUrl());
        return ImageIO.read(new FileInputStream(new File(batchCredential.getImgUrl())));
    }

    //    根据id查询充值凭据记录
    @RequestMapping(value = "/findById",method = RequestMethod.GET)
    public BatchCredential find(@RequestParam Integer id)  {
        return batchCredentialService.findById(id);
    }
}