package newenergy.db.service;

import newenergy.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    @Value("${backup.loaddir}")
    String loadPath;
    @Value("${backup.loadname}")
    String loadName;
    String cmdMac = "sh -c";
    String cmdWin = "cmd /c";

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public void saveBackup() throws Exception{
        String filePath = path + "backup" + TimeUtil.getDateString(TimeUtil.getUTCNow()) + ".sql";
//        String[] execCMD = new String[] {"mysqldump", "-u" + username, "-p" + password, database,
//                "-r" + filePath, "--skip-lock-tables"};
        String cmd = String.format("mysqldump -u%s -p%s %s -r %s --skip-lock-tables",
                username,password,database,filePath);
        Process process = Runtime.getRuntime().exec( cmd);
        int processComplete = process.waitFor();
        if (processComplete == 0) {
            logger.info("备份成功：" + filePath);
        } else {
            logger.error("备份失败：" + filePath);
            throw new RuntimeException("备份数据库失败.");
        }
    }
    public void loadBackup() throws Exception{
        String targetFile =  loadPath+loadName;
//        String[] execCMD = new String[]{"mysql", database, "-u" + username, "-p" + password, "-h localhost", "-P 3306", "<", targetFile};
        String cmd = String.format("%s mysql %s -u%s -p%s -h localhost -P 3306 < %s",cmdMac,database,username,password,targetFile);
        Process process = Runtime.getRuntime().exec(cmd);
//        Process process = Runtime.getRuntime().exec("sh -c" + execCMD);

        int processComplete = process.waitFor();
        if (processComplete == 0) {
            logger.info("还原成功：" + TimeUtil.getString(TimeUtil.getUTCNow()));
        } else {
            logger.error("还原失败：" + TimeUtil.getString(TimeUtil.getUTCNow()));
            throw new RuntimeException("还原数据库失败.");
        }

    }

}
