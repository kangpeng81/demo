/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.entity.sys.Dept;

import java.util.List;

public interface DeptService {

    /** 查询全部部门 */
    List<Dept> listDepts();

    /** 新增部门，并初始化该部门的 Casbin member/leader 静态策略 */
    Dept addDept(Dept dept);

    /** 设置部门领导人，并同步 Casbin leader 虚拟角色（旧领导摘除、新领导挂载） */
    void setLeader(Long deptId, Long leaderUserId);

    /** 人员调部门：更新 t_user.dept_id，并同步 Casbin member 虚拟角色域 */
    void transferUser(Long userId, Long deptId);
}
