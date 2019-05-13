package newenergy;

import io.netty.channel.ChannelFuture;
import newenergy.admin.background.communicate.NettyServer;
import newenergy.admin.background.service.DeviceRequireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {
    @Value("${netty.port}")
    private int port;
    @Value("${netty.url}")
    private String url;
    @Autowired
    private NettyServer nettyServer;
    @Autowired
    private DeviceRequireService deviceRequireService;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    public static void main(String[] args){
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        /**
         * 开始执行实时需水量计算定时任务
         */
        log.info("实施需水量计算定时任务启动>>>>>");
        deviceRequireService.updateCron();

        InetSocketAddress address = new InetSocketAddress(url,port);
        ChannelFuture future = nettyServer.run(address);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                nettyServer.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();



    }


}
