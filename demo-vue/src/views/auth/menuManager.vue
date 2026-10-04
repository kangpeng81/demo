<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { menuTree, listMenus, addMenu, updateMenu, deleteMenu } from '../../utils/menu'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

const queryForm = reactive({ title: '' })
const loading = ref(false)
const treeData = ref([])

const typeText = (type) => ({ 1: '目录', 2: '菜单', 3: '按钮' }[type] || '未知')
const typeTagType = (type) => ({ 1: 'warning', 2: 'primary', 3: 'info' }[type] || '')
const statusText = (status) => (status === 1 ? '显示' : '隐藏')

const fetchList = async () => {
  loading.value = true
  try {
    // 管理端用 tree 接口，返回带 children 的树
    const res = await menuTree({ title: queryForm.title || undefined })
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => fetchList()
const handleReset = () => {
  queryForm.title = ''
  fetchList()
}

// 扁平化菜单树，供「上级菜单」下拉使用（带层级缩进）
const flatMenus = ref([])
const flatten = (list, level = 0, acc = []) => {
  list.forEach((m) => {
    acc.push({ menuId: m.menuId, title: `${'　'.repeat(level)}${m.title || '(未命名)'}`, level })
    if (m.children?.length) flatten(m.children, level + 1, acc)
  })
  return acc
}
const loadFlatMenus = async () => {
  const res = await listMenus()
  const all = res.data || []
  // listMenus 是扁平列表，按 parentId 自行构树后拍平
  const map = new Map(all.map((m) => [m.menuId, { ...m, children: [] }]))
  const roots = []
  map.forEach((m) => {
    if (m.parentId && map.has(m.parentId)) {
      map.get(m.parentId).children.push(m)
    } else {
      roots.push(m)
    }
  })
  flatMenus.value = flatten(roots)
}

// 新增 / 编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref(null)
const form = reactive({
  menuId: null,
  parentId: 0,
  title: '',
  path: '',
  icon: '',
  type: 2,
  sort: 0,
  status: 1,
})
const rules = {
  title: [{ required: true, message: '请输入菜单标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
}

const openAdd = (row) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, {
    menuId: null,
    // 在某行点「新增子级」时，默认父级为该行；否则默认顶级
    parentId: row ? row.menuId : 0,
    title: '',
    path: '',
    icon: '',
    type: 2,
    sort: 0,
    status: 1,
  })
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, {
    menuId: row.menuId,
    parentId: row.parentId ?? 0,
    title: row.title || '',
    path: row.path || '',
    icon: row.icon || '',
    type: row.type ?? 2,
    sort: row.sort ?? 0,
    status: row.status ?? 1,
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.menuId) {
    await updateMenu({ ...form })
    ElMessage.success('修改成功')
  } else {
    await addMenu({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  fetchList()
  loadFlatMenus()
}

const handleDelete = (row) => {
  const hasChild = row.children?.length > 0
  ElMessageBox.confirm(
    `此操作将永久删除菜单「${row.title}」${hasChild ? `，且其下的 ${row.children.length} 个子菜单将一并失效` : ''}，且不可恢复。请确认`,
    '删除菜单',
    { type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteMenu(row.menuId)
      ElMessage.success('删除成功')
      fetchList()
      loadFlatMenus()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchList()
  loadFlatMenus()
})
</script>

<template>
  <div class="page">
    <h2>菜单管理</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="菜单标题">
          <el-input
            v-model="queryForm.title"
            placeholder="请输入菜单标题"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button v-if="hasPerm('menu:addMenu')" type="primary" @click="openAdd()">新增菜单</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="treeData"
        row-key="menuId"
        :tree-props="{ children: 'children' }"
        default-expand-all
        border
        style="width: 100%"
      >
        <el-table-column prop="title" label="菜单标题" min-width="200" />
        <el-table-column prop="path" label="路由路径" min-width="140" />
        <el-table-column prop="icon" label="图标" min-width="110" />
        <el-table-column prop="type" label="类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.type)" size="small">{{ typeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('menu:addMenu')" type="success" link size="small" @click="openAdd(row)">新增子级</el-button>
            <el-button v-if="hasPerm('menu:updateMenu')" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="hasPerm('menu:deleteMenu')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="上级菜单">
          <el-select v-model="form.parentId" placeholder="请选择上级菜单" style="width: 100%">
            <el-option :value="0" label="顶级菜单" />
            <el-option
              v-for="m in flatMenus"
              :key="m.menuId"
              :value="m.menuId"
              :label="m.title"
              :disabled="form.menuId !== null && m.menuId === form.menuId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入菜单标题" />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.path" placeholder="如 /auth/role（目录可为空）" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名，如 User" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option :value="1" label="目录" />
            <el-option :value="2" label="菜单" />
            <el-option :value="3" label="按钮" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="css" scoped>
.page {
  padding: 16px 24px;
}
h2 {
  margin: 0 0 16px;
}
.search-card,
.table-card {
  margin-bottom: 16px;
}
.search-card :deep(.el-form-item) {
  margin-bottom: 0;
}
.toolbar {
  margin-bottom: 12px;
}
</style>
