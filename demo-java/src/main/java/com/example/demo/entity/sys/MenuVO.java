package com.example.demo.entity.sys;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 菜单树节点 VO（Element Plus 前端格式）
 * - index: 前端 el-menu 的 index 属性
 * - type:  'submenu' 表示目录（有 children），菜单/按钮不输出此字段
 * - icon:  Element Plus 图标组件名字符串（如 "Monitor"）
 * 使用 NON_NULL：null 字段不序列化，确保前端收到的 JSON 干净
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuVO {

    private String index;

    private String path;

    private String title;

    private String icon;

    /** 'submenu' 表示目录节点，菜单节点不输出此字段 */
    private String type;

    private List<MenuVO> children;
}
