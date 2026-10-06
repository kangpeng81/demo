/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.casbin;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Casbin 策略服务（七元组模型：主体 × 资源ID × 资源类型 × 操作 × 密级 × 角色 × 部门，与省市区无关）。
 *
 * <p>请求七元组: (sub, resId, obj, act, docLevel, role, dept)，全部正则匹配。
 * resId 为资源实例ID（具体文档/合同主键，".*" 通配全部实例）；
 * obj 为资源类型字符串（doc 文档 / contract 合同，可扩展），密级等维度各资源类型通用。
 * 角色由 g 挂载（leader:dept:x / member:dept:x）在判定时解析为 leader/member/none。</p>
 *
 * <p>策略行示例（入库 v0~v6 七列）：
 * <ul>
 *   <li>领导全资源全权: p, .*, .*, (doc|contract), ^(read|write|delete|grant)$, ^[1-4]$, ^leader$, .*</li>
 *   <li>成员只读文档1-2级: p, .*, .*, doc, ^read$, ^(1|2)$, ^member$, .*</li>
 *   <li>实例级显式授权: p, ^user:10$, ^(4|7)$, ^(doc)$, ^read$, ^(3)$, .*, ^dept:1$</li>
 * </ul></p>
 *
 * <p>超管放行：拥有 sa-token 角色 admin 的用户跳过 Casbin 判定。</p>
 */
@Slf4j
@Service
public class CasbinPolicyService {

    /** 全操作 act 正则（read/write/delete/grant） */
    public static final String ACT_ALL = "^(read|write|delete|grant)$";
    /** 全部密级正则 */
    public static final String VISIBLE_ALL = "^[1-4]$";
    /** 成员可读密级范围正则（1公开 2部门） */
    public static final String VISIBLE_MEMBER = "^(1|2)$";
    /** 全资源类型正则（doc/contract 及未来扩展） */
    public static final String OBJ_ALL = ".*";
    /** 全资源实例正则（不限定具体资源ID） */
    public static final String RES_ID_ALL = ".*";

    /** 合法的操作集合（用于校验授权请求，防止正则注入） */
    private static final List<String> VALID_ACTS = List.of("read", "write", "delete", "grant");
    /** 合法的密级集合 */
    private static final List<Integer> VALID_LEVELS = List.of(1, 2, 3, 4);
    /** 合法的资源类型集合（doc 文档 / contract 合同） */
    private static final List<String> VALID_OBJS = List.of("doc", "contract");

    @Autowired
    private Enforcer enforcer;

    // ==================== 判定 ====================

    /**
     * 单次判定（含超管放行），用于单资源读/写/删/授权等点检场景。
     */
    public boolean enforce(Long userId, Long resId, String obj, Long docDeptId, Integer docLevel, String act) {
//        if (StpUtil.hasRole("admin")) {
//            return true;
//        }
        return enforceRaw(userId, resId, obj, docDeptId, docLevel, act);
    }

    public boolean enforceRaw(Long userId, Long resId, String obj, Long docDeptId, Integer docLevel, String act) {
        boolean result = enforcer.enforce(
                "user:" + userId,
                String.valueOf(resId),
                obj,
                act,
                String.valueOf(docLevel),
                resolveRole(userId, docDeptId),
                "dept:" + docDeptId
        );
        log.info("Casbin enforce: user:{}, resId:{}, obj:{}, act:{}, level:{}, dept:{}, result:{}",
                userId, resId, obj, act, docLevel, docDeptId, result);
        // 打印所有策略和 g 挂载
        log.info("All policies: {}", enforcer.getPolicy());
        log.info("All groupings: {}", enforcer.getGroupingPolicy());
        return result;
    }

    /**
     * 角色解析：g 挂载 → 当前用户在指定部门的角色 leader / member / none。
     */
    public String resolveRole(Long userId, Long deptId) {
        List<String> roles = enforcer.getRolesForUser("user:" + userId);
        if (roles.contains("leader:dept:" + deptId)) {
            return "leader";
        }
        if (roles.contains("member:dept:" + deptId)) {
            return "member";
        }
        return "none";
    }

    // ==================== 显式授权（属性级范围） ====================

