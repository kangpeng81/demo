<template>
  <div class="head">

    <div class="left">
      <div class="collapse" @click="emit('toggle-collapse')"> <!-- 你需要直接提供它们 -->
        <component :is="isCollapse ? Expand : Fold" style="width: 1em; height: 1em" />
      </div>
      <div class="menuOptions">
        <div
          class="menu-item"
          :class="{ active: item.index === activeIndex }"
          v-for="item in menuList"
          :key="item.path"
          @click="emit('select-menu', item)"
        >
          <component :is="item.icon" v-if="item.icon" style="width: 1em; height: 1em; margin-right: 8px" />
          <span>{{ item.title }}</span>
          <Close class="close-icon" @click.stop="emit('remove-menu', item)" />
        </div>
      </div>
    </div>
    <div class="user">
        <img :src="card1" class="user-icon" alt="card" /> {{ userStore.username || 'Admin' }}
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            <ArrowDown style="width: 1em; height: 1em; margin-left: 6px" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
    </div>


  </div>
</template>

<script setup>
import { Expand, Fold, Close, ArrowDown } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import card1 from '../images/card_1.png';
import { useUserStore } from '../stores/user';
import { logout } from '../utils/user';

const userStore = useUserStore();
const router = useRouter();

defineProps({
  isCollapse: {
    type: Boolean,
    default: false,
  },
  menuList: {
    type: Array,
    default: () => [],
  },
  activeIndex: {
    type: String,
    default: '',
  },
});
const emit = defineEmits(['toggle-collapse', 'remove-menu', 'select-menu']);

const handleCommand = async (cmd) => {
  if (cmd !== 'logout') return;
  try {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' });
    // 调后端清除 sa-token 会话（使服务端 token 立即失效）
    await logout().catch(() => {});
    userStore.logout();
    ElMessage.success('已退出登录');
    router.push('/login');
  } catch (e) {
    // 用户点取消，不做任何操作
  }
};
</script>

<style scoped>
.head {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}
.left {
  display: flex;
  align-items: center;
}
.menuOptions {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.menu-item {
  display: flex;
  align-items: center;
  margin-right: 6px;
  white-space: nowrap;
  padding: 2px 8px;
  border-radius: 4px;
  cursor: pointer;
}
/* 当前激活的标签高亮为蓝色 */
.menu-item.active {
  color: #409eff;
  background-color: #ecf5ff;
}
.close-icon {
  width: 1em;
  height: 1em;
  margin-left: 6px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}
/* 鼠标悬停在菜单项上时才显示关闭图标 */
.menu-item:hover .close-icon {
  opacity: 1;
}
.collapse {
  display: flex;
  align-items: center;
  font-size: 20px;
  margin: 0px 8px 0px 8px;
  cursor: pointer;
}
.user {
  display: flex;
  align-items: center;
  margin-right: 20px;
  cursor: pointer;
  height: 1em;
}
.user-icon {
  width: 2em;
  height: 2em;
  margin-right: 8px;
  object-fit: contain;
}
.el-dropdown-link {
  display: inline-flex;
  align-items: center;
  outline: none;
}
</style>
