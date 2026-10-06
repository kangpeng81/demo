/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.example.demo.annotation.DataPermission;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 数据权限切面：拦截标注 {@link DataPermission} 的 Controller 方法，
 * 根据当前登录用户的省/市/区归属，把对应过滤值注入查询参数（首个参数）。
 *
 * <p>过滤范围依据用户区域归属的「最深层级」自动收缩：
 * <ul>
 *   <li>省为空 → 超级管理员，不过滤，查看全部</li>
 *   <li>仅省有值 → 按省过滤</li>
 *   <li>省+市有值 → 按省+市过滤</li>
 *   <li>省+市+区都有值 → 按省+市+区过滤</li>
 * </ul>
 * </p>
 */
@Slf4j
@Aspect
@Component
public class DataPermissionAspect {

    /** 登录用户在 sa-token session 中存储省/市/区的 key */
    private static final String SESSION_PROVINCE = "dataScope:province";
    private static final String SESSION_CITY = "dataScope:city";
    private static final String SESSION_DISTRICT = "dataScope:district";

    @Around("@annotation(dataPermission)")
    public Object around(ProceedingJoinPoint joinPoint, DataPermission dataPermission) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            return joinPoint.proceed();
        }

        // 1. 读取当前登录用户的省/市/区归属（登录时写入 session）
        String userProvince = "userProvince1";
        String userCity ="userCity2";
        String userDistrict = "userDistrict3";
        //  city 1,2
        //  2,3
        //  city 2

        // 3. 通过反射把过滤值写入查询参数（按注解配置的字段名）
        Object query = args[0];
        setFieldValue(query, dataPermission.provinceField(), userProvince);
        if (!isEmpty(userCity)) {
            setFieldValue(query, dataPermission.cityField(), userCity);
        }
        if (!isEmpty(userDistrict)) {
            setFieldValue(query, dataPermission.districtField(), userDistrict);
        }

        return joinPoint.proceed(args);
    }

    private String getSessionValue(String key) {
        try {
            Object val = StpUtil.getSession().get(key);
            return val == null ? null : val.toString();
        } catch (Exception e) {
            log.warn("读取数据权限 session 字段失败: {}", key, e);
            return null;
        }
    }

    /** 反射设置对象字段值（含父类字段） */
    private void setFieldValue(Object target, String fieldName, Object value) {
        if (fieldName == null || fieldName.isEmpty()) {
            return;
        }
        Class<?> clazz = target.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                log.warn("设置数据权限字段失败: {}.{}", target.getClass().getSimpleName(), fieldName, e);
                return;
            }
        }
        log.warn("数据权限字段不存在: {}.{}", target.getClass().getSimpleName(), fieldName);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** 供登录时写入用户省/市/区归属到 sa-token session */
    public static void setUserRegionToSession(String province, String city, String district) {
        StpUtil.getSession().set(SESSION_PROVINCE, province);
        StpUtil.getSession().set(SESSION_CITY, city);
        StpUtil.getSession().set(SESSION_DISTRICT, district);
    }
}
