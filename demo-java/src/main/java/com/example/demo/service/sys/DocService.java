/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.service.sys;

import com.example.demo.common.PageResult;
import com.example.demo.entity.sys.Doc;

public interface DocService {

    /**
     * 文档列表：区域过滤（SQL）→ Casbin 记录过滤（内存）→ 内存分页。
     */
    PageResult<Doc> listDocs(Doc query);

    /** 查看单篇文档（Casbin 校验 read） */
    Doc detail(Long docId);

    /** 按ID取文档（不校验权限），供权限试算等内部场景 */
    Doc getRaw(Long docId);

    /** 新建文档：写入创建人区域，并自动生成创建人 Casbin 策略 */
    Doc addDoc(Doc doc);

    /** 修改文档（标题/内容/密级），密级变更时同步迁移 Casbin 策略 obj 前缀 */
    int updateDoc(Doc doc);

    /** 删除文档 */
    int deleteDoc(Long docId);

    /**
     * 显式授权（属性级范围）：给目标用户在指定部门、指定密级、指定资源类型/资源实例范围内授予操作集合。
     * 仅超管或该部门领导可授权。objs 为空表示全部资源类型（doc/contract）；resIds 为空表示全部实例。
     */
    void grant(Long targetUserId, java.util.List<String> acts, Long deptId,
               java.util.List<Integer> docLevels, java.util.List<String> objs, java.util.List<Long> resIds);

    /** 收回目标用户的全部显式授权（仅超管或相关部门领导可操作） */
    void revoke(Long targetUserId);
}
