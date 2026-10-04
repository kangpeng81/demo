/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys.impl;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.UserRole;
import com.example.demo.mapper.sys.UserRoleMapper;
import com.example.demo.service.sys.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public PageResult<UserRole> list(UserRole query) {
        UserRole q = query == null ? new UserRole() : query;
        long total = userRoleMapper.count(q);
        return PageResult.of(total, q, userRoleMapper.selectPage(q));
    }

    @Override
    public List<Long> roleIdsByUserId(Long userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public int add(UserRole userRole) {
        return userRoleMapper.insert(userRole);
    }

    @Override
    public int deleteById(Long id) {
        return userRoleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole ur = new UserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }
}