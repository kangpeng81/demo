-- 库名与应用实际连接保持一致（application.properties: spring.datasource.url ... /demo）
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE demo;

-- ============================ 表结构 ============================

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    user_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(64)  NOT NULL COMMENT '用户名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt加密后的密码',
    salt        VARCHAR(64)  DEFAULT NULL COMMENT '盐值（可选，BCrypt自带盐）',
    nickname    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    age         INT          DEFAULT NULL COMMENT '年龄',
    name        VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    province    VARCHAR(64)  DEFAULT NULL COMMENT '省（数据权限维度）',
    city        VARCHAR(64)  DEFAULT NULL COMMENT '市（数据权限维度）',
    district    VARCHAR(64)  DEFAULT NULL COMMENT '区/县（数据权限维度）',
    dept_id     BIGINT       DEFAULT NULL COMMENT '所属部门（Casbin 文档权限的域载体）',
    user_level  TINYINT      NOT NULL DEFAULT 1 COMMENT '人员级别 1-4，决定可访问最高文档密级（信息性字段）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS t_role;
CREATE TABLE t_role (
    role_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_code   VARCHAR(64)  NOT NULL COMMENT '角色编码（如 admin/user）',
    role_name   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- ----------------------------
-- 用户-角色关联表
-- ----------------------------
DROP TABLE IF EXISTS t_user_role;
CREATE TABLE t_user_role (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户-角色关联表';

-- ----------------------------
-- 菜单表（父子同表，来源 Element Plus menu）
-- ----------------------------
DROP TABLE IF EXISTS t_menu;
CREATE TABLE t_menu (
    menu_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示根节点',
    path        VARCHAR(128) DEFAULT NULL COMMENT '路由路径（Element Plus menu.path）',
    title       VARCHAR(128) DEFAULT NULL COMMENT '菜单标题',
    icon        VARCHAR(128) DEFAULT NULL COMMENT '菜单图标',
    type        TINYINT      NOT NULL DEFAULT 1 COMMENT '类型 1-目录 2-菜单 3-按钮',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序值',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1-显示 0-隐藏',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (menu_id),
    KEY idx_parent_id (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜单表（来源 Element Plus 菜单，父子同表）';

-- ----------------------------
-- 角色-菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS t_role_menu;
CREATE TABLE t_role_menu (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id),
    KEY idx_role_id (role_id),
    KEY idx_menu_id (menu_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色-菜单关联表';

-- ============================ 测试数据 ============================

-- 角色
INSERT INTO t_role (role_code, role_name, remark) VALUES
    ('admin', '超级管理员', '拥有所有权限'),
    ('user',  '普通用户',   '只读权限');

-- 用户表测试数据（密码 123456 的 BCrypt 加密值）
INSERT INTO t_user (username, password, nickname, name, age, status, province, city, district) VALUES
    ('zhangsan', '$2a$10$78QJwVRJcM3fUFoJziKSJ.UNIlD9e/.CcR0W9AAnw/uFt4NED9w8i', '张三', '张三', 25, 1, NULL, NULL, NULL),
    ('lisi',     '$2a$10$78QJwVRJcM3fUFoJziKSJ.UNIlD9e/.CcR0W9AAnw/uFt4NED9w8i', '李四', '李四', 30, 1, '广东省', NULL, NULL),
    ('wangwu',   '$2a$10$78QJwVRJcM3fUFoJziKSJ.UNIlD9e/.CcR0W9AAnw/uFt4NED9w8i', '王五', '王五', 28, 1, '广东省', '深圳市', '南山区');

-- 用户-角色绑定：zhangsan=admin，lisi/wangwu=user
INSERT INTO t_user_role (user_id, role_id) VALUES
    (1, 1),
    (2, 2),
    (3, 2);

-- 菜单（Element Plus 风格，父子同表）
-- menu_id=1 系统管理（目录）
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (1, 0, '/system', '系统管理', 'Setting', 1, 10);
-- menu_id=2,3,4 系统管理下的子菜单
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (2, 1, '/system/user', '用户管理', 'User',       2, 10),
    (3, 1, '/system/role', '角色管理', 'UserFilled', 2, 20),
    (4, 1, '/system/menu', '菜单管理', 'Menu',       2, 30);
-- menu_id=5..10 按钮型菜单（type=3）
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (5,  2, '', '用户查询', 'Search', 3, 1),
    (6,  2, '', '用户新增', 'Plus',   3, 2),
    (7,  2, '', '用户修改', 'Edit',   3, 3),
    (8,  2, '', '用户删除', 'Delete', 3, 4),
    (9,  3, '', '角色查询', 'Search', 3, 1),
    (10, 3, '', '角色授权', 'Lock',   3, 2);
-- menu_id=11 文档权限管理页 + 12/13 按钮（Casbin 五维文档权限）
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (11, 3, '/auth/docPerm', '文档权限管理', 'Document', 2, 40),
    (12, 11, '', '查询文档', 'Search', 3, 1),
    (13, 11, '', '文档授权', 'Key',    3, 2);

-- 角色-菜单：admin 拥有全部菜单
INSERT INTO t_role_menu (role_id, menu_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
    (1, 11), (1, 12), (1, 13);
-- 角色-菜单：user 只能看系统管理 + 用户管理 + 用户查询按钮 + 文档权限管理
INSERT INTO t_role_menu (role_id, menu_id) VALUES
    (2, 1), (2, 2), (2, 5), (2, 11), (2, 12), (2, 13);

-- ----------------------------
-- 部门表（Casbin 文档权限的「域」载体，含部门领导人）
-- ----------------------------
DROP TABLE IF EXISTS t_dept;
CREATE TABLE t_dept (
    dept_id        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    dept_name      VARCHAR(64) NOT NULL COMMENT '部门名称',
    parent_id      BIGINT      NOT NULL DEFAULT 0 COMMENT '上级部门ID，0为根',
    leader_user_id BIGINT      DEFAULT NULL COMMENT '部门领导人 user_id',
    province       VARCHAR(64) DEFAULT NULL COMMENT '省',
    city           VARCHAR(64) DEFAULT NULL COMMENT '市',
    district       VARCHAR(64) DEFAULT NULL COMMENT '区/县',
    status         TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
    create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (dept_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '部门表';

-- ----------------------------
-- 文档表（Casbin 五维权限控制的资源：文档类型 × 操作 × 密级 × 角色 × 部门）
-- ----------------------------
DROP TABLE IF EXISTS t_doc;
CREATE TABLE t_doc (
    doc_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    title       VARCHAR(200) NOT NULL COMMENT '文档标题',
    content     TEXT         COMMENT '文档内容',
    obj         VARCHAR(32)  NOT NULL DEFAULT 'doc' COMMENT '资源类型 doc-文档 contract-合同（Casbin obj 维度，可扩展）',
    doc_level   TINYINT      NOT NULL DEFAULT 1 COMMENT '密级 1-公开 2-部门 3-部门机密 4-区域',
    dept_id     BIGINT       NOT NULL COMMENT '归属部门ID（Casbin 域）',
    creator_id  BIGINT       NOT NULL COMMENT '创建人ID',
    province    VARCHAR(64)  DEFAULT NULL COMMENT '省（区域权限维度，继承创建人）',
    city        VARCHAR(64)  DEFAULT NULL COMMENT '市（区域权限维度，继承创建人）',
    district    VARCHAR(64)  DEFAULT NULL COMMENT '区/县（区域权限维度，继承创建人）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (doc_id),
    KEY idx_dept (dept_id),
    KEY idx_creator (creator_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文档表';

-- ----------------------------
-- 菜单-部门可见性关联表（无记录=全员可见；有记录=仅关联部门可见）
-- ----------------------------
DROP TABLE IF EXISTS t_menu_dept;
CREATE TABLE t_menu_dept (
    id      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_dept (menu_id, dept_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '菜单-部门可见性关联表';

-- ----------------------------
-- Casbin 策略表（自定义 CasbinJdbcAdapter 七列结构，官方 jdbc-adapter 硬编码六列不支持七元组）
-- ptype: p=授权策略 g=部门角色挂载（leader/member，角色判定来源）
-- 七元组策略: v0=sub v1=resId(资源实例ID) v2=obj(资源类型 doc/contract) v3=act v4=docLevel v5=role v6=dept（全部正则）
-- ----------------------------
DROP TABLE IF EXISTS casbin_rule;
CREATE TABLE casbin_rule (
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    ptype VARCHAR(16)  NOT NULL,
    v0    VARCHAR(128) DEFAULT NULL,
    v1    VARCHAR(128) DEFAULT NULL,
    v2    VARCHAR(128) DEFAULT NULL,
    v3    VARCHAR(128) DEFAULT NULL,
    v4    VARCHAR(128) DEFAULT NULL,
    v5    VARCHAR(128) DEFAULT NULL,
    v6    VARCHAR(128) DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_ptype (ptype)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'Casbin 策略存储表';

-- ============================ 文档权限种子数据 ============================

-- 增加一名普通成员（补充文档权限演示角色）
INSERT INTO t_user (username, password, nickname, name, age, status, province, city, district) VALUES
    ('zhaoliu', '$2a$10$78QJwVRJcM3fUFoJziKSJ.UNIlD9e/.CcR0W9AAnw/uFt4NED9w8i', '赵六', '赵六', 26, 1, '广东省', '深圳市', NULL);
-- 用户-角色：zhaoliu=user
INSERT INTO t_user_role (user_id, role_id) VALUES (4, 2);

-- 用户-部门绑定：wangwu→深圳研发部(领导)，zhaoliu→深圳研发部(成员)，lisi→广州市场部(领导)
UPDATE t_user SET dept_id = 1, user_level = 2 WHERE user_id = 3;
UPDATE t_user SET dept_id = 1, user_level = 1 WHERE user_id = 4;
UPDATE t_user SET dept_id = 2, user_level = 4 WHERE user_id = 2;

-- 部门：深圳研发部（领导 wangwu）、广州市场部（领导 lisi）
INSERT INTO t_dept (dept_id, dept_name, parent_id, leader_user_id, province, city, district) VALUES
    (1, '深圳研发部', 0, 3, '广东省', '深圳市', '南山区'),
    (2, '广州市场部', 0, 2, '广东省', '广州市', NULL);

-- 文档：wangwu 创建 1-4（密级递增、资源类型含合同演示），lisi 创建 5（跨部门演示）
INSERT INTO t_doc (doc_id, title, content, obj, doc_level, dept_id, creator_id, province, city, district) VALUES
    (1, '入职指引',   '面向全员的基础指引',     'doc',      1, 1, 3, '广东省', '深圳市', NULL),
    (2, '研发周报',   '本周研发进展汇总',       'doc',      2, 1, 3, '广东省', '深圳市', NULL),
    (3, '薪资方案',   '敏感：薪酬调整方案',     'doc',      3, 1, 3, '广东省', '深圳市', NULL),
    (4, '省级战略',   '省级战略合作备忘录',     'contract', 4, 1, 3, '广东省', '深圳市', NULL),
    (5, '市场计划',   '广州市场部季度计划',     'doc',      2, 2, 2, '广东省', NULL, NULL);

-- 菜单-部门：角色管理菜单仅深圳研发部可见
INSERT INTO t_menu_dept (menu_id, dept_id) VALUES (3, 1);

-- Casbin 策略种子（七元组模型：主体 × 资源ID × 资源类型 × 操作 × 密级 × 角色 × 部门，与省市区无关）
-- g：部门角色挂载（leader/member），判定时由服务层解析为 r.role = leader|member|none
INSERT INTO casbin_rule (ptype, v0, v1) VALUES
    ('g', 'user:3', 'leader:dept:1'),
    ('g', 'user:4', 'member:dept:1'),
    ('g', 'user:2', 'leader:dept:2');
-- p：七元组策略行，七字段全部正则匹配（v0=sub v1=resId v2=obj v3=act v4=docLevel v5=role v6=dept）
--   领导：全部实例、全部资源（doc/contract）、全密级、全操作
--   成员：全部实例、全部资源、只读 1-2 级
INSERT INTO casbin_rule (ptype, v0, v1, v2, v3, v4, v5, v6) VALUES
    ('p', '.*', '.*', '(doc|contract)', '^(read|write|delete|grant)$', '^[1-4]$', '^leader$', '.*'),
    ('p', '.*', '.*', 'doc',            '^read$',                      '^(1|2)$', '^member$', '.*'),
    ('p', '.*', '.*', 'contract',       '^read$',                      '^(1|2)$', '^member$', '.*');
-- p：实例级显式授权行示例（user:10 仅对资源实例 4/7、dept:1 的 3 级可读；按需启用）
-- INSERT INTO casbin_rule (ptype, v0, v1, v2, v3, v4, v5, v6) VALUES
--     ('p', '^user:10$', '^(4|7)$', '^(doc)$', '^read$', '^(3)$', '.*', '^dept:1$');

-- ============================ 各表 CRUD 示例 ============================

-- ----------------------------- 角色表 t_role -----------------------------
-- 增
-- INSERT INTO t_role (role_code, role_name, remark) VALUES ('manager', '经理', '部门经理');
-- 查
-- SELECT * FROM t_role WHERE status = 1;
-- SELECT * FROM t_role WHERE role_code = 'admin';
-- 改
-- UPDATE t_role SET role_name = '管理员v2', remark = '包含全部权限' WHERE role_id = 1;
-- 删
-- DELETE FROM t_role WHERE role_id = 3;

-- ----------------------------- 用户-角色 t_user_role -----------------------------
-- 给用户分配角色
-- INSERT INTO t_user_role (user_id, role_id) VALUES (1, 2);
-- 查某用户的角色
-- SELECT r.* FROM t_role r
-- INNER JOIN t_user_role ur ON ur.role_id = r.role_id
-- WHERE ur.user_id = 1;
-- 删某个用户的某角色
-- DELETE FROM t_user_role WHERE user_id = 1 AND role_id = 2;

-- ----------------------------- 菜单 t_menu -----------------------------
-- 增（根目录）
-- INSERT INTO t_menu (parent_id, path, title, icon, type, sort) VALUES (0, '/profile', '个人中心', 'UserFilled', 2, 50);
-- 增（子菜单，parent_id = 上一步返回的 menu_id）
-- INSERT INTO t_menu (parent_id, path, title, icon, type, sort) VALUES (LAST_INSERT_ID(), '/profile/info', '个人信息', 'InfoFilled', 2, 10);
-- 查全部菜单树
-- SELECT * FROM t_menu WHERE status = 1 ORDER BY parent_id, sort;
-- 查某节点的所有子节点
-- SELECT * FROM t_menu WHERE parent_id = 1 ORDER BY sort;
-- 改
-- UPDATE t_menu SET title = '用户管理v2', icon = 'Avatar' WHERE menu_id = 2;
-- 删（注意删除父节点需先删子节点）
-- DELETE FROM t_menu WHERE menu_id = 10;

-- ----------------------------- 角色-菜单 t_role_menu -----------------------------
-- 给角色分配菜单
-- INSERT INTO t_role_menu (role_id, menu_id) VALUES (2, 3);
-- 查某角色的菜单列表
-- SELECT m.* FROM t_menu m
-- INNER JOIN t_role_menu rm ON rm.menu_id = m.menu_id
-- WHERE rm.role_id = 1 ORDER BY m.parent_id, m.sort;
-- 删某角色的菜单授权
-- DELETE FROM t_role_menu WHERE role_id = 2 AND menu_id = 3;

-- ============================ 联表查询场景 ============================

-- 1. 查某用户拥有的所有菜单（构造左侧菜单树）
-- SELECT DISTINCT m.* FROM t_user u
-- INNER JOIN t_user_role ur ON ur.user_id = u.user_id
-- INNER JOIN t_role_menu rm ON rm.role_id = ur.role_id
-- INNER JOIN t_menu m ON m.menu_id = rm.menu_id
-- WHERE u.user_id = 1 AND m.status = 1
-- ORDER BY m.parent_id, m.sort;

-- 2. 查某用户拥有的所有角色编码
-- SELECT DISTINCT r.role_code FROM t_user u
-- INNER JOIN t_user_role ur ON ur.user_id = u.user_id
-- INNER JOIN t_role r ON r.role_id = ur.role_id
-- WHERE u.user_id = 1;
