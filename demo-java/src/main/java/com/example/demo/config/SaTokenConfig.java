package com.example.demo.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.demo.common.SaCacheKeys;
import com.example.demo.mapper.sys.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SaTokenConfig implements StpInterface {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String key = SaCacheKeys.buildPermKey(loginType, loginId);

        @SuppressWarnings("unchecked")
        List<String> perms = (List<String>) redisTemplate.opsForValue().get(key);
        if (perms != null) {
            return perms;
        }

        // t_menu type=3 行的 path 字段可能含多个权限点（分号分隔），此处展开
        List<String> rawPaths = userRoleMapper.selectPermsByUserId(Long.valueOf(loginId.toString()));
        if (rawPaths == null || rawPaths.isEmpty()) {
            perms = Collections.emptyList();
        } else {
            java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>();
            for (String p : rawPaths) {
                if (p == null) continue;
                for (String part : p.split(";")) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        set.add(trimmed);
                    }
                }
            }
            perms = new ArrayList<>(set);
        }
        redisTemplate.opsForValue().set(key, perms, SaCacheKeys.EXPIRE_MINUTES, TimeUnit.MINUTES);
        return perms;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String key = SaCacheKeys.buildRoleKey(loginType, loginId);

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) redisTemplate.opsForValue().get(key);
        if (roles != null) {
            return roles;
        }

        roles = userRoleMapper.selectRoleCodesByUserId(Long.valueOf(loginId.toString()));
        if (roles == null || roles.isEmpty()) {
            roles = Collections.emptyList();
        }
        redisTemplate.opsForValue().set(key, roles, SaCacheKeys.EXPIRE_MINUTES, TimeUnit.MINUTES);
        return roles;
    }

    /**
     * 清除指定用户的角色与权限点缓存。
     * StpUtil.logout() 只清 sa-token 内置 token/session，
     * 但以下三类 key 不会自动失效，需手动删除：
     *   1. sa-token 内置权限缓存 sa:permissions:<loginType>:<loginId>
     *   2. sa-token 内置角色缓存 sa:roles:<loginType>:<loginId>
     *   3. 本配置类自定义缓存 sa:perms:<loginType>:<loginId>
     * 否则用户调整权限后再次登录会读到旧缓存（最长 10 分钟过期）。
     * 接收 loginType 以构造精确 key，避免通配符 KEYS 扫描。
     */
    public void clearUserCache(String loginType, Object loginId) {
        for (String key : SaCacheKeys.clearKeys(loginType, loginId)) {
            redisTemplate.delete(key);
        }
    }
}
