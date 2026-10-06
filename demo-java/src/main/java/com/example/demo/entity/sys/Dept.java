/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.entity.sys;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门表实体：Casbin 文档权限的「域」（dom）载体，含部门领导人。
 */
@Data
public class Dept {

    private Long deptId;

    private String deptName;

    /** 上级部门ID，0为根 */
    private Long parentId;

    /** 部门领导人 user_id */
    private Long leaderUserId;

    /** 领导人姓名（联查展示用） */
    private String leaderName;

    private String province;

    private String city;

    private String district;

    private Integer status;

    private LocalDateTime createTime;
}
