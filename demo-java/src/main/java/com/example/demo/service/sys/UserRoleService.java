/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.UserRole;

import java.util.List;

public interface UserRoleService {

    /** 分页查询用户角色关联 */
    PageResult<UserRole> list(UserRole query);

    List<Long> roleIdsByUserId(Long userId);

    int add(UserRole userRole);

    int deleteById(Long id);

    void assignRoles(Long userId, List<Long> roleIds);
}
