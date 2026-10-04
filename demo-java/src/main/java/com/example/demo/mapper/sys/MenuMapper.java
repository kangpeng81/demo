package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<Menu> selectList(Menu query);

    /** 根据用户ID查询其有权限的所有菜单（扁平列表，含按钮） */
    List<Menu> selectByUserId(@Param("userId") Long userId);

    int insert(Menu menu);

    int update(Menu menu);

    int deleteById(@Param("menuId") Long menuId);
}
