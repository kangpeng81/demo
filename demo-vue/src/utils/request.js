import axios from 'axios'
import { ElMessage } from 'element-plus'

// axios 封装：baseURL 用 /api，由 vite.config.js 的 proxy 代理转发到 http://localhost:8080
// 开发环境用代理绕过浏览器 CORS 限制
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：sa-token 默认从名为 satoken 的请求头读取 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.satoken = token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：
// - 成功（HTTP 2xx）：返回后端 ResultUtils 整体（{ code, msg, data }），业务码非 200 时统一弹错
// - 失败（HTTP 4xx/5xx）：优先取后端返回的 JSON msg；401 时清登录态并跳登录页
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' && 'code' in res && res.code !== 200) {
      if (res.code === 401) {
        handleUnauthorized()
      } else {
        ElMessage.error(res.msg || '请求失败')
      }
      return Promise.reject(new Error(res.msg || 'Business Error'))
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    const data = error.response?.data
    if (status === 401) {
      handleUnauthorized()
    } else {
      ElMessage.error(data?.msg || data?.message || error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

// 401 处理：清除本地失效 token，跳转登录页（避免在登录页重复跳转）
const handleUnauthorized = () => {
  localStorage.removeItem('token')
  ElMessage.error('未登录或登录已过期，请重新登录')
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

export default request
