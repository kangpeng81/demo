/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.Role;
import com.example.demo.service.sys.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/listRole")
    @SaCheckPermission("role:listRole")
    public ResultUtils listRole(@RequestBody(required = false) Role query) {
        PageResult<Role> result = roleService.queryPage(query == null ? new Role() : query);
        return ResultUtils.success("success", result);
    }

    @PostMapping("/addRole")
    @SaCheckPermission("role:addRole")
    public ResultUtils addRole(@RequestBody Role role) {
        Role added = roleService.add(role);
        return ResultUtils.success("添加成功", added);
    }

    @PutMapping("/updateRole")
    @SaCheckPermission("role:updateRole")
    public ResultUtils updateRole(@RequestBody Role role) {
        int rows = roleService.update(role);
        return rows > 0 ? ResultUtils.successMsg("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/deleteRole/{roleId}")
    @SaCheckPermission("role:deleteRole")
    public ResultUtils deleteRole(@PathVariable Long roleId) {
        int rows = roleService.delete(roleId);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }
}
