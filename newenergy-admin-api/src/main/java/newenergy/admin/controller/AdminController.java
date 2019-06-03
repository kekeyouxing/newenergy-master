package newenergy.admin.controller;

import newenergy.admin.annotation.AdminLoginUser;
import newenergy.core.util.RegexUtil;
import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyAdmin;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.service.NewenergyAdminService;
import newenergy.db.service.NewenergyRoleService;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;

import static newenergy.admin.util.AdminResponseCode.*;
@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminController {
    @Autowired
    private NewenergyAdminService adminService;

    @Autowired
    private NewenergyRoleService roleService;

    //@RequiresPermissions("admin:admin:list")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="查询")
    @GetMapping("/list")
    public Object list(String username,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){

        List<NewenergyAdmin> adminList = adminService.querySelective(username);

        adminList = removeSuperRole(adminList);
        adminList = roleSort(adminList);
        Integer total = adminList.size();
        adminList = page(page, limit, adminList);


        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adminList);

        return ResponseUtil.ok(data);
    }
    private List<NewenergyAdmin> page(Integer page, Integer limit, List<NewenergyAdmin> adminList){
        List<NewenergyAdmin> result = new ArrayList<>();
        int totalPage = (adminList.size()-1)/limit+1;
        if(page<1){
            page = 1;
        }
        if(page>totalPage){
            page = totalPage;
        }
        int pageStart = (page-1)*limit;
        int pageEnd = -1;
        if(pageStart+limit<=adminList.size()){
            pageEnd = pageStart+limit;
        }else{
            pageEnd = adminList.size();
        }
        for(int i = pageStart; i<pageEnd; i++){
            result.add(adminList.get(i));
        }
        return result;
    }
    private List<NewenergyAdmin> removeSuperRole(List<NewenergyAdmin> adminList){
        List<NewenergyAdmin> result = new ArrayList<>();
        NewenergyRole superRole = roleService.findSuperAdmin();
        int superRoleId = -1;
        if(superRole != null){
            superRoleId = superRole.getId();
        }
        for(NewenergyAdmin admin : adminList){
            List<Integer> roleIds = Arrays.asList(admin.getRoleIds());
            if(!roleIds.contains(superRoleId)){
                result.add(admin);
            }
        }
        return result;
    }
    private List<NewenergyAdmin> roleSort(List<NewenergyAdmin> adminList){
        List<NewenergyAdmin> result = new ArrayList<>();
        if(adminList == null || adminList.size() == 0){
            return result;
        }
        Map<Integer, List<NewenergyAdmin>> maps = new HashMap<>();
        for(NewenergyAdmin admin : adminList){
            Integer roleId = admin.getRoleIds()[0];
            List<NewenergyAdmin> list = maps.get(roleId);
            if(list == null){
                list = new ArrayList<NewenergyAdmin>();
            }
            list.add(admin);
            maps.put(roleId,  list);
        }
        for (List<NewenergyAdmin> admins: maps.values()) {
            for(NewenergyAdmin admin : admins){
                result.add(admin);
            }
        }
        return result;
    }

    //@RequiresPermissions("admin:admin:create")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="添加")
    @PostMapping("/create")
    public Object create(@AdminLoginUser NewenergyAdmin adminLogin, @RequestBody NewenergyAdmin admin) {
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

        admin.setPassword(rawPassword);

        adminService.add(admin, adminLogin.getId());
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
            return ResponseUtil.fail(ADMIN_INVALID_NAME, "管理员名称不符合规定\n长度不小于6\n字符范围 a-z A-Z 0-9 _ 中文");
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
    public Object update(@AdminLoginUser NewenergyAdmin adminLogin, @RequestBody NewenergyAdmin admin) {
        Object error = validate(admin);
        if (error != null) {
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        String rawPassword = admin.getPassword();

        admin.setPassword(rawPassword);

        if (adminService.updateById(admin, adminLogin.getId()) == null) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(admin);
    }

    //@RequiresPermissions("admin:admin:delete")
    //@RequiresPermissionsDesc(menu={"系统管理" , "管理员管理"}, button="删除")
    @GetMapping("/delete")
    public Object delete(@AdminLoginUser NewenergyAdmin adminLogin,@RequestParam Integer id) {

        if (id == null) {
            return ResponseUtil.badArgument();
        }

        adminService.deleteById(id, adminLogin.getId());
        return ResponseUtil.ok();
    }
}
