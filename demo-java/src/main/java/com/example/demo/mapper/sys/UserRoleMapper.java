package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    List<UserRole> selectList(UserRole query);

    /** 分页查询用户角色关联 */
    List<UserRole> selectPage(UserRole query);

    /** 按查询条件统计关联总数 */
    long count(UserRole query);

    int insert(UserRole userRole);

    int deleteById(@Param("id") Long id);

    int deleteByUserId(@Param("userId") Long userId);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /** 查询用户拥有的所有按钮权限点（t_menu type=3 行的 path，按分号展开） */
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}