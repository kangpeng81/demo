-- 基于 Element Plus menuData 生成的菜单数据
-- 源数据:
--   1. 控制台          (Monitor)         → type=2 菜单
--   2. 权限管理        (Grid, submenu)   → type=1 目录
--     2-1. 账户管理    (Memo)            → type=2 菜单
--     2-2. 菜单管理    (Menu)            → type=2 菜单


USE demo;

-- 清空旧菜单（同时清掉关联表，避免外键逻辑失效）
DELETE FROM t_role_menu;
DELETE FROM t_menu;

-- menu_id=1: 控制台（根菜单，无 children → type=2 菜单）
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (1, 0, '/console', '控制台', 'Monitor', 2, 10);

-- menu_id=2: 权限管理（有 children → type=1 目录；path=auth 无前导斜杠，作为分组标识）
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (2, 0, '/auth', '权限管理', 'Grid', 1, 20);

-- menu_id=3,4: 权限管理的子菜单
INSERT INTO t_menu (menu_id, parent_id, path, title, icon, type, sort) VALUES
    (3, 2, '/auth/account', '账户管理', 'Memo', 2, 10),
    (4, 2, '/auth/menu',    '菜单管理', 'Menu', 2, 20);


-- 给 admin 角色（role_id=1）分配全部新菜单
INSERT INTO t_role_menu (role_id, menu_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7);



-- 验证查询
-- SELECT * FROM t_menu ORDER BY parent_id, sort;
-- SELECT r.role_code, m.title FROM t_role_menu rm
--   INNER JOIN t_role r ON r.role_id = rm.role_id
--   INNER JOIN t_menu m ON m.menu_id = rm.menu_id
--   ORDER BY r.role_id, m.parent_id, m.sort;
