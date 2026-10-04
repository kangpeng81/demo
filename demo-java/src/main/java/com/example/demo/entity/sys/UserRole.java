package com.example.demo.entity.sys;

import com.example.demo.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserRole extends PageQuery {

    private Long id;

    private Long userId;

    private Long roleId;

    /** 用户名（JOIN t_user 查询时填充，非表字段） */
    private String userName;

    /** 角色名（JOIN t_role 查询时填充，非表字段） */
    private String roleName;
}
