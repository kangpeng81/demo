/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys.impl;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.Role;
import com.example.demo.entity.sys.RoleMenu;
import com.example.demo.entity.sys.UserRole;
import com.example.demo.mapper.sys.RoleMapper;
import com.example.demo.mapper.sys.RoleMenuMapper;
import com.example.demo.mapper.sys.UserRoleMapper;
import com.example.demo.service.sys.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageResult<Role> queryPage(Role query) {
        long total = roleMapper.count(query);
        return PageResult.of(total, query, roleMapper.selectPage(query));
    }

    @Override
    public Role add(Role role) {
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        roleMapper.insert(role);
        return role;
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }

    @Override
    public int delete(Long roleId) {
        // 删除前检查：角色被用户引用 / 角色已分配菜单，均禁止删除，避免脏数据
        UserRole userRoleQuery = new UserRole();
        userRoleQuery.setRoleId(roleId);
        long userRefCount = userRoleMapper.count(userRoleQuery);
        if (userRefCount > 0) {
            throw new RuntimeException("该角色已分配给" + userRefCount + "个用户，请先解除用户关联后再删除");
        }
        RoleMenu roleMenuQuery = new RoleMenu();
        roleMenuQuery.setRoleId(roleId);
        long menuRefCount = roleMenuMapper.count(roleMenuQuery);
        if (menuRefCount > 0) {
            throw new RuntimeException("该角色已分配" + menuRefCount + "个菜单权限，请先解除菜单关联后再删除");
        }
        return roleMapper.deleteById(roleId);
    }
}
