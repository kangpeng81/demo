<script setup>
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

// 应用启动时若已登录但 perms 未加载（如刷新页面），重新拉取按钮权限点
onMounted(() => {
  if (userStore.token && userStore.perms.length === 0) {
    userStore.loadPerms()
  }
})
</script>

<template>
  <router-view />
</template>