    /**
     * 显式授权：给用户在指定部门、指定密级、指定资源类型/资源实例范围内授予操作集合。
     * 生成策略行: p, ^user:{userId}$, {resIdRegex}, ^({objs})$, ^({acts})$, ^({levels})$, .*, ^dept:{deptId}$
     *
     * @param objs   资源类型集合（doc/contract），null/空 表示全部资源类型
     * @param resIds 资源实例ID集合，null/空 表示全部实例（.*）；指定时授权精确到单条资源
     */
    public void grant(Long targetUserId, List<String> acts, Long deptId,
                      List<Integer> docLevels, List<String> objs, List<Long> resIds) {
        validateActs(acts);
        validateLevels(docLevels);
        String objRegex = (objs == null || objs.isEmpty()) ? OBJ_ALL : strListToRegex(objs, VALID_OBJS);
        String actRegex = "^(" + String.join("|", acts) + ")$";
        String levelRegex = "^(" + docLevels.stream().map(String::valueOf).collect(Collectors.joining("|")) + ")$";
        String resIdRegex = (resIds == null || resIds.isEmpty())
                ? RES_ID_ALL
                : "^(" + resIds.stream().map(String::valueOf).collect(Collectors.joining("|")) + ")$";
        enforcer.addPolicy("^user:" + targetUserId + "$", resIdRegex, objRegex, actRegex, levelRegex,
                ".*", "^dept:" + deptId + "$");
    }

    /**
     * 收回显式授权：删除该用户名下全部显式授权策略行。
     */
    public void revoke(Long targetUserId) {
        String subRegex = "^user:" + targetUserId + "$";
        List<List<String>> matched = new java.util.ArrayList<>();
        for (List<String> rule : enforcer.getPolicy()) {
            if (rule.size() >= 7 && subRegex.equals(rule.get(0))) {
                matched.add(rule);
            }
        }
        for (List<String> rule : matched) {
            enforcer.removePolicy(rule.get(0), rule.get(1), rule.get(2),
                    rule.get(3), rule.get(4), rule.get(5), rule.get(6));
        }
    }

    /**
     * 是否具备对指定部门的授权资格：超管或该部门领导。
     */
    public boolean canGrant(Long userId, Long deptId) {
        if (StpUtil.hasRole("admin")) {
            return true;
        }
        return "leader".equals(resolveRole(userId, deptId));
    }

    /**
     * 查询用户担任领导的所有部门ID列表（g 挂载 leader:dept:x）。
     */
    public List<Long> leaderDeptIds(Long userId) {
        return enforcer.getRolesForUser("user:" + userId).stream()
                .filter(role -> role.startsWith("leader:dept:"))
                .map(role -> Long.valueOf(role.substring("leader:dept:".length())))
                .collect(Collectors.toList());
    }

    // ==================== 人员 / 部门变更（g 角色挂载同步） ====================

    /** 人员调部门：member 虚拟角色从旧部门摘除、挂到新部门 */
    public void syncUserDept(Long userId, Long oldDeptId, Long newDeptId) {
        String sub = "user:" + userId;
        if (oldDeptId != null) {
            enforcer.removeGroupingPolicy(sub, "member:dept:" + oldDeptId);
        }
        enforcer.addGroupingPolicy(sub, "member:dept:" + newDeptId);
    }

    /** 部门换领导：leader 虚拟角色交接，换领导成本 O(1) */
    public void syncDeptLeader(Long deptId, Long oldLeaderId, Long newLeaderId) {
        if (oldLeaderId != null) {
            enforcer.removeGroupingPolicy("user:" + oldLeaderId, "leader:dept:" + deptId);
        }
        enforcer.addGroupingPolicy("user:" + newLeaderId, "leader:dept:" + deptId);
    }

    // ==================== 校验 ====================

    private void validateActs(List<String> acts) {
        if (acts == null || acts.isEmpty()) {
            throw new RuntimeException("授权操作类型不能为空");
        }
        for (String act : acts) {
            if (!VALID_ACTS.contains(act)) {
                throw new RuntimeException("非法操作类型: " + act + "，仅支持 read/write/delete/grant");
            }
        }
    }

    private void validateLevels(List<Integer> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new RuntimeException("授权密级范围不能为空");
        }
        for (Integer level : levels) {
            if (!VALID_LEVELS.contains(level)) {
                throw new RuntimeException("非法密级: " + level + "，仅支持 1-4");
            }
        }
    }

    private String strListToRegex(List<String> values, List<String> validSet) {
        for (String v : values) {
            if (!validSet.contains(v)) {
                throw new RuntimeException("非法资源类型: " + v + "，仅支持 doc/contract");
            }
        }
        return "^(" + String.join("|", values) + ")$";
    }
}
