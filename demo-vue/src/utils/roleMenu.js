import request from '../utils/request'

// 角色菜单关联列表：POST /sys/roleMenu/listRoleMenu
export const listRoleMenus = (data = {}) => {
  return request.post('/sys/roleMenu/listRoleMenu', data)
}

// 新增关联：POST /sys/roleMenu/addRoleMenu
export const addRoleMenu = (data) => {
  return request.post('/sys/roleMenu/addRoleMenu', data)
}

// 删除单条关联：DELETE /sys/roleMenu/deleteRoleMenu/{id}
export const deleteRoleMenu = (id) => {
  return request.delete(`/sys/roleMenu/deleteRoleMenu/${id}`)
}

// 给角色分配菜单（覆盖式）：POST /sys/roleMenu/assignMenus，body: { roleId, menuIds: [] }
export const assignMenus = (roleId, menuIds) => {
  return request.post('/sys/roleMenu/assignMenus', { roleId, menuIds })
}

// 查询角色已关联的菜单ID集合：GET /sys/roleMenu/menuIdsByRoleId/{roleId}
export const getMenuIdsByRoleId = (roleId) => {
  return request.get(`/sys/roleMenu/menuIdsByRoleId/${roleId}`)
}
