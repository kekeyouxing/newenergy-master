package newenergy.db.service;

import newenergy.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by HUST Corey on 2019-05-27.
 */
@Service
public class BackupService {
//    @Value("${backup.basedir}")
//    String path;
    @Value("${backup.username}")
    String username;
    @Value("${backup.password}")
    String password;
    @Value("${backup.database}")
    String database;
//    @Value("${backup.loaddir}")
//    String loadPath;
    @Value("${backup.loadname}")
    String loadName;
    String cmdWin = "cmd /c ";

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public void saveBackup() throws Exception{
        String filePath = "backup" + TimeUtil.getDateString(TimeUtil.getUTCNow()) + ".sql";
        String cmd = String.format("mysqldump -u%s -p%s %s -r %s --skip-lock-tables",
                username,password,database,filePath);
        //服务器上
        cmd = cmdWin.concat(cmd);

        logger.info(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream is = process.getErrorStream();
        int processComplete = process.waitFor();
        if (processComplete == 0) {
            logger.info("备份成功：" + filePath);
        } else {
            logger.error("备份失败：" + filePath);

            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while(is.available() > 0){
                is.read(buffer);
                sb.append(buffer);
            }
            logger.error("错误输出：" + buffer);

            throw new RuntimeException("备份数据库失败.");
        }
    }
    public void loadBackup() throws Exception{
        String targetFile = loadName;
//        String[] execCMD = new String[]{"mysql", database, "-u" + username, "-p" + password, "-h localhost", "-P 3306", "<", targetFile};
        String cmd = String.format("mysql %s -u%s -p%s -h localhost -P 3306",database,username,password);

        //服务器上
        cmd = cmdWin.concat(cmd);

        logger.info(cmd);
        Process process = Runtime.getRuntime().exec(cmd);
        OutputStream os = process.getOutputStream();
        InputStream is = process.getErrorStream();

        FileInputStream fis = new FileInputStream(targetFile);
        byte[] buffer = new byte[1024];
        while(fis.available() > 0){
            int len = fis.read(buffer);
            os.write(buffer,0,len);
        }
        fis.close();
        os.close();

        int processComplete = process.waitFor();
        if (processComplete == 0) {
            logger.info("还原成功：" + TimeUtil.getString(TimeUtil.getUTCNow()));
        } else {
            logger.error("还原失败：" + TimeUtil.getString(TimeUtil.getUTCNow()));

            byte[] errBuffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            while(is.available() > 0){
                is.read(errBuffer);
                sb.append(errBuffer);
            }
            logger.error("错误输出：" + errBuffer);

            throw new RuntimeException("还原数据库失败.");
        }

    }

}
