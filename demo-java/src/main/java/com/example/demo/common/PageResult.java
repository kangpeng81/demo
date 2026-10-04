package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果：{ total, page, pageSize, list }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private int page;

    /** 每页条数 */
    private int pageSize;

    /** 当前页数据 */
    private List<T> list;

    public static <T> PageResult<T> of(long total, PageQuery query, List<T> list) {
        return new PageResult<>(total, query.getSafePage(), query.getSafePageSize(), list);
    }
}
