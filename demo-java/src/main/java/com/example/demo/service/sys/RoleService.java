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
