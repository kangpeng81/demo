import request from '../utils/request'

// 用户角色关联列表：POST /sys/userRole/listUserRole，body: { userId, roleId, ... }（可选）
export const listUserRoles = (data = {}) => {
  return request.post('/sys/userRole/listUserRole', data)
}

// 新增关联：POST /sys/userRole/addUserRole
export const addUserRole = (data) => {
  return request.post('/sys/userRole/addUserRole', data)
}

// 删除单条关联：DELETE /sys/userRole/deleteUserRole/{id}
export const deleteUserRole = (id) => {
  return request.delete(`/sys/userRole/deleteUserRole/${id}`)
}

// 给用户分配角色（覆盖式）：POST /sys/userRole/assignRoles，body: { userId, roleIds: [] }
export const assignRoles = (userId, roleIds) => {
  return request.post('/sys/userRole/assignRoles', { userId, roleIds })
}

// 查询用户已关联的角色ID集合：GET /sys/userRole/roleIdsByUserId/{userId}
export const getRoleIdsByUserId = (userId) => {
  return request.get(`/sys/userRole/roleIdsByUserId/${userId}`)
}
