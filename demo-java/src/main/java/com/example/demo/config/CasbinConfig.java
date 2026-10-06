/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.config;

import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Casbin Enforcer 配置：
 * - 模型来自 classpath:casbin/model.conf（七元组：sub, resId, obj, act, docLevel, role, dept）
 * - 策略存储使用自定义 CasbinJdbcAdapter，读写 casbin_rule 表（v0~v6 七列结构）
 * - 开启 autoSave：运行期 addPolicy/removePolicy 等操作自动持久化到数据库
 */
@Configuration
public class CasbinConfig {

    @Bean
    public Enforcer enforcer(DataSource dataSource) throws Exception {
        Model model = new Model();
        try (InputStream in = CasbinConfig.class.getResourceAsStream("/casbin/model.conf")) {
            if (in == null) {
                throw new IllegalStateException("classpath:casbin/model.conf 不存在");
            }
            String modelText = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            model.loadModelFromText(modelText);
        }
        CasbinJdbcAdapter adapter = new CasbinJdbcAdapter(dataSource);
        Enforcer enforcer = new Enforcer(model, adapter);
        enforcer.enableAutoSave(true);
        return enforcer;
    }
}
