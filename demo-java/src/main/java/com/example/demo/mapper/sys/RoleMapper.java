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
