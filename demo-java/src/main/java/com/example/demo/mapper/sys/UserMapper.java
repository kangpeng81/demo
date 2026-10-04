/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /** 分页查询用户 */
    List<User> queryUserPage(User query);

    /** 按查询条件统计用户总数 */
    long countUser(User query);

    User findByUsername(@Param("userName") String userName);

    int addUser(User user);

    int updatePasswordByUsername(User user);

    int updateUser(User user);

    int deleteById(@Param("userId") Long userId);
}
