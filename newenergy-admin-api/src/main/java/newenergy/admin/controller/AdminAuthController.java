package newenergy.admin.controller;

import newenergy.core.util.JacksonUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.service.NewenergyPermissionService;
import newenergy.db.service.NewenergyRoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static newenergy.admin.util.AdminResponseCode.ADMIN_INVALID_ACCOUNT;

@RestController
@RequestMapping("/admin/auth")
@Validated
public class AdminAuthController {

    @Autowired
    private NewenergyAdminService adminService;

    @Autowired
    private NewenergyRoleService roleService;

    @Autowired
    private NewenergyPermissionService permissionService;

    @PostMapping("/login")
    public Object login(@RequestBody String body){
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号或密码不正确");
        } catch (LockedAccountException lae) {
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, "用户帐号已锁定不可用");
        } catch (AuthenticationException ae) {
            return ResponseUtil.fail(ADMIN_INVALID_ACCOUNT, ae.getMessage());
        }
        return ResponseUtil.ok(currentUser.getSession().getId());

    }
    @GetMapping("/401")
    public Object page401(){
        return ResponseUtil.unlogin();
    }

    //@RequiresAuthentication
    @GetMapping("/info")
    public Object info(){
        Subject currentUser = SecurityUtils.getSubject();
        NewenergyAdmin admin = (NewenergyAdmin)currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());

        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        data.put("roles", roles);
        data.put("perms", permissions);
        return ResponseUtil.ok(data);
    }

    @GetMapping("/logout")
    public Object logout(){
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return ResponseUtil.ok();
    }
}
