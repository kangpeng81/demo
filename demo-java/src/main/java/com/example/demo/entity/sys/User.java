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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}