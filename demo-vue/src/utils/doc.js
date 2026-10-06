import request from '../utils/request'

// 文档列表（区域过滤 + Casbin 五维记录过滤，返回当前用户可见的文档）：POST /sys/doc/list
export const listDocs = (data = {}) => {
  return request.post('/sys/doc/list', data)
}

// 权限判定试算：GET /sys/doc/check/{docId}/{act}，返回 { allowed, act, docType, docLevel }
export const checkPerm = (docId, act) => {
  return request.get(`/sys/doc/check/${docId}/${act}`)
}

// 显式授权（属性级范围）：POST /sys/doc/grant，body: { targetUserId, acts, deptId, docLevels, objs, resIds }
// 仅超管或目标部门领导可授权；docLevels/objs/resIds 为空表示全部
// （objs 资源类型：doc 文档 / contract 合同；resIds 资源实例ID集合，指定时精确到单条资源）
export const grantDoc = (data) => {
  return request.post('/sys/doc/grant', data)
}

// 收回目标用户的全部显式授权：POST /sys/doc/revoke，body: { targetUserId }
export const revokeDoc = (targetUserId) => {
  return request.post('/sys/doc/revoke', { targetUserId })
}

// 部门列表：GET /sys/dept/list
export const listDepts = () => {
  return request.get('/sys/dept/list')
}
