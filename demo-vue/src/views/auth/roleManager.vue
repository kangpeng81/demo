<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRoles, addRole, updateRole, deleteRole } from '../../utils/role'
import { menuTree } from '../../utils/menu'
import { assignMenus, getMenuIdsByRoleId } from '../../utils/roleMenu'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

const queryForm = reactive({ roleName: '' })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const loading = ref(false)
// 当前页表格数据（服务端分页）
const tableList = ref([])

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listRoles({
      roleName: queryForm.roleName || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize,
    })
    tableList.value = res.data?.list || []
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
  queryForm.roleName = ''
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

// 菜单权限树
const menuTreeData = ref([])
const menuTreeRef = ref(null)
// 弹窗打开后待回显的勾选节点（仅叶子，父节点由 el-tree 自动计算全选/半选）
const pendingCheckedIds = ref([])

const fetchMenuTree = async () => {
  const res = await menuTree()
  menuTreeData.value = res.data || []
}

// 收集树中所有叶子节点 menuId
const collectLeafIds = (nodes, acc = []) => {
  nodes.forEach((node) => {
    if (node.children && node.children.length) {
      collectLeafIds(node.children, acc)
    } else {
      acc.push(node.menuId)
    }
  })
  return acc
}

// 弹窗打开动画结束、el-tree 渲染完成后回显
const onDialogOpened = () => {
  menuTreeRef.value?.setCheckedKeys(pendingCheckedIds.value)
}

// 新增 / 编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref(null)
const form = reactive({ roleId: null, roleName: '', roleCode: '', remark: '' })
const rules = {
  roleName: [{ required: true, message: '请输入角色名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
}

const openAdd = async () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, { roleId: null, roleName: '', roleCode: '', remark: '' })
  if (!menuTreeData.value.length) {
    await fetchMenuTree()
  }
  pendingCheckedIds.value = []
  dialogVisible.value = true
}

const openEdit = async (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, {
    roleId: row.roleId,
    roleName: row.roleName,
    roleCode: row.roleCode,
    remark: row.remark || '',
  })
  if (!menuTreeData.value.length) {
    await fetchMenuTree()
  }
  // 查询角色已有菜单，只回显其中的叶子节点（父级自动算出全选/半选状态）
  const res = await getMenuIdsByRoleId(row.roleId)
  const ownedIds = res.data || []
  const leafIds = collectLeafIds(menuTreeData.value)
  pendingCheckedIds.value = leafIds.filter((id) => ownedIds.includes(id))
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  // 完全勾选 + 半选（父级目录）一起提交，保证用户菜单层级完整
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  const menuIds = [...checkedKeys, ...halfCheckedKeys]

  let roleId = form.roleId
  if (roleId) {
    await updateRole({ roleId, roleName: form.roleName, roleCode: form.roleCode, remark: form.remark })
    ElMessage.success('更新成功')
  } else {
    const res = await addRole({ roleName: form.roleName, roleCode: form.roleCode, remark: form.remark })
    roleId = res.data?.roleId
    ElMessage.success('新增成功')
  }
  await assignMenus(roleId, menuIds)
  dialogVisible.value = false
  fetchList()
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `此操作将永久删除角色「${row.roleName}」，且不可恢复。请确认`,
    '删除角色',
    { type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await deleteRole(row.roleId)
      ElMessage.success('删除成功')
      fetchList()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchList()
  fetchMenuTree()
})
</script>

<template>
  <div class="page">
    <h2>角色管理</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="角色名">
          <el-input
            v-model="queryForm.roleName"
            placeholder="请输入角色名"
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
        <el-button v-if="hasPerm('role:addRole')" type="primary" @click="openAdd">新增角色</el-button>
      </div>
      <el-table v-loading="loading" :data="tableList" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名" min-width="120" />
        <el-table-column prop="roleCode" label="角色编码" min-width="120" />
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="hasPerm('role:updateRole')" type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="hasPerm('role:deleteRole')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="560px"
      @opened="onDialogOpened"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="备注" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <div class="menu-tree-box">
            <el-tree
              ref="menuTreeRef"
              :data="menuTreeData"
              node-key="menuId"
              show-checkbox
              default-expand-all
              :props="{ label: 'title', children: 'children' }"
            />
          </div>
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
.menu-tree-box {
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  padding: 8px 12px;
  box-sizing: border-box;
}
</style>
