package com.example.demo.service.sys.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.User;
import com.example.demo.entity.sys.UserRole;
import com.example.demo.mapper.sys.UserMapper;
import com.example.demo.mapper.sys.UserRoleMapper;
import com.example.demo.service.sys.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public PageResult<User> queryUserPage(User query) {
        long total = userMapper.countUser(query);
        List<User> list = userMapper.queryUserPage(query);
        list.forEach(u -> u.setPassword(null));
        return PageResult.of(total, query, list);
    }

    @Override
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.addUser(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public int updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userMapper.updatePasswordByUsername(user);
        }
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUser(Long userId) {
        // 删除前检查：用户已分配角色，禁止删除，避免脏数据
        UserRole userRoleQuery = new UserRole();
        userRoleQuery.setUserId(userId);
        long roleRefCount = userRoleMapper.count(userRoleQuery);
        if (roleRefCount > 0) {
            throw new RuntimeException("该用户已分配" + roleRefCount + "个角色，请先解除角色关联后再删除");
        }
        return userMapper.deleteById(userId);
    }

    @Override
    public User login(String userName, String password) {
        User user = userMapper.findByUsername(userName);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        StpUtil.login(user.getUserId());

        user.setPassword(null);
        return user;
    }
}
