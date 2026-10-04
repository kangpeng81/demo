/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.Role;

public interface RoleService {

    /** 分页查询角色 */
    PageResult<Role> queryPage(Role query);

    Role add(Role role);

    int update(Role role);

    int delete(Long roleId);
}
