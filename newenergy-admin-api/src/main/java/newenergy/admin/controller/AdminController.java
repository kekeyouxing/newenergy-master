package newenergy.admin.controller;

import newenergy.admin.annotation.RequiresPermissionsDesc;
import newenergy.core.util.RegexUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.core.util.bcrypt.BCryptPasswordEncoder;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.service.NewenergyAdminService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static newenergy.admin.util.AdminResponseCode.*;
@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminController {
    @Autowired
    private NewenergyAdminService adminService;

    //@RequiresPermissions("admin:admin:list")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String username,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
        Page<NewenergyAdmin> pageAdmin = adminService.querySelective(username, page-1, limit);
        List<NewenergyAdmin> adminList = pageAdmin.getContent();
        int total = pageAdmin.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adminList);

        return ResponseUtil.ok(data);
    }

    //@RequiresPermissions("admin:admin:create")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@RequestBody NewenergyAdmin admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        String username = admin.getUsername();
        List<NewenergyAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(ADMIN_NAME_EXIST, "管理员已经存在");
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);

        adminService.add(admin);
        return ResponseUtil.ok(admin);

    }

    //@RequiresPermissions("admin:admin:read")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {

        NewenergyAdmin admin = adminService.findById(id);
        return ResponseUtil.ok(admin);

    }

    private Object validate(NewenergyAdmin admin) {
        String name = admin.getUsername();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isUsername(name)) {
            return ResponseUtil.fail(ADMIN_INVALID_NAME, "管理员名称不符合规定");
        }
        String password = admin.getPassword();
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            return ResponseUtil.fail(ADMIN_INVALID_PASSWORD, "管理员密码长度不能小于6");
        }
        return null;
    }

    //@RequiresPermissions("admin:admin:update")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody NewenergyAdmin admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);

        if (adminService.updateById(admin) == null) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(admin);
    }
    //@RequiresPermissions("admin:admin:delete")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody NewenergyAdmin admin) {
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        adminService.deleteById(anotherAdminId);
        return ResponseUtil.ok();
    }
}
