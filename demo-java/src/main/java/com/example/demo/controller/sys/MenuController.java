package com.example.demo.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.Menu;
import com.example.demo.service.sys.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/listMenu")
    public ResultUtils listMenu(@RequestBody(required = false) Menu query) {
        List<Menu> list = menuService.queryList(query == null ? new Menu() : query);
        return ResultUtils.success("success", list);
    }

    @PostMapping("/tree")
    @SaCheckPermission("menu:tree")
    public ResultUtils tree(@RequestBody(required = false) Menu query) {
        List<Menu> tree = menuService.queryTree(query == null ? new Menu() : query);
        return ResultUtils.success("success", tree);
    }

    @PostMapping("/addMenu")
    @SaCheckPermission("menu:addMenu")
    public ResultUtils addMenu(@RequestBody Menu menu) {
        Menu added = menuService.add(menu);
        return ResultUtils.success("添加成功", added);
    }

    @PutMapping("/updateMenu")
    @SaCheckPermission("menu:updateMenu")
    public ResultUtils updateMenu(@RequestBody Menu menu) {
        int rows = menuService.update(menu);
        return rows > 0 ? ResultUtils.successMsg("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/deleteMenu/{menuId}")
    @SaCheckPermission("menu:deleteMenu")
    public ResultUtils deleteMenu(@PathVariable Long menuId) {
        int rows = menuService.delete(menuId);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }
}
