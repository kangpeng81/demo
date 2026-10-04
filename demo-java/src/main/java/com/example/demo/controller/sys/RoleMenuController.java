package com.example.demo.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.RoleMenu;
import com.example.demo.service.sys.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/roleMenu")
public class RoleMenuController {

    @Autowired
    private RoleMenuService roleMenuService;

    @PostMapping("/listRoleMenu")
    @SaCheckPermission("roleMenu:listRoleMenu")
    public ResultUtils listRoleMenu(@RequestBody(required = false) RoleMenu query) {
        return ResultUtils.success("success", roleMenuService.list(query));
    }

    @GetMapping("/menuIdsByRoleId/{roleId}")
    public ResultUtils menuIdsByRoleId(@PathVariable Long roleId) {
        List<Long> menuIds = roleMenuService.menuIdsByRoleId(roleId);
        return ResultUtils.success("success", menuIds);
    }

    @PostMapping("/addRoleMenu")
    @SaCheckPermission("roleMenu:addRoleMenu")
    public ResultUtils addRoleMenu(@RequestBody RoleMenu roleMenu) {
        roleMenuService.add(roleMenu);
        return ResultUtils.success("添加成功", roleMenu);
    }

    @DeleteMapping("/deleteRoleMenu/{id}")
    @SaCheckPermission("roleMenu:deleteRoleMenu")
    public ResultUtils deleteRoleMenu(@PathVariable Long id) {
        int rows = roleMenuService.deleteById(id);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }

    @PostMapping("/assignMenus")
    @SaCheckPermission("roleMenu:assignMenus")
    public ResultUtils assignMenus(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> menuIds = (List<Integer>) params.get("menuIds");

        List<Long> ids = menuIds == null ? java.util.Collections.emptyList()
                : menuIds.stream().map(Integer::longValue).collect(java.util.stream.Collectors.toList());
        roleMenuService.assignMenus(roleId, ids);
        return ResultUtils.successMsg("分配成功");
    }
}
