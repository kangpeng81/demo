import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { useUserStore } from './stores/user'

const app = createApp(App)
app.use(createPinia())
app.use(ElementPlus)
app.use(router)

// 全局权限指令：v-perm="'xxx'" 无对应权限时移除元素
app.directive('perm', {
  mounted(el, binding) {
    const userStore = useUserStore()
    const code = binding.value
    if (code && !userStore.hasPerm(code)) {
      el.parentNode?.removeChild(el)
    }
  }
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.mount('#app')
