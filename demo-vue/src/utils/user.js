import request from './request'

// 登录：POST /sys/user/login，body: { userName, password }
// 实际请求经 vite proxy 转发到 http://127.0.0.1:8080/sys/user/login
export const login = (data) => {
  return request.post('/sys/user/login', data)
}

// 退出登录：GET /sys/user/logout，后端清除 sa-token 会话状态
export const logout = () => {
  return request.get('/sys/user/logout')
}

// 用户列表（分页）：POST /sys/user/queryUser，body: { userName, name, status, page, pageSize }
// 返回 { total, page, pageSize, list }
export const listUsers = (data = {}) => {
  return request.post('/sys/user/queryUser', data)
}

// 新增用户：POST /sys/user/addUser，body: { userName, password, nickname, phone, ... }
export const addUser = (data) => {
  return request.post('/sys/user/addUser', data)
}

// 修改用户：PUT /sys/user/updateUser
export const updateUser = (data) => {
  return request.put('/sys/user/updateUser', data)
}

// 删除用户：DELETE /sys/user/deleteUser/{userId}
export const deleteUser = (userId) => {
  return request.delete(`/sys/user/deleteUser/${userId}`)
}

// 当前登录用户的菜单树：GET /sys/user/menus（按角色过滤）
export const getUserMenus = () => {
  return request.get('/sys/user/menus')
}

// 当前登录用户拥有的按钮权限点列表：GET /sys/user/perms
export const getUserPerms = () => {
  return request.get('/sys/user/perms')
}
