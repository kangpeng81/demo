/*
 * Copyright (c) 2026 demo kangpeng81
 * 本软件仅供学习研究使用，未经书面授权不得用于任何商业用途。
 * 详见项目根目录 LICENSE 文件。
 */
package com.example.demo.config;

import org.casbin.jcasbin.model.Assertion;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.Adapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义 Casbin JDBC 适配器（七列 v0~v6）。
 *
 * <p>官方 jdbc-adapter 2.7.0 硬编码 v0~v5 六列，无法承载七元组策略
 * (sub, resId, obj, act, docLevel, role, dept)，故自实现 Adapter。
 * 表结构：casbin_rule(id, ptype, v0~v6)，g 行尾部列存 NULL，读取时自动截断尾部空串。</p>
 */
public class CasbinJdbcAdapter implements Adapter {

    /** 策略列数（v0~v6） */
    private static final int COLUMN_COUNT = 7;

    private final DataSource dataSource;

    public CasbinJdbcAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void loadPolicy(Model model) {
        String sql = "SELECT ptype, v0, v1, v2, v3, v4, v5, v6 FROM casbin_rule";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ptype = rs.getString("ptype");
                List<String> rule = new ArrayList<>(COLUMN_COUNT);
                for (int i = 0; i < COLUMN_COUNT; i++) {
                    String v = rs.getString("v" + i);
                    rule.add(v == null ? "" : v);
                }
                // 截掉尾部空串，g 行仅保留实际两列，与官方 adapter 行为一致
                while (!rule.isEmpty() && rule.get(rule.size() - 1).isEmpty()) {
                    rule.remove(rule.size() - 1);
                }
                if (rule.isEmpty()) {
                    continue;
                }
                String sec = ptype.startsWith("p") ? "p" : "g";
                model.addPolicy(sec, ptype, rule);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Casbin 策略加载失败", e);
        }
    }

    @Override
    public void savePolicy(Model model) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM casbin_rule")) {
                ps.executeUpdate();
            }
            Map<String, Map<String, Assertion>> policyMap = model.model;
            if (policyMap != null) {
                for (String sec : new String[]{"p", "g"}) {
                    Map<String, Assertion> secMap = policyMap.get(sec);
                    if (secMap == null) {
                        continue;
                    }
                    for (Assertion ast : secMap.values()) {
                        for (List<String> rule : ast.policy) {
                            insert(conn, ast.key, rule);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Casbin 策略全量保存失败", e);
        }
    }

    @Override
    public void addPolicy(String sec, String ptype, List<String> rule) {
        try (Connection conn = dataSource.getConnection()) {
            insert(conn, ptype, rule);
        } catch (SQLException e) {
            throw new RuntimeException("Casbin 策略写入失败: " + rule, e);
        }
    }

    @Override
    public void removePolicy(String sec, String ptype, List<String> rule) {
        StringBuilder sql = new StringBuilder("DELETE FROM casbin_rule WHERE ptype = ?");
        for (int i = 0; i < rule.size() && i < COLUMN_COUNT; i++) {
            sql.append(" AND v").append(i).append(" = ?");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, ptype);
            for (int i = 0; i < rule.size() && i < COLUMN_COUNT; i++) {
                ps.setString(i + 2, rule.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Casbin 策略删除失败: " + rule, e);
        }
    }

    @Override
    public void removeFilteredPolicy(String sec, String ptype, int fieldIndex, String... fieldValues) {
        StringBuilder sql = new StringBuilder("DELETE FROM casbin_rule WHERE ptype = ?");
        List<String> params = new ArrayList<>();
        for (int i = 0; i < fieldValues.length; i++) {
            String v = fieldValues[i];
            if (v == null || v.isEmpty()) {
                continue; // 空值 = 该列不限定
            }
            sql.append(" AND v").append(fieldIndex + i).append(" = ?");
            params.add(v);
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, ptype);
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 2, params.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Casbin 过滤删除失败: " + ptype, e);
        }
    }

    /** 按 rule 实际长度写入 v0..v(n-1)，其余列留 NULL */
    private void insert(Connection conn, String ptype, List<String> rule) throws SQLException {
        StringBuilder cols = new StringBuilder("ptype");
        StringBuilder marks = new StringBuilder("?");
        List<String> values = new ArrayList<>();
        values.add(ptype);
        for (int i = 0; i < rule.size() && i < COLUMN_COUNT; i++) {
            cols.append(", v").append(i);
            marks.append(", ?");
            values.add(rule.get(i));
        }
        String sql = "INSERT INTO casbin_rule (" + cols + ") VALUES (" + marks + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                ps.setString(i + 1, values.get(i));
            }
            ps.executeUpdate();
        }
    }
}
