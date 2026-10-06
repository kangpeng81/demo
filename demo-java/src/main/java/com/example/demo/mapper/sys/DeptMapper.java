/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeptMapper {

    /** 查询全部部门（联查领导人姓名） */
    List<Dept> selectAll();

    /** 按ID查询部门 */
    Dept findDeptById(@Param("deptId") Long deptId);

    /** 新增部门 */
    int insertDept(Dept dept);

    /** 更新部门领导人 */
    int updateLeader(@Param("deptId") Long deptId, @Param("leaderUserId") Long leaderUserId);

    /** 查询用户所属部门ID（t_user.dept_id，无部门返回 null） */
    Long selectDeptIdByUserId(@Param("userId") Long userId);

    /** 更新用户所属部门（人员调部门） */
    int updateUserDept(@Param("userId") Long userId, @Param("deptId") Long deptId);
}
