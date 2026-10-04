/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.common;

import cn.dev33.satoken.exception.SaTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：统一返回 ResultUtils JSON，避免默认 500 HTML 页面
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常（如"用户不存在""密码错误"） */
    @ExceptionHandler(RuntimeException.class)
    public ResultUtils handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return ResultUtils.error(e.getMessage() == null ? "系统异常" : e.getMessage());
    }

    /** sa-token 未登录 / token 失效 */
    @ExceptionHandler(SaTokenException.class)
    public ResultUtils handleSaTokenException(SaTokenException e) {
        return ResultUtils.error(401, "未登录或登录已过期，请重新登录");
    }
}
