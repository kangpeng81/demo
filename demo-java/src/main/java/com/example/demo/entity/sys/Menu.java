/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.entity.sys;

import lombok.Data;

import java.util.List;

/**
 * 菜单表（来源 Element Plus 菜单，父子同表）
 */
@Data
public class Menu {

    private Long menuId;

    /** 父菜单ID，0表示根节点 */
    private Long parentId;

    /** 路由路径（Element Plus menu.path） */
    private String path;

    /** 菜单标题 */
    private String title;

    /** 菜单图标（Element Plus 组件名，如 Monitor） */
    private String icon;

    /** 类型 1-目录 2-菜单 3-按钮 */
    private Integer type;

    /** 排序值 */
    private Integer sort;

    /** 状态 1-显示 0-隐藏 */
    private Integer status;

    /** Element Plus 风格 index，如 "1"、"2-1"，用于前端渲染 */
    private String index;

    /** 子菜单 */
    private List<Menu> children;
}
