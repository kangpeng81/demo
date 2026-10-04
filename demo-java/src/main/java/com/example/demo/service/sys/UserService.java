/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.User;

public interface UserService {

    /** 分页查询用户 */
    PageResult<User> queryUserPage(User query);

    User addUser(User user);

    int updateUser(User user);

    int deleteUser(Long userId);

    User login(String userName, String password);
}
