/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 分页查询基类：列表查询实体继承本类以接收 page / pageSize 参数。
 * 默认第 1 页、每页 10 条。
 * @JsonProperty(access=WRITE_ONLY) 使分页字段只接收请求参数，不被序列化到响应。
 */
@Data
public class PageQuery {

    /** 页码，从 1 开始 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer page = 1;

    /** 每页条数，默认 10 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer pageSize = 10;

    @JsonIgnore
    public int getSafePage() {
        return (page == null || page < 1) ? 1 : page;
    }

    @JsonIgnore
    public int getSafePageSize() {
        return (pageSize == null || pageSize < 1) ? 10 : pageSize;
    }

    /** LIMIT 偏移量 */
    @JsonIgnore
    public int getOffset() {
        return (getSafePage() - 1) * getSafePageSize();
    }
}
