package newenergy.db.service;

import newenergy.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.io.*;

/**
 * Created by HUST Corey on 2019-05-27.
 */
@Service
public class BackupService {
    @Value("${backup.basedir}")
    String path;
    @Value("${backup.username}")
    String username;
    @Value("${backup.password}")
    String password;
    @Value("${backup.database}")
    String database;
    @Value("${backup.loadname}")
    String loadName;
    @Value("${spring.mail.username}")
    String mailUser;
    @Value("${backup.encnum}")
    Integer numOfEncAndDec;
    @Value("${backup.encloadname}")
    String encLoadName;
    //idea显示错误
    @Autowired
    private JavaMailSender mailSender;

    String cmdWin = "cmd /c ";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void saveBackup() throws Exception{
        String filePath = path + "backup" + TimeUtil.getDateString(TimeUtil.getUTCNow()) + ".sql";
        String cmd = String.format("mysqldump -u%s -p%s %s -r %s --skip-lock-tables",
                username,password,database,filePath);
        //服务器上
        cmd = "C:\\mysql-8.0.16-winx64\\bin\\".concat(cmd);
        cmd = cmdWin.concat(cmd);

        logger.info(cmd);
        Process process = Runtime.getRuntime().exec(cmd);


        int processComplete = process.waitFor();
        if (processComplete == 0) {
            logger.info("备份成功：" + filePath);
            String encPath = path + "backup" + TimeUtil.getDateString(TimeUtil.getUTCNow());
            encFile(new File(filePath),new File(encPath));
            logger.info("加密成功：" + encPath);

        } else {
            logger.error("备份失败：" + filePath);
            throw new RuntimeException("备份数据库失败.");
        }

    }
    public boolean loadBackup() throws Exception{
        String encFile = path+encLoadName;
        String targetFile = path + loadName;
        boolean res = decFile(new File(encFile),new File(targetFile));
        if(!res){
            logger.error("加密文件不存在，解密失败");
            return false;
        }
        if( new File(encFile).delete() ){
            logger.info("删除加密文件成功");
        }else{
            logger.info("删除加密文件失败");
        }

//        String[] execCMD = new String[]{"mysql", database, "-u" + username, "-p" + password, "-h localhost", "-P 3306", "<", targetFile};
        String cmd = String.format("mysql %s -u%s -p%s -h localhost -P 3306",database,username,password);

        //服务器上
        cmd = "C:\\mysql-8.0.16-winx64\\bin\\".concat(cmd);
        cmd = cmdWin.concat(cmd);

        logger.info(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        OutputStream os = process.getOutputStream();


        FileInputStream fis = new FileInputStream(targetFile);
        byte[] buffer = new byte[1024];
        while(fis.available() > 0){
            int len = fis.read(buffer);
            os.write(buffer,0,len);
        }
        fis.close();
        os.close();


        int processComplete = process.waitFor();
        if(new File(targetFile).delete()){
            logger.info("删除解密文件成功");
        }else{
            logger.info("删除解密文件失败");
        }

        if (processComplete == 0) {
            logger.info("还原成功：" + TimeUtil.getString(TimeUtil.getUTCNow()));
            return true;
        } else {
            logger.error("还原失败：" + TimeUtil.getString(TimeUtil.getUTCNow()));
            return false;
        }

    }

    public void sendBackup(String toUser) throws Exception{
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(mailUser);
        helper.setTo(toUser);
        helper.setSubject(TimeUtil.getDateString(TimeUtil.getUTCNow()) + "数据库备份文件");
        helper.setText("");
        //加密后的文件
        String filename = path + "backup" + TimeUtil.getDateString(TimeUtil.getUTCNow());

        FileSystemResource file = new FileSystemResource(new File(filename));
        helper.addAttachment(filename, file);

        mailSender.send(mimeMessage);
    }

    public void encFile(File srcFile, File encFile) throws Exception {
        if(!srcFile.exists()){
            logger.info("source file not exixt");
            return;
        }

        if(!encFile.exists()){
            logger.info("encrypt file created");
            encFile.createNewFile();
        }
        InputStream fis  = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(encFile);

        int dataOfFile;
        while ((dataOfFile = fis.read()) > -1) {
            fos.write(dataOfFile^numOfEncAndDec);
        }

        fis.close();
        fos.flush();
        fos.close();
    }

    public boolean decFile(File srcFile, File decFile) throws Exception {
        if(!srcFile.exists()){
            logger.info("source file not exixt");
            return false;
        }

        if(!decFile.exists()){
            logger.info("decrypt file created");
            decFile.createNewFile();
        }
        InputStream fis  = new FileInputStream(srcFile);
        OutputStream fos = new FileOutputStream(decFile);

        int dataOfFile;
        while ((dataOfFile = fis.read()) > -1) {
            fos.write(dataOfFile^numOfEncAndDec);
        }

        fis.close();
        fos.flush();
        fos.close();
        return true;
    }

}
