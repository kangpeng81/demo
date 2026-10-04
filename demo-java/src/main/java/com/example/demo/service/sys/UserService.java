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
