package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.service.NewenergyAdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static newenergy.admin.util.AdminResponseCode.ADMIN_INVALID_ACCOUNT;

@RestController
@RequestMapping("/admin/password")
public class AdminPasswordController {
    @Autowired
    private NewenergyAdminService adminService;

    //@RequiresAuthentication
    @PostMapping("/password")
    public Object create(@AdminLoginUser NewenergyAdmin adminLogin, @RequestBody String body){
        String oldPassword = JacksonUtil.parseString(body, "oldPassword");
        String newPassword = JacksonUtil.parseString(body, "newPassword");

        if (StringUtils.isEmpty(oldPassword)) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(newPassword)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        NewenergyAdmin admin = (NewenergyAdmin) currentUser.getPrincipal();

        if (!oldPassword.equals(admin.getPassword())) {
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "原密码错误");
        }

        admin.setPassword(newPassword);

        adminService.updateById(admin, adminLogin.getId());
        return ResponseUtil.ok();
    }
}
