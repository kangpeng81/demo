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
