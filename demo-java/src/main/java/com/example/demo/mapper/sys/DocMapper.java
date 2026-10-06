/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.mapper.sys;

import com.example.demo.entity.sys.Doc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocMapper {

    /**
     * 按条件查询文档列表（不做 SQL 分页，总数由 Casbin 过滤后在内存中计算）。
     * 过滤条件：标题模糊、密级、部门、区域维度（上级可看下级区域，含未细化区域）。
     */
    List<Doc> queryDocList(Doc query);

    /** 按ID查询文档 */
    Doc findDocById(@Param("docId") Long docId);

    /** 新增文档 */
    int insertDoc(Doc doc);

    /** 修改文档（仅标题/内容/密级） */
    int updateDoc(Doc doc);

    /** 按ID删除文档 */
    int deleteDocById(@Param("docId") Long docId);
}
