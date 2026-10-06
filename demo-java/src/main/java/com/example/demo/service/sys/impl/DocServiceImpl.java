/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.aspect.DataPermissionAspect;
import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.Doc;
import com.example.demo.mapper.sys.DeptMapper;
import com.example.demo.mapper.sys.DocMapper;
import com.example.demo.service.casbin.CasbinPolicyService;
import com.example.demo.service.sys.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private DeptMapper  deptMapper;
    @Autowired
    private CasbinPolicyService casbinPolicyService;

    @Override
    public PageResult<Doc> listDocs(Doc query) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 1. SQL 层：按业务条件 + 区域维度过滤（区域值由 @DataPermission 切面注入）
        List<Doc> all = docMapper.queryDocList(query);
        // 2. 内存层：Casbin ABAC 记录级过滤（read），超管在外层判一次后放行全部
        boolean admin = StpUtil.hasRole("admin");
        List<Doc> allowed = new ArrayList<>();
        for (Doc doc : all) {
            if (admin || casbinPolicyService.enforceRaw(userId, doc.getDocId(), doc.getObj(), doc.getDeptId(), doc.getDocLevel(), "read")) {
                allowed.add(doc);
            }
        }
        // 3. 内存分页：Casbin 过滤后总数才准确
        long total = allowed.size();
        int from = Math.min(query.getOffset(), (int) total);
        int to = Math.min(from + query.getSafePageSize(), (int) total);
        return new PageResult<>(total, query.getSafePage(), query.getSafePageSize(), allowed.subList(from, to));
    }

    @Override
    public Doc detail(Long docId) {
        Doc doc = requireDoc(docId);
        checkPermission(doc, "read");
        return doc;
    }

    @Override
    public Doc getRaw(Long docId) {
        return requireDoc(docId);
    }

    @Override
    public Doc addDoc(Doc doc) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (doc.getTitle() == null || doc.getTitle().isEmpty()) {
            throw new RuntimeException("文档标题不能为空");
        }
        if (doc.getObj() == null || doc.getObj().isEmpty()) {
            doc.setObj("doc");
        }
        if (doc.getDocLevel() == null) {
            doc.setDocLevel(1);
        }
        // 归属部门：未指定时取创建人所在部门
        if (doc.getDeptId() == null) {
            doc.setDeptId(deptMapper.selectDeptIdByUserId(userId));
        }
        if (doc.getDeptId() == null) {
            throw new RuntimeException("无法确定归属部门：未指定且创建人未分配部门");
        }
        // 区域标注：继承创建人的省/市/区（登录时写入 session）
        doc.setCreatorId(userId);
        doc.setProvince(sessionValue(DataPermissionAspect.SESSION_PROVINCE));
        doc.setCity(sessionValue(DataPermissionAspect.SESSION_CITY));
        doc.setDistrict(sessionValue(DataPermissionAspect.SESSION_DISTRICT));

        docMapper.insertDoc(doc);
        // 创建人权限由 matcher 关系分支(r.sub == r.docCreator)自动覆盖，无需写入策略
        return doc;
    }

    @Override
    public int updateDoc(Doc doc) {
        Doc old = requireDoc(doc.getDocId());
        checkPermission(old, "write");
        return docMapper.updateDoc(doc);
    }

    @Override
    public int deleteDoc(Long docId) {
        Doc doc = requireDoc(docId);
        checkPermission(doc, "delete");
        return docMapper.deleteDocById(docId);
    }

    @Override
    public void grant(Long targetUserId, List<String> acts, Long deptId,
                      List<Integer> docLevels, List<String> objs, List<Long> resIds) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 仅超管或目标部门领导可授权
        if (!casbinPolicyService.canGrant(userId, deptId)) {
            throw new RuntimeException("无授权资格：仅超管或部门 " + deptId + " 的领导可授权");
        }
        casbinPolicyService.grant(targetUserId, acts, deptId, docLevels, objs, resIds);
    }

    @Override
    public void revoke(Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean admin = StpUtil.hasRole("admin");
        if (!admin) {
            // 非超管：必须是任一部门领导才可收回
            boolean anyLeader = !casbinPolicyService.leaderDeptIds(userId).isEmpty();
            if (!anyLeader) {
                throw new RuntimeException("无授权资格：仅超管或部门领导可收回授权");
            }
        }
        casbinPolicyService.revoke(targetUserId);
    }

    // ==================== 内部方法 ====================

    private Doc requireDoc(Long docId) {
        Doc doc = docMapper.findDocById(docId);
        if (doc == null) {
            throw new RuntimeException("文档不存在");
        }
        return doc;
    }

    private void checkPermission(Doc doc, String act) {
        Long userId = StpUtil.getLoginIdAsLong();
        if (!casbinPolicyService.enforce(userId, doc.getDocId(), doc.getObj(), doc.getDeptId(), doc.getDocLevel(), act)) {
            throw new RuntimeException("无权限对文档《" + doc.getTitle() + "》执行操作: " + act);
        }
    }

    private String sessionValue(String key) {
        try {
            Object val = StpUtil.getSession().get(key);
            return val == null ? null : val.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
