<script setup>
import { reactive, ref, watchEffect, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HeadMenu from '../components/head.vue'
import MenuItem from '../components/MenuItem.vue'
import { getUserMenus } from '../utils/user'
const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态：true = 已折叠。由布局层持有，head 触发切换，aside 读取
const isCollapse = ref(false)
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}
const menuList = reactive([])
// 当前激活的菜单 index（1 / 2-1 / 2-2 这样的层级编号），用于 el-menu 的 default-active 高亮叶子项
// 以及 head 里通过 item.index === activeIndex 判断对应标签是否蓝色高亮
const activeIndex = ref('')

// 菜单数据：登录后按当前用户角色从后端动态加载（/sys/user/menus）
// 节点字段：index 层级编号 / path 真实路由 / type=submenu 目录 / icon 图标组件名字符串（已全局注册）
const menuData = ref([])

// 后端部分叶子菜单 path 可能无前导“/”（如 auth/role），统一补全为绝对路由路径，
// 目录节点不参与跳转，保持原值即可
const normalizeMenus = (nodes) => {
  return (nodes || []).map((node) => {
    const item = { ...node }
    if (item.path && item.type !== 'submenu' && !item.path.startsWith('/')) {
      item.path = '/' + item.path
    }
    if (item.children) {
      item.children = normalizeMenus(item.children)
    }
    return item
  })
}

const fetchMenus = async () => {
  const res = await getUserMenus()
  menuData.value = normalizeMenus(res.data)
}

const handleOpen = (key, keyPath) => {
  console.log(key, keyPath)
}
const handleClose = (key, keyPath) => {
  console.log(key, keyPath)
}

// MenuItem 叶子项点击后逐层 emit 上来：把该 item 存入 menuList（按 path 去重，path 全局唯一）
const handleMenuClick = (item) => {
  const exist = menuList.some((m) => m.path === item.path)
  if (!exist) {
    menuList.push(item)
  }
  // 高亮：activeIndex 存层级编号（item.index），el-menu 通过 default-active 匹配到对应子项
  activeIndex.value = item.index
  // 路由跳转：用 item.path（真实路由路径）
  if (route.path !== item.path) {
    router.push(item.path)
  }
  console.log('menuList', JSON.stringify(menuList.map((m) => ({ index: m.index, path: m.path, title: m.title }))))
}

// 根据 path 在菜单树中递归查找对应的菜单项（用于按路由路径反查 item）
const findItemByPath = (list, path) => {
  for (const item of list) {
    if (item.path === path) return item
    if (item.children) {
      const found = findItemByPath(item.children, path)
      if (found) return found
    }
  }
  return null
}

// 路由变化时：自动把当前页面对应的菜单项加入 menuList，并同步 activeIndex
// 这样首屏进入、刷新页面、或直接敲 URL 进来，标签与高亮都能自动对齐
watchEffect(() => {
  const fullPath = route.path
  const item = findItemByPath(menuData.value, fullPath)
  if (!item) return
  const exist = menuList.some((m) => m.path === item.path)
  if (!exist) {
    menuList.push(item)
  }
  activeIndex.value = item.index
})

onMounted(fetchMenus)


// HeadMenu 点击关闭按钮后，从 menuList 中移除该 item
const handleRemoveMenu = (item) => {
  const removedIdx = menuList.indexOf(item)
  menuList.splice(removedIdx, 1)
  // 若关掉的正是当前激活项（按 item.index 判断），则把激活态移到它后一个标签；
  // 若删的是最末尾（后面没有了），则退回前一个；都没有则清空
  if (activeIndex.value === item.index) {
    const next = menuList[removedIdx] || menuList[removedIdx - 1]
    activeIndex.value = next ? next.index : ''
    // 同步跳转对应路由，避免 URL 和激活的标签不一致。
    // 放 nextTick：让 DOM / 响应式状态先稳定下来，再触发路由导航。
    // 相同路径的 push 在 Vue Router 4 中会被直接 resolve，不会产生 NavigationDuplicated 报错。
    if (next) {
      nextTick(() => {
        router.push(next.path).catch(() => { /* 静默忽略相同路径的取消 / 重复导航 */ })
      })
    }
  }
  console.log('menuList', JSON.stringify(menuList.map((m) => ({ index: m.index, path: m.path, title: m.title }))))
}
</script>


<template>
  <div class="common-layout">
    <el-container>
      <el-aside class="aside" :class="{ 'is-collapse': isCollapse }">
        <el-row class="tac">
          <el-col :span="24">
            <h5 class="mb-2">{{ isCollapse ? 'DIDI' : 'DIDI培诊' }}</h5>
            <el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo"
              text-color="#fff" :collapse="isCollapse" :default-active="activeIndex" @open="handleOpen" @close="handleClose">
              <MenuItem v-for="item in menuData" :key="item.path" :item="item" @item-click="handleMenuClick" />
            </el-menu>
          </el-col>
        </el-row>
      </el-aside>
      <el-container>
        <el-header>
            <HeadMenu  :is-collapse="isCollapse" @toggle-collapse="toggleCollapse" :menu-list="menuList" :active-index="activeIndex" @remove-menu="handleRemoveMenu" @select-menu="handleMenuClick"/>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="css" scoped>
.common-layout {
  height: 100vh;
}
/* el-container 默认不占满父级高度，显式撑满，才能让子级 el-aside 的 100% 生效 */
.common-layout > .el-container {
  height: 100%;
}
.aside {
  width: 200px;
  max-width: 300px;
  height: 100%;
  transition: width 0.3s;
}
/* 折叠时收窄侧栏，宽度与 el-menu collapse 的默认宽度(100px)一致 */
.aside.is-collapse {
  width: 100px;
}

/* ↓↓↓ 以下为原 aside.vue 的侧栏样式 ↓↓↓ */
.tac {
  height: 100%;
  background-color: #545c64;
}

/* el-col 撑满父级高度，并用 flex 纵向布局，让菜单吃掉标题以外的剩余高度 */
.tac>.el-col {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 标题也在深色底上，文字改成浅色 */
.mb-2 {
  margin: 0;
  padding: 16px 20px;
  color: #fff;
}

/* 菜单占据剩余全部高度，深色背景才能铺满整个侧栏 */
.el-menu-vertical-demo {
  flex: 1;
  border-right: none;
}
</style>
