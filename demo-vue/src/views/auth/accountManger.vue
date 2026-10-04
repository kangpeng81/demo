<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUsers, addUser, updateUser, deleteUser } from '../../utils/user'
import { listRoles } from '../../utils/role'
import { assignRoles, getRoleIdsByUserId } from '../../utils/userRole'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

const queryForm = reactive({ userName: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const loading = ref(false)

// 当前页表格数据（服务端分页）
const tableList = ref([])

// 全部可分配角色
const roleOptions = ref([])
// 角色ID → 角色名 映射，用于列表展示
const roleMap = computed(() => {
  const map = {}
  roleOptions.value.forEach((r) => { map[r.roleId] = r.roleName })
  return map
})

const fetchRoles = async () => {
  // 下拉选项需要全量角色，传大 pageSize
  const res = await listRoles({ page: 1, pageSize: 1000 })
  roleOptions.value = res.data?.list || []
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listUsers({
      userName: queryForm.userName || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    const users = res.data?.list || []
    // 并发查询每个用户已分配的角色，挂上 roleNames 供列表展示
    await Promise.all(users.map(async (u) => {
      const r = await getRoleIdsByUserId(u.userId)
      const ids = r.data || []
      u.roleIds = ids
      u.roleNames = ids.map((id) => roleMap.value[id]).filter(Boolean)
    }))
    tableList.value = users
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchList()
}

const handleReset = () => {
  queryForm.userName = ''
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

// 新增 / 编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref(null)
const form = reactive({
  userId: null,
  userName: '',
  password: '',
  nickname: '',
  name: '',
  phone: '',
  email: '',
  age: null,
  status: 1,
  roleIds: [],
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const resetForm = () => {
  Object.assign(form, {
    userId: null,
    userName: '',
    password: '',
    nickname: '',
    name: '',
    phone: '',
    email: '',
    age: null,
    status: 1,
    roleIds: [],
  })
}

const openAdd = async () => {
  dialogTitle.value = '新增用户'
  resetForm()
  if (!roleOptions.value.length) {
    await fetchRoles()
  }
  dialogVisible.value = true
}

const openEdit = async (row) => {
  dialogTitle.value = '编辑用户'
  if (!roleOptions.value.length) {
    await fetchRoles()
  }
  Object.assign(form, {
    userId: row.userId,
    userName: row.userName,
    password: '',
    nickname: row.nickname || '',
    name: row.name || '',
    phone: row.phone || '',
    email: row.email || '',
    age: row.age ?? null,
    status: row.status ?? 1,
  })
  // 回显该用户已分配的角色
  const res = await getRoleIdsByUserId(row.userId)
  form.roleIds = res.data || []
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  let userId = form.userId
  if (userId) {
    // 编辑：后端 updateUser 仅更新昵称/姓名/手机/邮箱/年龄/状态（用户名不可改）
    await updateUser({
      userId,
      nickname: form.nickname,
      name: form.name,
      phone: form.phone,
      email: form.email,
      age: form.age,
      status: form.status,
    })
    ElMessage.success('修改成功')
  } else {
    const res = await addUser({
      userName: form.userName,
      password: form.password,
      nickname: form.nickname,
      name: form.name,
      phone: form.phone,
      email: form.email,
      age: form.age,
      status: form.status,
    })
    userId = res.data?.userId
    ElMessage.success('新增成功')
  }
  // 覆盖式分配角色（可多选，可为空）
  await assignRoles(userId, form.roleIds || [])
  dialogVisible.value = false
  fetchList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将永久删除用户「${row.userName}」，且不可恢复。请确认`,
    '删除用户',
    { type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteUser(row.userId)
      ElMessage.success('删除成功')
      fetchList()
    })
    .catch(() => {})
}

const statusText = (status) => (status === 1 ? '正常' : '禁用')

onMounted(async () => {
  await fetchRoles()
  fetchList()
})
</script>

<template>
  <div class="page">
    <h2>用户管理</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="用户名">
          <el-input
            v-model="queryForm.userName"
            placeholder="请输入用户名"
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
        <el-button v-if="hasPerm('user:addUser')" type="primary" @click="openAdd">新增用户</el-button>
      </div>
      <el-table v-loading="loading" :data="tableList" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userName" label="用户名" min-width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="100" />
        <el-table-column prop="name" label="真实姓名" min-width="100" />
        <el-table-column label="角色" min-width="140">
          <template #default="{ row }">
            <el-tag
              v-for="name in row.roleNames"
              :key="name"
              size="small"
              type="warning"
              class="role-tag"
            >
              {{ name }}
            </el-tag>
            <span v-if="!row.roleNames || !row.roleNames.length" class="role-empty">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="age" label="年龄" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('user:updateUser')" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="hasPerm('user:deleteUser')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" :disabled="!!form.userId" />
        </el-form-item>
        <el-form-item v-if="!form.userId" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分配角色">
          <el-select
            v-model="form.roleIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="请选择角色（可多选）"
            style="width: 100%"
          >
            <el-option
              v-for="role in roleOptions"
              :key="role.roleId"
              :label="role.roleName"
              :value="role.roleId"
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
.role-tag {
  margin-right: 4px;
  margin-bottom: 2px;
}
.role-empty {
  color: var(--el-text-color-placeholder);
}
</style>
