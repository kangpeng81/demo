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