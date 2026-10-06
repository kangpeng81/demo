/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.controller.sys;

import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.annotation.DataPermission;
import com.example.demo.common.PageResult;
import com.example.demo.common.ResultUtils;
import com.example.demo.entity.sys.Doc;
import com.example.demo.service.casbin.CasbinPolicyService;
import com.example.demo.service.sys.DocService;
import lombok.Data;
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

/**
 * 文档接口：接口登录校验由 Sa-Token 拦截器完成，
 * 记录级访问控制由 Casbin 完成（创建人/部门/部门领导/区域管理员/单文档授权）。
 */
@RestController
@RequestMapping("/sys/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @Autowired
    private CasbinPolicyService casbinPolicyService;

    /** 文档列表：区域过滤 + Casbin read 过滤 + 内存分页 */
    @PostMapping("/list")
    @DataPermission(provinceField = "province", cityField = "city", districtField = "district")
    public ResultUtils list(@RequestBody(required = false) Doc query) {
        PageResult<Doc> result = docService.listDocs(query == null ? new Doc() : query);
        return ResultUtils.success("success", result);
    }

    /** 文档详情 */
    @GetMapping("/detail/{docId}")
    public ResultUtils detail(@PathVariable Long docId) {
        return ResultUtils.success("success", docService.detail(docId));
    }

    /** 新建文档（创建人自动获得全操作策略） */
    @PostMapping("/add")
    public ResultUtils add(@RequestBody Doc doc) {
        return ResultUtils.success("创建成功", docService.addDoc(doc));
    }

    /** 修改文档（标题/内容/密级） */
    @PutMapping("/update")
    public ResultUtils update(@RequestBody Doc doc) {
        int rows = docService.updateDoc(doc);
        return rows > 0 ? ResultUtils.successMsg("修改成功") : ResultUtils.error("修改失败");
    }

    /** 删除文档 */
    @DeleteMapping("/delete/{docId}")
    public ResultUtils delete(@PathVariable Long docId) {
        int rows = docService.deleteDoc(docId);
        return rows > 0 ? ResultUtils.successMsg("删除成功") : ResultUtils.error("删除失败");
    }

    /** 显式授权（属性级范围）：给目标用户在指定部门、指定密级范围授予操作集合 */
    @PostMapping("/grant")
    public ResultUtils grant(@RequestBody GrantRequest req) {
        docService.grant(req.getTargetUserId(), req.getActs(), req.getDeptId(),
                req.getDocLevels(), req.getObjs(), req.getResIds());
        return ResultUtils.successMsg("授权成功");
    }

    /** 收回目标用户的全部显式授权 */
    @PostMapping("/revoke")
    public ResultUtils revoke(@RequestBody GrantRequest req) {
        docService.revoke(req.getTargetUserId());
        return ResultUtils.successMsg("已收回授权");
    }

    /** 权限判定试算：返回当前用户对指定资源的 act 是否放行（演示/排查用，不做任何操作） */
    @GetMapping("/check/{docId}/{act}")
    public ResultUtils check(@PathVariable Long docId, @PathVariable String act) {
        Doc doc = docService.getRaw(docId);
        boolean allowed = casbinPolicyService.enforce(
                StpUtil.getLoginIdAsLong(), doc.getDocId(), doc.getObj(), doc.getDeptId(), doc.getDocLevel(), act);
        ResultUtils result = ResultUtils.successMsg(allowed ? "允许" : "拒绝");
        return result.put("allowed", allowed).put("act", act).put("obj", doc.getObj()).put("docLevel", doc.getDocLevel());
    }

    /** 授权请求体（属性级范围） */
    @Data
    public static class GrantRequest {
        /** 被授权用户ID */
        private Long targetUserId;
        /** 授权的操作集合：read/write/delete/grant 的子集 */
        private List<String> acts;
        /** 授权范围：部门ID */
        private Long deptId;
        /** 授权范围：密级集合（1公开 2部门 3机密 4区域），空表示全部 */
        private List<Integer> docLevels;
        /** 授权范围：资源类型集合（doc 文档 / contract 合同），空表示全部 */
        private List<String> objs;
        /** 授权范围：资源实例ID集合（精确到单条资源），空表示全部实例 */
        private List<Long> resIds;
    }
}
