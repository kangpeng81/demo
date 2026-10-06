/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys.impl;

import com.example.demo.entity.sys.Dept;
import com.example.demo.mapper.sys.DeptMapper;
import com.example.demo.service.casbin.CasbinPolicyService;
import com.example.demo.service.sys.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private CasbinPolicyService casbinPolicyService;

    @Override
    public List<Dept> listDepts() {
        return deptMapper.selectAll();
    }

    @Override
    public Dept addDept(Dept dept) {
        if (dept.getDeptName() == null || dept.getDeptName().isEmpty()) {
            throw new RuntimeException("部门名称不能为空");
        }
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }
        deptMapper.insertDept(dept);
        // 哨兵策略行(creator/leader/member)为全局定义，新部门无需额外初始化；
        // 部门权限通过 g 角色挂载(leader:dept:x / member:dept:x)在人员任命时同步
        return dept;
    }

    @Override
    public void setLeader(Long deptId, Long leaderUserId) {
        Dept dept = deptMapper.findDeptById(deptId);
        if (dept == null) {
            throw new RuntimeException("部门不存在");
        }
        Long oldLeaderId = dept.getLeaderUserId();
        deptMapper.updateLeader(deptId, leaderUserId);
        // Casbin：旧领导摘除 leader 角色，新领导挂载（权限自动交接）
        casbinPolicyService.syncDeptLeader(deptId, oldLeaderId, leaderUserId);
    }

    @Override
    public void transferUser(Long userId, Long deptId) {
        if (deptMapper.findDeptById(deptId) == null) {
            throw new RuntimeException("目标部门不存在");
        }
        Long oldDeptId = deptMapper.selectDeptIdByUserId(userId);
        deptMapper.updateUserDept(userId, deptId);
        // Casbin：member 虚拟角色从旧部门域摘除、挂到新部门域
        casbinPolicyService.syncUserDept(userId, oldDeptId, deptId);
    }
}
