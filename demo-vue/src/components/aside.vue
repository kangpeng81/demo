<template>
  <el-row class="tac">
    <el-col :span="24">
      <h5 class="mb-2">{{ isCollapse ? 'DIDI' : 'DIDI培诊' }}</h5>
      <el-menu active-text-color="#ffd04b" background-color="#545c64" class="el-menu-vertical-demo" default-active="2"
        text-color="#fff" @open="handleOpen" @close="handleClose">
        <MenuItem v-for="item in menuData" :key="item.path" :item="item" />
      </el-menu>
    </el-col>
  </el-row>
</template>

<script setup>
import {
  Monitor,
  Grid,
  Memo,
  Menu,
  User,
  Tickets,
  UserFilled,
  Connection,
  Share,
} from '@element-plus/icons-vue'
import MenuItem from './MenuItem.vue'

// 折叠状态由父组件(布局层)传入，控制 el-menu 是否折叠
defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
})

// 菜单数据：type 为 submenu(子菜单) / group(分组)，无 type 即普通菜单项
// icon 直接放图标组件，disabled 控制禁用
const menuData = [
  {
    index: '1',
    title: '控制台',
    icon: Monitor,
  },
  {
    index: '2',
    title: '系统管理',
    icon: Grid,
    type: 'submenu',
    children: [
      {
        index: '2-1',
        icon: UserFilled,
        title: '用户管理',
      },
      {
        index: '2-2',
        icon: User,
        title: '角色管理',
      },
      {
        index: '2-3',
        icon: Menu,
        title: '菜单管理',
      },
      {
        index: '2-4',
        icon: Connection,
        title: '用户角色列表',
      },
      {
        index: '2-5',
        icon: Share,
        title: '角色菜单',
      },
    ],
  },
  {
    index: '3',
    title: 'DIDI培诊',
    icon: Tickets,
    type: 'submenu',
    children: [
      {
        index: '3-1',
        icon: User,
        title: '陪护管理',
      },
      {
        index: '3-2',
        icon: Tickets,
        title: '订单管理',
      },
    ],
  },
]

const handleOpen = (key, keyPath) => {
  console.log(key, keyPath)
}
const handleClose = (key, keyPath) => {
  console.log(key, keyPath)
}
</script>

<style scoped>
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
