<script setup>
// 递归菜单节点组件：根据 item.type 渲染分组 / 子菜单 / 普通菜单项，支持任意层级嵌套
defineProps({
  item: {
    type: Object,
    required: true,
  },
})

// 叶子项被点击时向上抛出被点的 item；子层 MenuItem 的点击也逐层向上转发到 Home
const emit = defineEmits(['item-click'])
const onClick = (clickedItem) => {
  emit('item-click', clickedItem)
}
</script>

<template>
  <!-- 分组 -->
  <el-menu-item-group v-if="item.type === 'group'" :title="item.title">
    <MenuItem
      v-for="child in item.children"
      :key="child.path"
      :item="child"
      @item-click="onClick"
    />
  </el-menu-item-group>

  <!-- 子菜单（可继续嵌套）：Element Plus 的 :index 是组件固定 API，绑定层级编号 item.index -->
  <el-sub-menu v-else-if="item.type === 'submenu'" :index="item.index">
    <template #title>
      <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
      <span>{{ item.title }}</span>
    </template>
    <MenuItem
      v-for="child in item.children"
      :key="child.path"
      :item="child"
      @item-click="onClick"
    />
  </el-sub-menu>

  <!-- 普通菜单项（叶子）：点击抛出自身 item；:index 绑定层级编号 item.index，
       el-menu 的 :default-active="activeIndex" 按这个值匹配来显示黄色高亮 -->
  <el-menu-item v-else :index="item.index" :disabled="item.disabled" @click="onClick(item)">
    <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
    <span>{{ item.title }}</span>
  </el-menu-item>
</template>
