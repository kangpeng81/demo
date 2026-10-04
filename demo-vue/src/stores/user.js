import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getUserPerms } from '../utils/user'

// 用户登录态：存登录返回的数据（username、token 等），供 head 等组件读取
// 后端登录返回结构：{ code, msg, data: null, token, tokenName, user: { id, userName, ... } }
export const useUserStore = defineStore('user', () => {
  const username = ref('')
  const userInfo = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  // 当前用户拥有的按钮权限点列表（来自 /sys/user/perms，type=3 菜单的 path 集合）
  const perms = ref([])

  // 写入登录返回数据；兼容两种形态：
  // 1) 登录接口返回 { token, user: {...} }
  // 2) 旧格式 { data: { token, userName, ... } } 或直接是用户对象
  const setUser = (data) => {
    const user = data?.user ?? data?.data ?? data
    userInfo.value = user
    // 后端返回字段名是 userName（驼峰），优先取 userName，兜底 username
    username.value = user?.userName || user?.username || ''
    const tokenValue = data?.token ?? user?.token
    if (tokenValue) {
      token.value = tokenValue
      localStorage.setItem('token', tokenValue)
    }
  }

  // 拉取并存储当前用户的按钮权限点
  const loadPerms = async () => {
    try {
      const res = await getUserPerms()
      perms.value = res?.data || []
    } catch (e) {
      perms.value = []
    }
  }

  // 是否拥有某个按钮权限点（如 'user:addUser'）
  const hasPerm = (code) => {
    if (!code) return true
    if (!perms.value || perms.value.length === 0) return false
    return perms.value.includes(code)
  }

  const logout = () => {
    username.value = ''
    userInfo.value = null
    token.value = ''
    perms.value = []
    localStorage.removeItem('token')
  }

  return { username, userInfo, token, perms, setUser, loadPerms, hasPerm, logout }
})
