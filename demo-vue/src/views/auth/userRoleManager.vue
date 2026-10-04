<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUserRoles, addUserRole, deleteUserRole } from '../../utils/userRole'
import { listUsers } from '../../utils/user'
import { listRoles } from '../../utils/role'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

const queryForm = reactive({ userId: null, roleId: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const loading = ref(false)

// 当前页表格数据（服务端分页）
const tableList = ref([])
// 用户、角色选项，供搜索栏和新增弹窗的下拉使用
const userOptions = ref([])
const roleOptions = ref([])

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listUserRoles({
      userId: queryForm.userId ?? undefined,
      roleId: queryForm.roleId ?? undefined,
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
  // 下拉选项需要全量数据，传大 pageSize
  const [userRes, roleRes] = await Promise.all([
    listUsers({ page: 1, pageSize: 1000 }),
    listRoles({ page: 1, pageSize: 1000 }),
  ])
  userOptions.value = userRes.data?.list || []
  roleOptions.value = roleRes.data?.list || []
}

const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.userId = null
  queryForm.roleId = null
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
const form = reactive({ userId: null, roleId: null })

const openAdd = () => {
  Object.assign(form, { userId: null, roleId: null })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.userId || !form.roleId) {
    ElMessage.warning('请选择用户和角色')
    return
  }
  await addUserRole({ userId: form.userId, roleId: form.roleId })
  ElMessage.success('新增成功')
  dialogVisible.value = false
  fetchList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将解除用户「${row.userName || row.userId}」与角色「${row.roleName || row.roleId}」的关联，解除后该用户不再拥有此角色权限。请确认`,
    '解除用户角色关联',
    { type: 'warning', confirmButtonText: '确认解除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteUserRole(row.id)
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
    <h2>用户角色列表</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="用户">
          <el-select
            v-model="queryForm.userId"
            placeholder="全部用户"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option
              v-for="u in userOptions"
              :key="u.userId"
              :value="u.userId"
              :label="u.userName"
            />
          </el-select>
        </el-form-item>
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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button v-if="hasPerm('userRole:addUserRole')" type="primary" @click="openAdd">新增关联</el-button>
      </div>
      <el-table v-loading="loading" :data="tableList" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="userName" label="用户名" min-width="140" />
        <el-table-column prop="roleId" label="角色ID" width="100" />
        <el-table-column prop="roleName" label="角色名" min-width="140" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('userRole:deleteUserRole')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增用户角色关联" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户">
          <el-select v-model="form.userId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option
              v-for="u in userOptions"
              :key="u.userId"
              :value="u.userId"
              :label="u.userName"
            />
          </el-select>
        </el-form-item>
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
