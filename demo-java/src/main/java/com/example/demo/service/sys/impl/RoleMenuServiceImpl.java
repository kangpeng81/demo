package com.example.demo.service.sys.impl;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.RoleMenu;
import com.example.demo.mapper.sys.RoleMenuMapper;
import com.example.demo.service.sys.RoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public PageResult<RoleMenu> list(RoleMenu query) {
        RoleMenu q = query == null ? new RoleMenu() : query;
        long total = roleMenuMapper.count(q);
        return PageResult.of(total, q, roleMenuMapper.selectPage(q));
    }

    @Override
    public List<Long> menuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public int add(RoleMenu roleMenu) {
        return roleMenuMapper.insert(roleMenu);
    }

    @Override
    public int deleteById(Long id) {
        return roleMenuMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
    }
}