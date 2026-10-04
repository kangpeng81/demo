import request from './request'

// 菜单列表（扁平）：POST /sys/menu/listMenu
export const listMenus = (data = {}) => {
  return request.post('/sys/menu/listMenu', data)
}

// 菜单树：POST /sys/menu/tree，返回带 children 的树形结构
export const menuTree = (data = {}) => {
  return request.post('/sys/menu/tree', data)
}

// 新增菜单：POST /sys/menu/addMenu
export const addMenu = (data) => {
  return request.post('/sys/menu/addMenu', data)
}

// 修改菜单：PUT /sys/menu/updateMenu
export const updateMenu = (data) => {
  return request.put('/sys/menu/updateMenu', data)
}

// 删除菜单：DELETE /sys/menu/deleteMenu/{menuId}
export const deleteMenu = (menuId) => {
  return request.delete(`/sys/menu/deleteMenu/${menuId}`)
}
