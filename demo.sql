/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : localhost:3306
 Source Schema         : demo

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 04/10/2026 12:59:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示根节点',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由路径（Element Plus menu.path）',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单标题',
  `icon` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '类型 1-目录 2-菜单 3-按钮',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1-显示 0-隐藏',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表（来源 Element Plus 菜单，父子同表）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_menu
-- ----------------------------
INSERT INTO `t_menu` VALUES (1, 0, '/console', '控制台', 'Monitor', 2, 10, 1, '2026-09-15 14:34:23', '2026-09-17 15:17:28');
INSERT INTO `t_menu` VALUES (2, 0, '/auth', '权限管理', 'Grid', 1, 20, 1, '2026-09-15 14:34:23', '2026-09-16 11:22:15');
INSERT INTO `t_menu` VALUES (3, 2, '/auth/account', '用户管理', 'UserFilled', 2, 21, 1, '2026-09-15 14:34:23', '2026-09-16 10:24:04');
INSERT INTO `t_menu` VALUES (4, 2, '/auth/menu', '菜单管理', 'Menu', 2, 23, 1, '2026-09-15 14:34:23', '2026-09-16 10:21:22');
INSERT INTO `t_menu` VALUES (12, 2, 'auth/role', '角色管理', 'User', 2, 22, 1, '2026-09-16 10:18:44', '2026-09-16 15:44:07');
INSERT INTO `t_menu` VALUES (13, 2, '/auth/userRole', '用户角色管理', 'Connection', 2, 24, 1, '2026-09-16 10:19:28', '2026-09-17 14:45:46');
INSERT INTO `t_menu` VALUES (14, 2, '/auth/roleMenu', '角色菜单管理', 'Share', 2, 25, 1, '2026-09-16 10:20:23', '2026-09-17 14:45:55');
INSERT INTO `t_menu` VALUES (15, 3, 'user:addUser;userRole:assignRoles', '新增用户', '', 3, 1, 1, '2026-09-16 10:31:03', '2026-09-16 15:40:42');
INSERT INTO `t_menu` VALUES (16, 3, 'user:updateUser;userRole:assignRoles', '修改用户', '', 3, 2, 1, '2026-09-16 10:49:21', '2026-09-16 16:28:22');
INSERT INTO `t_menu` VALUES (17, 3, 'user:deleteUser', '删除用户', '', 3, 3, 1, '2026-09-16 10:49:55', '2026-09-16 15:40:52');
INSERT INTO `t_menu` VALUES (19, 3, 'user:query', '查询用户', '', 3, 0, 1, '2026-09-16 15:00:45', '2026-09-16 16:28:22');
INSERT INTO `t_menu` VALUES (20, 12, 'role:listRole', '查询角色', '', 3, 0, 1, '2026-09-16 15:27:47', '2026-09-16 15:43:52');
INSERT INTO `t_menu` VALUES (22, 12, 'role:addRole;roleMenu:assignMenus', '新增角色', '', 3, 2, 1, '2026-09-16 15:42:15', '2026-09-16 15:43:27');
INSERT INTO `t_menu` VALUES (23, 12, 'role:updateRole;roleMenu:assignMenus', '修改角色', '', 3, 2, 1, '2026-09-16 15:42:35', '2026-09-16 15:43:19');
INSERT INTO `t_menu` VALUES (24, 4, 'menu:tree', '查询菜单', '', 3, 0, 1, '2026-09-16 15:44:48', '2026-09-16 15:45:24');
INSERT INTO `t_menu` VALUES (25, 4, 'menu:addMenu', '新增菜单', '', 3, 1, 1, '2026-09-16 15:45:18', '2026-09-16 15:45:18');
INSERT INTO `t_menu` VALUES (26, 4, 'menu:updateMenu', '修改菜单', '', 3, 0, 1, '2026-09-16 15:45:57', '2026-09-16 15:45:57');
INSERT INTO `t_menu` VALUES (27, 4, 'menu:deleteMenu', '删除菜单', '', 3, 3, 1, '2026-09-16 15:46:19', '2026-09-16 15:46:29');
INSERT INTO `t_menu` VALUES (28, 13, 'userRole:listUserRole', '用户角色查询', '', 3, 0, 1, '2026-09-16 15:47:08', '2026-09-16 16:28:22');
INSERT INTO `t_menu` VALUES (30, 13, 'userRole:deleteUserRole', '用户角色删除', '', 3, 2, 1, '2026-09-16 15:48:36', '2026-09-17 15:58:38');
INSERT INTO `t_menu` VALUES (31, 14, 'roleMenu:listRoleMenu', '角色菜单查询', '', 3, 0, 1, '2026-09-16 15:49:19', '2026-09-16 15:49:19');
INSERT INTO `t_menu` VALUES (33, 14, 'roleMenu:deleteRoleMenu', '角色菜单删除', '', 3, 2, 1, '2026-09-16 15:50:23', '2026-09-17 15:58:45');
INSERT INTO `t_menu` VALUES (34, 12, 'role:deleteRole', '删除角色', '', 3, 3, 1, '2026-09-16 16:28:22', '2026-09-16 16:28:38');
INSERT INTO `t_menu` VALUES (35, 14, 'roleMenu:addRoleMenu', '新增', '', 3, 0, 1, '2026-10-04 11:00:16', '2026-10-04 11:00:41');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码（如 admin/user）',
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES (1, 'admin', '超级管理员', '拥有所有权限', 1, '2026-09-15 11:48:51', '2026-09-15 11:48:51');
INSERT INTO `t_role` VALUES (2, 'user', '普通用户', '只读权限', 1, '2026-09-15 11:48:51', '2026-09-15 11:48:51');
INSERT INTO `t_role` VALUES (4, 'manager', '经理', '', 1, '2026-09-16 10:16:09', '2026-09-16 10:16:09');
INSERT INTO `t_role` VALUES (7, 'test1', '测试', '', 1, '2026-09-16 10:53:08', '2026-09-16 10:53:08');

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 318 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_role_menu
-- ----------------------------
INSERT INTO `t_role_menu` VALUES (316, 1, 2);
INSERT INTO `t_role_menu` VALUES (296, 1, 3);
INSERT INTO `t_role_menu` VALUES (306, 1, 4);
INSERT INTO `t_role_menu` VALUES (301, 1, 12);
INSERT INTO `t_role_menu` VALUES (311, 1, 13);
INSERT INTO `t_role_menu` VALUES (317, 1, 14);
INSERT INTO `t_role_menu` VALUES (298, 1, 15);
INSERT INTO `t_role_menu` VALUES (299, 1, 16);
INSERT INTO `t_role_menu` VALUES (300, 1, 17);
INSERT INTO `t_role_menu` VALUES (297, 1, 19);
INSERT INTO `t_role_menu` VALUES (302, 1, 20);
INSERT INTO `t_role_menu` VALUES (303, 1, 22);
INSERT INTO `t_role_menu` VALUES (304, 1, 23);
INSERT INTO `t_role_menu` VALUES (307, 1, 24);
INSERT INTO `t_role_menu` VALUES (309, 1, 25);
INSERT INTO `t_role_menu` VALUES (308, 1, 26);
INSERT INTO `t_role_menu` VALUES (310, 1, 27);
INSERT INTO `t_role_menu` VALUES (312, 1, 28);
INSERT INTO `t_role_menu` VALUES (313, 1, 30);
INSERT INTO `t_role_menu` VALUES (314, 1, 31);
INSERT INTO `t_role_menu` VALUES (305, 1, 34);
INSERT INTO `t_role_menu` VALUES (315, 1, 35);
INSERT INTO `t_role_menu` VALUES (22, 2, 5);
INSERT INTO `t_role_menu` VALUES (23, 2, 6);
INSERT INTO `t_role_menu` VALUES (24, 2, 7);
INSERT INTO `t_role_menu` VALUES (58, 7, 2);
INSERT INTO `t_role_menu` VALUES (59, 7, 3);
INSERT INTO `t_role_menu` VALUES (57, 7, 4);
INSERT INTO `t_role_menu` VALUES (56, 7, 12);
INSERT INTO `t_role_menu` VALUES (54, 7, 15);
INSERT INTO `t_role_menu` VALUES (55, 7, 17);
INSERT INTO `t_role_menu` VALUES (139, 11, 1);
INSERT INTO `t_role_menu` VALUES (140, 11, 2);
INSERT INTO `t_role_menu` VALUES (141, 11, 3);
INSERT INTO `t_role_menu` VALUES (151, 11, 4);
INSERT INTO `t_role_menu` VALUES (164, 11, 5);
INSERT INTO `t_role_menu` VALUES (165, 11, 6);
INSERT INTO `t_role_menu` VALUES (166, 11, 7);
INSERT INTO `t_role_menu` VALUES (146, 11, 12);
INSERT INTO `t_role_menu` VALUES (156, 11, 13);
INSERT INTO `t_role_menu` VALUES (160, 11, 14);
INSERT INTO `t_role_menu` VALUES (143, 11, 15);
INSERT INTO `t_role_menu` VALUES (144, 11, 16);
INSERT INTO `t_role_menu` VALUES (145, 11, 17);
INSERT INTO `t_role_menu` VALUES (142, 11, 19);
INSERT INTO `t_role_menu` VALUES (147, 11, 20);
INSERT INTO `t_role_menu` VALUES (148, 11, 22);
INSERT INTO `t_role_menu` VALUES (149, 11, 23);
INSERT INTO `t_role_menu` VALUES (152, 11, 24);
INSERT INTO `t_role_menu` VALUES (154, 11, 25);
INSERT INTO `t_role_menu` VALUES (153, 11, 26);
INSERT INTO `t_role_menu` VALUES (155, 11, 27);
INSERT INTO `t_role_menu` VALUES (157, 11, 28);
INSERT INTO `t_role_menu` VALUES (158, 11, 29);
INSERT INTO `t_role_menu` VALUES (159, 11, 30);
INSERT INTO `t_role_menu` VALUES (161, 11, 31);
INSERT INTO `t_role_menu` VALUES (162, 11, 32);
INSERT INTO `t_role_menu` VALUES (163, 11, 33);
INSERT INTO `t_role_menu` VALUES (150, 11, 34);

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'BCrypt加密后的密码',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐值（可选，BCrypt自带盐）',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省（数据权限维度）',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市（数据权限维度）',
  `district` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '区/县（数据权限维度）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (1, 'test1', '$2a$10$asm27fE9Bkrz/FcKlxF95uXt03C1eKlSOYl1d635EKdRaw6yp9vbK', NULL, 'qq', '', '', NULL, 'qq11', 1, '广东省', NULL, NULL, '2026-09-15 11:58:28', '2026-10-04 12:14:29');
