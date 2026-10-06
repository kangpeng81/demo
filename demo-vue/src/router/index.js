import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/',
    component: () => import('../views/Home.vue'),
    redirect: '/console',
    children: [
      {
        path: 'console',
        name: 'console',
        component: () => import('../views/console/Index.vue'),
        meta: { title: '控制台' },
      },
      {
        path: 'auth/account',
        name: 'accountManger',
        component: () => import('../views/auth/accountManger.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'auth/role',
        name: 'roleManager',
        component: () => import('../views/auth/roleManager.vue'),
        meta: { title: '角色管理' },
      },
      {
        path: 'auth/menu',
        name: 'menuManager',
        component: () => import('../views/auth/menuManager.vue'),
        meta: { title: '菜单管理' },
      },
      {
        path: 'auth/userRole',
        name: 'userRoleManager',
        component: () => import('../views/auth/userRoleManager.vue'),
        meta: { title: '用户角色列表' },
      },
      {
        path: 'auth/roleMenu',
        name: 'roleMenuManager',
        component: () => import('../views/auth/roleMenuManager.vue'),
        meta: { title: '角色菜单列表' },
      },
      {
        path: 'auth/docPerm',
        name: 'docPermManager',
        component: () => import('../views/auth/docPermManager.vue'),
        meta: { title: '文档权限管理' },
      },

    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/Login.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 全局前置守卫：未登录访问受保护页面时跳转到 /login
// 判断依据：store 中的 token（登录时写入、退出时清空、刷新页面时由 App.vue 重新加载 perms 时仍保留）
const WHITE_LIST = ['/login']

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  if (hasToken) {
    // 已登录还想去登录页，直接送到首页
    if (to.path === '/login') {
      next('/')
      return
    }
    next()
    return
  }

  // 未登录
  if (WHITE_LIST.includes(to.path)) {
    next()
    return
  }
  // 把目标地址带到 login 页，登录成功后可回跳
  next({ path: '/login', query: to.fullPath !== '/' ? { redirect: to.fullPath } : {} })
})

export default router
