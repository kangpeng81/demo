/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.entity.sys;

import com.example.demo.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends PageQuery {

    private Long userId;

    private String userName;

    private String password;

    private String salt;

    private String nickname;

    private String email;

    private String phone;

    private Integer age;

    private String name;

    private Integer status;

    /** 省（数据权限维度） */
    private String province;

    /** 市（数据权限维度） */
    private String city;

    /** 区/县（数据权限维度） */
    private String district;

    /** 所属部门（Casbin 文档权限的域载体） */
    private Long deptId;

    /** 人员级别 1-4，决定可访问的最高文档密级（信息性字段） */
    private Integer userLevel;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}