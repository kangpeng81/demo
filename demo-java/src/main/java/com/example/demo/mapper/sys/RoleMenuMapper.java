/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    List<RoleMenu> selectList(RoleMenu query);

    /** 分页查询角色菜单关联 */
    List<RoleMenu> selectPage(RoleMenu query);

    /** 按查询条件统计关联总数 */
    long count(RoleMenu query);

    int insert(RoleMenu roleMenu);

    int deleteById(@Param("id") Long id);

    int deleteByRoleId(@Param("roleId") Long roleId);

    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}