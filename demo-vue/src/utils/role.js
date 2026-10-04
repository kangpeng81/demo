import request from '../utils/request'

// 角色列表（分页）：POST /sys/role/listRole，body: { roleName, page, pageSize }
// 返回 { total, page, pageSize, list }
export const listRoles = (data = {}) => {
  return request.post('/sys/role/listRole', data)
}

// 新增角色：POST /sys/role/addRole，body: { roleName, roleCode, remark }
export const addRole = (data) => {
  return request.post('/sys/role/addRole', data)
}

// 更新角色：PUT /sys/role/updateRole
export const updateRole = (data) => {
  return request.put('/sys/role/updateRole', data)
}

// 删除角色：DELETE /sys/role/deleteRole/{roleId}
export const deleteRole = (roleId) => {
  return request.delete(`/sys/role/deleteRole/${roleId}`)
}
