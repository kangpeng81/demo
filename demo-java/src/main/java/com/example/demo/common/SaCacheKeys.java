package com.example.demo.common;

import java.util.Arrays;
import java.util.List;

/**
 * sa-token 相关 Redis 缓存 key 的集中管理。
 * 把 key 模板、key 生成、清理 pattern 统一在此定义，
 * 避免 SaTokenConfig / UserController 等多处散落 String.format 与字符串拼接。
 *
 * 涉及两类 key：
 *   1. sa-token 内置缓存：sa:permissions:&lt;loginType&gt;:&lt;loginId&gt;、sa:roles:&lt;loginType&gt;:&lt;loginId&gt;
 *      （StpUtil.logout 不会清这两个，会留下脏数据）
 *   2. 本项目自定义缓存：sa:perms:&lt;loginType&gt;:&lt;loginId&gt;、sa:roles:&lt;loginType&gt;:&lt;loginId&gt;
 *      （SaTokenConfig 中 getPermissionList/getRoleList 写入，10 分钟过期）
 *
 * 注：为兼容不同 loginType，清理时用通配符 * 占位。
 */
public final class SaCacheKeys {

    private SaCacheKeys() {}

    /** sa-token 内置权限缓存 key 模板：sa:permissions:<loginType>:<loginId> */
    public static final String SA_PERMS_KEY_TEMPLATE = "sa:permissions:%s:%s";
    /** sa-token 内置角色缓存 key 模板：sa:roles:<loginType>:<loginId>（本项目自定义角色缓存复用同一前缀） */
    public static final String SA_ROLES_KEY_TEMPLATE = "sa:roles:%s:%s";
    /** 本项目自定义权限点缓存 key 模板：sa:perms:<loginType>:<loginId> */
    public static final String PERM_KEY_TEMPLATE = "sa:perms:%s:%s";

    /** 缓存过期时间（分钟） */
    public static final long EXPIRE_MINUTES = 1L;

    /**
     * 生成 sa-token 内置权限缓存 key。
     */
    public static String buildSaPermsKey(String loginType, Object loginId) {
        return String.format(SA_PERMS_KEY_TEMPLATE, loginType, loginId);
    }

    /**
     * 生成角色缓存 key（sa-token 内置角色缓存与本项目自定义角色缓存使用同一 key）。
     */
    public static String buildRoleKey(String loginType, Object loginId) {
        return String.format(SA_ROLES_KEY_TEMPLATE, loginType, loginId);
    }

    /**
     * 生成本项目自定义权限点缓存 key。
     */
    public static String buildPermKey(String loginType, Object loginId) {
        return String.format(PERM_KEY_TEMPLATE, loginType, loginId);
    }

    /**
     * 返回需要清理的所有缓存 key 列表（精确匹配，用于 logout 等场景）。
     * 接收 loginType 以构造精确 key，避免通配符 KEYS 扫描。
     * 三类 key：
     *   1. sa-token 内置权限缓存 sa:permissions:&lt;loginType&gt;:&lt;loginId&gt;
     *   2. sa-token 内置角色缓存 sa:roles:&lt;loginType&gt;:&lt;loginId&gt;
     *   3. 本项目自定义权限点缓存 sa:perms:&lt;loginType&gt;:&lt;loginId&gt;
     */
    public static List<String> clearKeys(String loginType, Object loginId) {
        return Arrays.asList(
                buildSaPermsKey(loginType, loginId),
                buildRoleKey(loginType, loginId),
                buildPermKey(loginType, loginId)
        );
    }
}
