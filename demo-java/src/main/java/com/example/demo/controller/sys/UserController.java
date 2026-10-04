package com.example.demo.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultUtils;
import com.example.demo.config.SaTokenConfig;
import com.example.demo.entity.sys.MenuVO;
import com.example.demo.entity.sys.User;
import com.example.demo.service.sys.MenuService;
import com.example.demo.service.sys.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private SaTokenConfig saTokenConfig;

    @PostMapping("/login")
    public ResultUtils login(@RequestBody User entity) {
        User user = userService.login(entity.getUserName(), entity.getPassword());

        return ResultUtils.success("登录成功", null)
                .put("token", StpUtil.getTokenValue())
                .put("tokenName", StpUtil.getTokenName())
                .put("user", user);
    }

    @GetMapping("/logout")
    public ResultUtils logout() {
        // 必须在 StpUtil.logout() 之前取 loginId/loginType，logout 后会话销毁再取会抛 NotLoginException
        Object loginId = StpUtil.getLoginId();
        String loginType = StpUtil.getLoginType();
        // 1. 清除 sa:permissions / sa:roles / sa:perms 三类缓存（StpUtil.logout 不会清这些 key）
        saTokenConfig.clearUserCache(loginType, loginId);
        // 2. 清除 sa-token 内置 token/session
        StpUtil.logout();
        return ResultUtils.successMsg("退出成功");
    }

    @PostMapping("/queryUser")
    @SaCheckPermission("user:query")
    public ResultUtils queryUser(@RequestBody(required = false) User query) {
        PageResult<User> result = userService.queryUserPage(query == null ? new User() : query);
        return ResultUtils.success("success", result);
    }

    @PostMapping("/addUser")
    @SaCheckPermission("user:addUser")
    public ResultUtils addUser(@RequestBody User user) {
        User added = userService.addUser(user);
        return ResultUtils.success("添加成功", added);
    }

    @PutMapping("/updateUser")
    @SaCheckPermission("user:updateUser")
    public ResultUtils updateUser(@RequestBody User user) {
        int rows = userService.updateUser(user);
        return rows > 0 ? ResultUtils.successMsg("修改成功") : ResultUtils.error("修改失败");
    }

    @DeleteMapping("/deleteUser/{userId}")
    @SaCheckPermission("user:deleteUser")
    public ResultUtils deleteUser(@PathVariable Long userId) {
        int rows = userService.deleteUser(userId);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }

    @GetMapping("/menus")
    public ResultUtils menus() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<MenuVO> tree = menuService.queryTreeByUserId(userId);
        return ResultUtils.success("success", tree);
    }

    @GetMapping("/perms")
    public ResultUtils perms() {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResultUtils.success("success", cn.dev33.satoken.stp.StpUtil.getPermissionList());
    }
}
