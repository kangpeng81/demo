/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper {

    /** 分页查询角色 */
    List<Role> selectPage(Role query);

    /** 按查询条件统计角色总数 */
    long count(Role query);

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("roleId") Long roleId);
}
