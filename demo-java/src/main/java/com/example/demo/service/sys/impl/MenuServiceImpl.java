package com.example.demo.service.sys.impl;

import com.example.demo.entity.sys.Menu;
import com.example.demo.entity.sys.MenuVO;
import com.example.demo.mapper.sys.MenuMapper;
import com.example.demo.service.sys.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> queryList(Menu query) {
        return menuMapper.selectList(query);
    }

    @Override
    public List<Menu> queryTree(Menu query) {
        List<Menu> all = menuMapper.selectList(query == null ? new Menu() : query);
        return buildMenuTree(all);
    }

    @Override
    public List<MenuVO> queryTreeByUserId(Long userId) {
        // 1. 查扁平列表（已按 parent_id, sort 排序）
        List<Menu> all = menuMapper.selectByUserId(userId);
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }
        // 2. 构造 Element Plus 格式树
        return buildVOTree(all);
    }

    @Override
    public Menu add(Menu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getType() == null) {
            menu.setType(2);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    public int update(Menu menu) {
        return menuMapper.update(menu);
    }

    @Override
    public int delete(Long menuId) {
        return menuMapper.deleteById(menuId);
    }

    /**
     * 把扁平菜单列表组装成 Menu 父子树（用于 /sys/menu/tree 管理界面）
     */
    private List<Menu> buildMenuTree(List<Menu> all) {
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Menu> map = all.stream()
                .collect(Collectors.toMap(Menu::getMenuId, m -> m));

        List<Menu> roots = new ArrayList<>();
        for (Menu menu : all) {
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                roots.add(menu);
            } else {
                Menu parent = map.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                } else {
                    roots.add(menu);
                }
            }
        }
        return roots;
    }

    /**
     * 构造 Element Plus 格式的菜单树
     * - index: 根节点用 menuId；子节点用 "父index-序号"（如 "2-1"）
     * - type:  有 children 的目录节点设为 'submenu'，菜单节点不设（序列化为 null 不输出）
     * - 无 children 的节点不输出 children 字段
     */
    private List<MenuVO> buildVOTree(List<Menu> all) {
        if (all == null || all.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, MenuVO> map = all.stream()
                .collect(Collectors.toMap(Menu::getMenuId, this::toVO));

        List<MenuVO> roots = new ArrayList<>();
        for (Menu menu : all) {
            MenuVO vo = map.get(menu.getMenuId());
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                // 根节点 index = menuId
                vo.setIndex(String.valueOf(menu.getMenuId()));
                roots.add(vo);
            } else {
                MenuVO parent = map.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    // 子节点 index = 父index-序号
                    int childSeq = parent.getChildren().size() + 1;
                    vo.setIndex(parent.getIndex() + "-" + childSeq);
                    parent.getChildren().add(vo);
                } else {
                    // 父不在结果集中，作为根节点
                    vo.setIndex(String.valueOf(menu.getMenuId()));
                    roots.add(vo);
                }
            }
        }

        // 给有 children 的节点设置 type='submenu'
        for (MenuVO root : roots) {
            markSubmenu(root);
        }
        return roots;
    }

    private void markSubmenu(MenuVO vo) {
        if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
            vo.setType("submenu");
            for (MenuVO child : vo.getChildren()) {
                markSubmenu(child);
            }
        }
        // 无 children 的叶子节点：type 为 null，序列化时不输出
    }

    /** Menu → MenuVO 基础字段转换（不含 index/children/type，由 buildVOTree 填充） */
    private MenuVO toVO(Menu menu) {
        MenuVO vo = new MenuVO();
        vo.setPath(menu.getPath());
        vo.setTitle(menu.getTitle());
        vo.setIcon(menu.getIcon());
        return vo;
    }
}
