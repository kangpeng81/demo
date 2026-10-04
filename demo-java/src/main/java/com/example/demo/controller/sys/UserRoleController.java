package com.example.demo.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.UserRole;
import com.example.demo.service.sys.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @PostMapping("/listUserRole")
    @SaCheckPermission("userRole:listUserRole")
    public ResultUtils listUserRole(@RequestBody(required = false) UserRole query) {
        return ResultUtils.success("success", userRoleService.list(query));
    }

    @GetMapping("/roleIdsByUserId/{userId}")
    public ResultUtils roleIdsByUserId(@PathVariable Long userId) {
        List<Long> roleIds = userRoleService.roleIdsByUserId(userId);
        return ResultUtils.success("success", roleIds);
    }

    @PostMapping("/addUserRole")
    @SaCheckPermission("userRole:addUserRole")
    public ResultUtils addUserRole(@RequestBody UserRole userRole) {
        userRoleService.add(userRole);
        return ResultUtils.success("添加成功", userRole);
    }

    @DeleteMapping("/deleteUserRole/{id}")
    @SaCheckPermission("userRole:deleteUserRole")
    public ResultUtils deleteUserRole(@PathVariable Long id) {
        int rows = userRoleService.deleteById(id);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }

    @PostMapping("/assignRoles")
    @SaCheckPermission("userRole:assignRoles")
    public ResultUtils assignRoles(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) params.get("roleIds");

        List<Long> ids = roleIds == null ? java.util.Collections.emptyList()
                : roleIds.stream().map(Integer::longValue).collect(java.util.stream.Collectors.toList());
        userRoleService.assignRoles(userId, ids);
        return ResultUtils.successMsg("分配成功");
    }
}
