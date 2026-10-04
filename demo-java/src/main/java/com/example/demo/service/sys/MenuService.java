/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.entity.sys.Menu;
import com.example.demo.entity.sys.MenuVO;

import java.util.List;

public interface MenuService {

    List<Menu> queryList(Menu query);

    List<Menu> queryTree(Menu query);

    /** 根据当前登录用户ID查询菜单树（Element Plus 格式） */
    List<MenuVO> queryTreeByUserId(Long userId);

    Menu add(Menu menu);

    int update(Menu menu);

    int delete(Long menuId);
}
