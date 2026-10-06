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

/**
 * 文档表实体，同时兼作列表查询参数（继承分页与区域过滤字段）。
 *
 * <p>Casbin 控制：主体 × 资源类型(obj) × 操作 × 密级 × 角色 × 部门（与省市区无关）。</p>
 *
 * <p>密级：1-公开 2-部门 3-部门机密 4-区域；资源类型 obj：doc-文档 contract-合同（可扩展）。</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Doc extends PageQuery {

    private Long docId;

    private String title;

    private String content;

    /** 资源类型：doc-文档 contract-合同（Casbin obj 维度，可扩展新资源类型） */
    private String obj;

    /** 密级 1-公开 2-部门 3-部门机密 4-区域 */
    private Integer docLevel;

    /** 归属部门ID（Casbin 域） */
    private Long deptId;

    /** 创建人ID */
    private Long creatorId;

    /** 省（区域权限维度，继承创建人） */
    private String province;

    /** 市（区域权限维度，继承创建人） */
    private String city;

    /** 区/县（区域权限维度，继承创建人） */
    private String district;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