INSERT INTO `t_user` VALUES (3, 'wangwu', '$2a$10$78QJwVRJcM3fUFoJziKSJ.UNIlD9e/.CcR0W9AAnw/uFt4NED9w8i', NULL, '王五', '', '', 28, '王五1', 1, '广东省', '深圳市', NULL, '2026-09-15 11:48:51', '2026-10-04 12:14:29');
INSERT INTO `t_user` VALUES (6, 'admin', '$2a$10$q2vbX//DFLO6saVwGUEdeOulhvYhkHyh6rrdCqCpZm0m.NfR0FctG', NULL, 'admin', '', '', NULL, '', 1, NULL, NULL, NULL, '2026-09-16 10:15:41', '2026-09-16 10:15:41');
INSERT INTO `t_user` VALUES (10, 'test3', '$2a$10$Q9NZx9xml/vt.hg0jXEUe.GIwAj7D2A3gbQ1xZWctTbmzfO2axF7O', NULL, '', '', '', NULL, '', 1, '广东省', '深圳市', '南山区', '2026-09-16 15:12:18', '2026-10-04 12:14:29');

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES (30, 1, 1);
INSERT INTO `t_user_role` VALUES (17, 3, 2);
INSERT INTO `t_user_role` VALUES (18, 6, 1);
INSERT INTO `t_user_role` VALUES (23, 10, 1);
INSERT INTO `t_user_role` VALUES (24, 10, 2);
INSERT INTO `t_user_role` VALUES (25, 10, 4);
INSERT INTO `t_user_role` VALUES (26, 10, 7);

SET FOREIGN_KEY_CHECKS = 1;
