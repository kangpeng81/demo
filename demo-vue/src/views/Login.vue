<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import loginHead from '../images/login-head.png'
import { login } from '../utils/user'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 登录表单：后端登录接口入参 { userName, password }
const form = reactive({
  userName: '',
  password: '',
})

const onSubmit = async () => {
  if (!form.userName || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    // 后端返回 { code, msg, token, tokenName, user }
    const res = await login({
      userName: form.userName,
      password: form.password,
    })
    userStore.setUser(res)
    // 拉取当前用户按钮权限点，供各页面 v-if 控制按钮显示
    await userStore.loadPerms()
    ElMessage.success('登录成功')
    // 若是路由守卫因未登录拦截跳来，登录成功后回跳原目标；否则默认进首页
    const redirect = route.query.redirect
    router.push(redirect || '/')
  } catch (e) {
    // 错误提示已在 request.js 响应拦截器里统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-container">

    <el-form :model="form" label-width="auto" class="login-form" @submit.prevent="onSubmit">
      <div class="foxCenter">
        <el-image :src="loginHead" fit="contain" />
      </div>
      <el-form-item>
        <el-input v-model="form.userName" placeholder="用户名" />
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="form.password"
          type="password"
          show-password
          placeholder="密码"
          @keyup.enter="onSubmit"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" class="submit-btn" :loading="loading" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form>

  </div>
</template>

<style scoped>
.foxCenter {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 280px;
  background-color: grey;
  margin-bottom: 5px;
}

.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
}

.login-form {
  width: 280px;
}

.submit-btn {
  width: 100%;
}
</style>
