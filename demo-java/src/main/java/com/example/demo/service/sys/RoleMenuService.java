package com.example.demo.service.sys;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.RoleMenu;

import java.util.List;

public interface RoleMenuService {

    /** 分页查询角色菜单关联 */
    PageResult<RoleMenu> list(RoleMenu query);

    List<Long> menuIdsByRoleId(Long roleId);

    int add(RoleMenu roleMenu);

    int deleteById(Long id);

    void assignMenus(Long roleId, List<Long> menuIds);
}
