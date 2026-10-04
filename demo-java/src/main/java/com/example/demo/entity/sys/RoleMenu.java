package com.example.demo.entity.sys;

import com.example.demo.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleMenu extends PageQuery {

    private Long id;

    private Long roleId;

    private Long menuId;

    /** 角色名（JOIN t_role 查询时填充，非表字段） */
    private String roleName;

    /** 菜单名（JOIN t_menu.title 查询时填充，非表字段） */
    private String menuName;
}
