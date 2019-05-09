package newenergy.admin.background.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by HUST Corey on 2019-04-17.
 */
@Service
public class ScheduledService {
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        return new ThreadPoolTaskScheduler();
    }
    public ScheduledFuture<?> startCron(Runnable runnable,String cron) {
        return threadPoolTaskScheduler.schedule(runnable, new CronTrigger(cron));
    }

    public boolean stopCron(ScheduledFuture<?> future) {
        if (future != null) {
            future.cancel(true);
        }
        return future!=null;
    }

    public ScheduledFuture<?> changeCron(ScheduledFuture<?> future,Runnable runnable,String cron) {
        stopCron(future);// 先停止，在开启.
        return threadPoolTaskScheduler.schedule(runnable, new CronTrigger(cron));
    }

    /**
     *  hour minute second 三选一
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public String getCronByRate(Integer hour, Integer minute, Integer second){
        String baseStr = " * * ?";
        String prefix = "* * *";
        if(hour != null){
            prefix = String.format("0 0 0/%d",hour);
        }else if(minute != null){
            prefix = String.format("0 0/%d *",minute);
        }else if(second != null){
            prefix = String.format("0/%d * *",second);
        }
        return prefix.concat(baseStr);
    }
}
