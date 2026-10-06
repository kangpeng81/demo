/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.controller.sys;

import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.Dept;
import com.example.demo.service.casbin.CasbinPolicyService;
import com.example.demo.service.sys.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门接口：部门是 Casbin 文档权限的「域」。
 * 新建部门自动初始化 member/leader 静态策略；换领导/调部门自动同步角色挂载。
 */
@RestController
@RequestMapping("/sys/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @Autowired
    private CasbinPolicyService casbinPolicyService;

    /** 部门列表（含领导人姓名） */
    @GetMapping("/list")
    public ResultUtils list() {
        List<Dept> list = deptService.listDepts();
        return ResultUtils.success("success", list);
    }

    /** 新增部门（自动初始化该部门的 member/leader 静态策略） */
    @PostMapping("/add")
    public ResultUtils add(@RequestBody Dept dept) {
        return ResultUtils.success("创建成功", deptService.addDept(dept));
    }

    /** 设置部门领导人（Casbin leader 角色自动交接） */
    @PutMapping("/setLeader/{deptId}/{userId}")
    public ResultUtils setLeader(@PathVariable Long deptId, @PathVariable Long userId) {
        deptService.setLeader(deptId, userId);
        return ResultUtils.successMsg("设置成功");
    }

    /** 人员调部门（Casbin member 角色域自动迁移） */
    @PutMapping("/transferUser/{userId}/{deptId}")
    public ResultUtils transferUser(@PathVariable Long userId, @PathVariable Long deptId) {
        deptService.transferUser(userId, deptId);
        return ResultUtils.successMsg("调动成功");
    }
}
