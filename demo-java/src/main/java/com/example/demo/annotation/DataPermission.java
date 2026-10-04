package com.example.demo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解：标注在 Controller 查询方法上，按省/市/区维度过滤数据。
 *
 * <p>通过 {@link #provinceField()} / {@link #cityField()} / {@link #districtField()}
 * 可配置业务查询实体中表示省、市、区的字段名，适配不同业务表的字段命名差异。</p>
 *
 * <p>过滤规则（依据当前登录用户的省/市/区归属的最深层级）：
 * <ul>
 *   <li>省/市/区都为空 → 不过滤，查看全部</li>
 *   <li>仅省有值 → 过滤该省</li>
 *   <li>省+市有值 → 过滤该市</li>
 *   <li>省+市+区都有值 → 仅过滤该区</li>
 * </ul>
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {

    /** 业务实体中表示「省」的字段名 */
    String provinceField() default "province";

    /** 业务实体中表示「市」的字段名 */
    String cityField() default "city";

    /** 业务实体中表示「区/县」的字段名 */
    String districtField() default "district";
}
