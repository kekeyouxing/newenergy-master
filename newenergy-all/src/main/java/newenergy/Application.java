package newenergy;

import io.netty.channel.ChannelFuture;
import newenergy.admin.background.communicate.NettyServer;
import newenergy.admin.background.service.DeviceRequireService;
import newenergy.db.constant.AdminConstant;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.service.NewenergyRoleService;
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
import java.util.List;

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
    @Autowired
    private NewenergyRoleService newenergyRoleService;
    @Autowired
    private NewenergyAdminService newenergyAdminService;

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

        log.info("添加用户角色>>>>>>");
        addRoles();

        log.info("添加超级管理员>>>>>");
        addAdmin();


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
    private void addRoles(){
        NewenergyRole role;
        if(newenergyRoleService.findById(AdminConstant.ROLE_ADMIN) == null) {
            role = new NewenergyRole();
            role.setId(1);
            role.setDescription("所有模块的权限");
            role.setName("超级管理员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if(newenergyRoleService.findById(AdminConstant.ROLE_INPUT) == null) {
            role = new NewenergyRole();
            role.setId(2);
            role.setDescription("负责录入用户基本信息、小区信息、设备类型信息、泵房信息");
            role.setName("数据录入人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if(newenergyRoleService.findById(AdminConstant.ROLE_RECHARGE) == null) {
            role = new NewenergyRole();
            role.setId(3);
            role.setDescription("批量充值功能(非移动充值)、修改充值系数、发起退款请求");
            role.setName("财务充值人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_CHECK) == null) {
            role = new NewenergyRole();
            role.setId(4);
            role.setDescription("对财务充值人员的批量充值、退款请求进行审核，对运维人员修改的充值系数进行审核");
            role.setName("财务审核人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_MANAGER) == null) {
            role = new NewenergyRole();
            role.setId(5);
            role.setDescription("管理以上人员的身份和密码；输出各种报表");
            role.setName("公司管理人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_AUDIT) == null) {
            role = new NewenergyRole();
            role.setId(6);
            role.setDescription("对公司的所有报表可以查看");
            role.setName("审计人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_BACKUP) == null) {
            role = new NewenergyRole();
            role.setId(7);
            role.setDescription("每天对系统的数据做好备份工作");
            role.setName("备份管理人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_MONITOR) == null) {
            role = new NewenergyRole();
            role.setId(8);
            role.setDescription("查看各小区水量消耗数据；修改充值系数；接收机器故障信息；可手动添加故障记录；超时无响应向故障处理领导上诉");
            role.setName("运维人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_FAULTLEADER) == null) {
            role = new NewenergyRole();
            role.setId(9);
            role.setDescription("接收机器故障信息和故障处理人员的故障处理流程, 监督故障处理人员处理过程");
            role.setName("故障领导");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }

        if (newenergyRoleService.findById(AdminConstant.ROLE_SERVICER) == null) {
            role = new NewenergyRole();
            role.setId(10);
            role.setDescription("响应运营维护管理人员调度（收到信息后按一个确认键表示已响应）；对机器故障进行维护和处理,处理完成之后，还需在微信中反馈处理结果（已处理，未处理已上报）");
            role.setName("故障处理人员");
            role.setEnable(true);
            role.setDeleted(false);
            newenergyRoleService.add(role);
        }


    }
    private void addAdmin(){
        List<NewenergyAdmin> res = newenergyAdminService.findAdmin("admin123");
        if(res==null || res.isEmpty()){
            NewenergyAdmin admin = new NewenergyAdmin();
            admin.setUsername("admin123");
            admin.setPassword("admin123admin123");
            admin.setRoleIds(new Integer[]{AdminConstant.ROLE_ADMIN});
            newenergyAdminService.add(admin,null);
        }

    }

}
