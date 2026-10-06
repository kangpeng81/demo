<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDocs, checkPerm, grantDoc, revokeDoc, listDepts } from '../../utils/doc'
import { listUsers } from '../../utils/user'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const hasPerm = (code) => userStore.hasPerm(code)

// 资源类型/密级/操作 的展示映射（obj 为 Casbin 资源类型维度，可扩展）
const OBJ_MAP = { doc: '文档', contract: '合同' }
const LEVEL_MAP = { 1: '公开', 2: '部门', 3: '部门机密', 4: '区域' }
const ACTS = ['read', 'write', 'delete', 'grant']
const ACT_LABEL = { read: '读', write: '写', delete: '删', grant: '授权' }

const queryForm = reactive({ title: '', obj: '', docLevel: null })
const pagination = reactive({ page: 1, pageSize: 10, total: 0 })
const loading = ref(false)
const tableList = ref([])

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listDocs({
      title: queryForm.title || undefined,
      obj: queryForm.obj || undefined,
      docLevel: queryForm.docLevel ?? undefined,
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
  Object.assign(queryForm, { title: '', obj: '', docLevel: null })
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

// ===== 权限试算 =====
const checkVisible = ref(false)
const checkDoc = ref(null)
const checkRows = ref([])
const checkLoading = ref(false)

const openCheck = (row) => {
  checkDoc.value = row
  checkVisible.value = true
  checkLoading.value = true
  checkRows.value = ACTS.map((act) => ({ act, label: ACT_LABEL[act], allowed: null }))
  Promise.all(
    ACTS.map((act) =>
      checkPerm(row.docId, act)
        // check 接口的 allowed 挂在 ResultUtils 顶层（.put("allowed")），不在 data 里
        .then((res) => ({ act, allowed: res.allowed ?? res.data?.allowed }))
        .catch(() => ({ act, allowed: null }))
    )
  ).then((results) => {
    checkRows.value = results.map((r) => ({
      act: r.act,
      label: ACT_LABEL[r.act],
      allowed: r.allowed,
    }))
    checkLoading.value = false
  })
}

// ===== 授权 =====
const grantVisible = ref(false)
const grantForm = reactive({ targetUserId: null, deptId: null, acts: [], docLevels: [], objs: [], resIds: [] })
const userOptions = ref([])
const deptOptions = ref([])
const docOptions = ref([])

const openGrant = async () => {
  Object.assign(grantForm, { targetUserId: null, deptId: null, acts: [], docLevels: [], objs: [], resIds: [] })
  grantVisible.value = true
  if (!userOptions.value.length || !deptOptions.value.length || !docOptions.value.length) {
    const [userRes, deptRes, docRes] = await Promise.all([
      listUsers({ page: 1, pageSize: 1000 }),
      listDepts(),
      listDocs({ page: 1, pageSize: 1000 }),
    ])
    userOptions.value = userRes.data?.list || []
    deptOptions.value = deptRes.data || []
    docOptions.value = docRes.data?.list || []
  }
}

const handleSubmitGrant = async () => {
  if (!grantForm.targetUserId || !grantForm.deptId || !grantForm.acts.length || !grantForm.docLevels.length) {
    ElMessage.warning('请选择用户、部门、操作与密级范围')
    return
  }
  await grantDoc({
    targetUserId: grantForm.targetUserId,
    deptId: grantForm.deptId,
    acts: grantForm.acts,
    docLevels: grantForm.docLevels,
    objs: grantForm.objs, // 空=全部资源类型
    resIds: grantForm.resIds, // 空=全部资源实例；指定时精确到单条资源
  })
  ElMessage.success('授权成功')
  grantVisible.value = false
}

// ===== 收回授权 =====
const handleRevoke = () => {
  ElMessageBox.prompt('输入要收回授权的用户ID', '收回显式授权', {
    confirmButtonText: '确认收回',
    cancelButtonText: '取消',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入数字用户ID',
  })
    .then(async ({ value }) => {
      await revokeDoc(Number(value))
      ElMessage.success(`已收回用户 ${value} 的全部显式授权`)
    })
    .catch(() => {})
}

onMounted(fetchList)
</script>

<template>
  <div class="page">
    <h2>文档权限管理</h2>

    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm" @submit.prevent="handleSearch">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="标题关键字" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="queryForm.obj" placeholder="全部资源" clearable style="width: 140px">
            <el-option v-for="(label, val) in OBJ_MAP" :key="val" :value="val" :label="label" />
          </el-select>
        </el-form-item>
        <el-form-item label="密级">
          <el-select v-model="queryForm.docLevel" placeholder="全部密级" clearable style="width: 140px">
            <el-option v-for="(label, val) in LEVEL_MAP" :key="val" :value="Number(val)" :label="label" />
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
        <el-button v-if="hasPerm('doc:grant')" type="primary" @click="openGrant">显式授权</el-button>
        <el-button v-if="hasPerm('doc:revoke')" type="danger" plain @click="handleRevoke">收回授权</el-button>
      </div>
      <el-table v-loading="loading" :data="tableList" stripe border style="width: 100%">
        <el-table-column prop="docId" label="ID" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column label="资源类型" width="100" align="center">
          <template #default="{ row }">{{ OBJ_MAP[row.obj] || row.obj }}</template>
        </el-table-column>
        <el-table-column label="密级" width="100" align="center">
          <template #default="{ row }">{{ LEVEL_MAP[row.docLevel] || row.docLevel }}</template>
        </el-table-column>
        <el-table-column prop="deptId" label="归属部门" width="90" align="center" />
        <el-table-column prop="creatorId" label="创建人ID" width="90" align="center" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openCheck(row)">权限试算</el-button>
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

    <!-- 权限试算弹窗 -->
    <el-dialog v-model="checkVisible" :title="`权限试算：${checkDoc?.title || ''}`" width="420px">
      <el-table v-loading="checkLoading" :data="checkRows" stripe border>
        <el-table-column prop="label" label="操作" width="100" align="center" />
        <el-table-column prop="act" label="act" width="100" align="center" />
        <el-table-column label="判定结果" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.allowed === true" type="success">允许</el-tag>
            <el-tag v-else-if="row.allowed === false" type="danger">拒绝</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 显式授权弹窗 -->
    <el-dialog v-model="grantVisible" title="显式授权（属性级范围）" width="520px">
      <el-form :model="grantForm" label-width="90px">
        <el-form-item label="被授权用户">
          <el-select v-model="grantForm.targetUserId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.userId" :value="u.userId" :label="u.userName" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门范围">
          <el-select v-model="grantForm.deptId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="d in deptOptions" :key="d.deptId" :value="d.deptId" :label="d.deptName" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作集合">
          <el-checkbox-group v-model="grantForm.acts">
            <el-checkbox v-for="act in ACTS" :key="act" :value="act">{{ ACT_LABEL[act] }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="密级范围">
          <el-checkbox-group v-model="grantForm.docLevels">
            <el-checkbox v-for="(label, val) in LEVEL_MAP" :key="val" :value="Number(val)">{{ label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-checkbox-group v-model="grantForm.objs">
            <el-checkbox v-for="(label, val) in OBJ_MAP" :key="val" :value="val">{{ label }}</el-checkbox>
          </el-checkbox-group>
          <div class="tip">不勾选表示全部资源类型</div>
        </el-form-item>
        <el-form-item label="资源实例">
          <el-select v-model="grantForm.resIds" placeholder="全部实例（可多选具体资源）" multiple filterable clearable collapse-tags style="width: 100%">
            <el-option
              v-for="d in docOptions"
              :key="d.docId"
              :value="d.docId"
              :label="`#${d.docId} ${d.title}（${OBJ_MAP[d.obj] || d.obj}·${LEVEL_MAP[d.docLevel] || d.docLevel}）`"
            />
          </el-select>
          <div class="tip">不选表示全部资源实例；选中后授权精确到指定资源ID</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitGrant">确定授权</el-button>
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
.tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}
</style>
