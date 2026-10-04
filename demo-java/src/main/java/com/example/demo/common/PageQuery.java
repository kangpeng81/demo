/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.common;

import lombok.Data;

/**
 * 分页查询基类：列表查询实体继承本类以接收 page / pageSize 参数。
 * 默认第 1 页、每页 10 条。
 */
@Data
public class PageQuery {

    /** 页码，从 1 开始 */
    private Integer page = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;

    public int getSafePage() {
        return (page == null || page < 1) ? 1 : page;
    }

    public int getSafePageSize() {
        return (pageSize == null || pageSize < 1) ? 10 : pageSize;
    }

    /** LIMIT 偏移量 */
    public int getOffset() {
        return (getSafePage() - 1) * getSafePageSize();
    }
}
