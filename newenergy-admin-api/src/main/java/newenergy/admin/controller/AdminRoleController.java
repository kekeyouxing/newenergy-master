package newenergy.admin.controller;

import newenergy.core.util.ResponseUtil;
import newenergy.db.domain.NewenergyRole;
import newenergy.db.service.NewenergyPermissionService;
import newenergy.db.service.NewenergyRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static newenergy.admin.util.AdminResponseCode.ROLE_NAME_EXIST;

@RestController
@RequestMapping("/admin/role")
@Validated
public class AdminRoleController {

    @Autowired
    private NewenergyRoleService roleService;

    @Autowired
    private NewenergyPermissionService permissionService;

    //@RequiresPermissions("admin:role:list")
    //@RequiresPermissionsDesc(menu={"系统管理", "角色管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit){
        Page<NewenergyRole> pageRole = roleService.querySelective(name, page-1, limit);
        List<NewenergyRole> roleList = pageRole.getContent();
        int total = pageRole.getNumberOfElements();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", roleList);

        return ResponseUtil.ok(data);
    }

    //@RequiresPermissions("admin:role:create")
    //@RequiresPermissionsDesc(menu={"系统管理", "角色管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody NewenergyRole role){
        Object error = validate(role);
        if(error != null){
            return error;
        }
        if(roleService.checkExist(role.getName())){
            return ResponseUtil.fail(ROLE_NAME_EXIST, "角色已经存在");
        }
        role.setDeleted(false);
        role.setEnable(true);
        roleService.add(role);
        return ResponseUtil.ok(role);
    }

    //@RequiresPermissions("admin:role:read")
    //@RequiresPermissionsDesc(menu={"系统管理", "角色管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id){
        NewenergyRole role = roleService.read(id);
        return ResponseUtil.ok(role);
    }

    //@RequiresPermissions("admin:role:delete")
    //@RequiresPermissionsDesc(menu = {"系统管理","角色管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody NewenergyRole role){
        Integer id = role.getId();
        if(id == null){
            return ResponseUtil.badArgument();
        }
        roleService.deleteRole(role);
        return ResponseUtil.ok();
    }
    private Object validate(NewenergyRole role){
        String name = role.getName();
        if(StringUtils.isEmpty(name)){
            return ResponseUtil.badArgument();
        }
        return null;
    }
    //@RequiresPermissions("admin:role:list")
    //@RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="查询")
    @GetMapping("/options")
    public Object options(){
        List<NewenergyRole> roleList = roleService.queryAll();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        for(NewenergyRole role: roleList){
            Map<String, Object> option = new HashMap<>();
            option.put("value", role.getId());
            option.put("label",role.getName());
            options.add(option);
        }

        return ResponseUtil.ok(options);
    }

    //@RequiresPermissions("admin:role:update")
    //@RequiresPermissionsDesc(menu={"系统管理" , "角色管理"}, button="编辑")
    @PostMapping("/update")
    public Object update(@RequestBody NewenergyRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        roleService.updateRole(role);
        return ResponseUtil.ok();
    }
}
