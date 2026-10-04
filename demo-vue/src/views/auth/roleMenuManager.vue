<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRoleMenus, addRoleMenu, deleteRoleMenu } from '../../utils/roleMenu'
import { listRoles } from '../../utils/role'
import { listMenus } from '../../utils/menu'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

const queryForm = reactive({ roleId: null, menuId: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const loading = ref(false)

// 当前页表格数据（服务端分页）
const tableList = ref([])
const roleOptions = ref([])
// 菜单下拉：扁平列表，按父级缩进展示
const menuOptions = ref([])

// 把扁平菜单构树后拍平，生成带层级缩进的选项
const buildMenuOptions = (all) => {
  const map = new Map(all.map((m) => [m.menuId, { ...m, children: [] }]))
  const roots = []
  map.forEach((m) => {
    if (m.parentId && map.has(m.parentId)) {
      map.get(m.parentId).children.push(m)
    } else {
      roots.push(m)
    }
  })
  const acc = []
  const walk = (list, level) => {
    list.forEach((m) => {
      acc.push({ menuId: m.menuId, title: `${'　'.repeat(level)}${m.title || '(未命名)'}` })
      if (m.children.length) walk(m.children, level + 1)
    })
  }
  walk(roots, 0)
  return acc
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listRoleMenus({
      roleId: queryForm.roleId ?? undefined,
      menuId: queryForm.menuId ?? undefined,
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    tableList.value = res.data?.list || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const fetchOptions = async () => {
  // 角色下拉需要全量；菜单 list 接口本身不分页（树/选项用全量）
  const [roleRes, menuRes] = await Promise.all([
    listRoles({ page: 1, pageSize: 1000 }),
    listMenus(),
  ])
  roleOptions.value = roleRes.data?.list || []
  menuOptions.value = buildMenuOptions(menuRes.data || [])
}

const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.roleId = null
  queryForm.menuId = null
  pagination.page = 1
  fetchList()
}

const handlePageChange = (p) => {
  pagination.page = p
  fetchList()
}

const handleSizeChange = (s) => {
  pagination.pageSize = s
  pagination.page = 1
  fetchList()
}

const dialogVisible = ref(false)
const form = reactive({ roleId: null, menuId: null })

const openAdd = () => {
  Object.assign(form, { roleId: null, menuId: null })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.roleId || !form.menuId) {
    ElMessage.warning('请选择角色和菜单')
    return
  }
  await addRoleMenu({ roleId: form.roleId, menuId: form.menuId })
  ElMessage.success('新增成功')
  dialogVisible.value = false
  fetchList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将解除角色「${row.roleName || row.roleId}」与菜单「${row.menuName || row.menuId}」的关联，解除后该角色不再拥有此菜单权限。请确认`,
    '解除角色菜单关联',
    { type: 'warning', confirmButtonText: '确认解除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteRoleMenu(row.id)
      ElMessage.success('删除成功')
      fetchList()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchOptions()
  fetchList()
})
</script>

<template>
  <div class="page">
    <h2>角色菜单列表</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="角色">
          <el-select
            v-model="queryForm.roleId"
            placeholder="全部角色"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="r in roleOptions"
              :key="r.roleId"
              :value="r.roleId"
              :label="r.roleName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单">
          <el-select
            v-model="queryForm.menuId"
            placeholder="全部菜单"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="m in menuOptions"
              :key="m.menuId"
              :value="m.menuId"
              :label="m.title"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button v-if="hasPerm('roleMenu:addRoleMenu')" type="primary" @click="openAdd">新增关联</el-button>
        <el-button v-perm="'roleMenu:addRoleMenu'" type="primary" @click="openAdd">新增关联</el-button>
      </div>
      <el-table v-loading="loading" :data="tableList" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleId" label="角色ID" width="100" />
        <el-table-column prop="roleName" label="角色名" min-width="140" />
        <el-table-column prop="menuId" label="菜单ID" width="100" />
        <el-table-column prop="menuName" label="菜单名" min-width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('roleMenu:deleteRoleMenu')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-info">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增角色菜单关联" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色">
          <el-select v-model="form.roleId" placeholder="请选择角色" filterable style="width: 100%">
            <el-option
              v-for="r in roleOptions"
              :key="r.roleId"
              :value="r.roleId"
              :label="r.roleName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单">
          <el-select v-model="form.menuId" placeholder="请选择菜单" filterable style="width: 100%">
            <el-option
              v-for="m in menuOptions"
              :key="m.menuId"
              :value="m.menuId"
              :label="m.title"
            />
          </el-select>
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
.pagination-info {
  margin-top: 12px;
  text-align: right;
}
</style>
