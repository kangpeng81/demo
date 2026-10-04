CREATE DATABASE IF NOT EXISTS travl DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE travl;

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

-- 角色-菜单：admin 拥有全部菜单
INSERT INTO t_role_menu (role_id, menu_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4),
    (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);
-- 角色-菜单：user 只能看系统管理 + 用户管理 + 用户查询按钮
INSERT INTO t_role_menu (role_id, menu_id) VALUES
    (2, 1), (2, 2), (2, 5);

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
