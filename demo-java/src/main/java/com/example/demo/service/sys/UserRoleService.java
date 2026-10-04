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
