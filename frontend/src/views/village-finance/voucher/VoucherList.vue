<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">凭证管理</span>
          <div class="header-actions">
            <el-button type="primary" :icon="Plus" @click="openForm()">新增凭证</el-button>
            <el-button :icon="Check" :disabled="!selectedIds.length" @click="batchAudit">批量审核</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:200px" @change="loadList">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:120px" @change="loadList">
            <el-option label="草稿" :value="0" />
            <el-option label="已审核" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadList"
          />
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        @selection-change="handleSelection"
        stripe
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="voucherWord" label="凭证字" width="80" />
        <el-table-column prop="voucherNo" label="凭证号" width="80" />
        <el-table-column prop="makeDate" label="制单日期" width="120" />
        <el-table-column prop="summary" label="摘要" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="制单时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openDetail(row.id)">查看</el-button>
            <el-button v-if="row.status === 0" link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button v-if="row.status === 0" link type="success" size="small" @click="audit(row.id)">审核</el-button>
            <el-button v-if="row.status === 1" link type="warning" size="small" @click="unaudit(row.id)">反审核</el-button>
            <el-button v-if="row.status === 0" link type="danger" size="small" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @change="loadList"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <VoucherForm
      v-if="formVisible"
      :voucher-id="editId"
      :account-set-id="query.accountSetId"
      @close="formVisible = false"
      @saved="onSaved"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Check } from '@element-plus/icons-vue'
import { getVouchers, auditVoucher, unauditVoucher, deleteVoucher, batchAuditVouchers } from '@/api/finance'
import { getAccountSets } from '@/api/finance'
import VoucherForm from './VoucherForm.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])
const formVisible = ref(false)
const editId = ref(null)
const accountSets = ref([])
const dateRange = ref([])

const query = reactive({
  accountSetId: null,
  status: null,
  startDate: null,
  endDate: null,
  pageNum: 1,
  pageSize: 20,
})

async function loadAccountSets() {
  try {
    const res = await getAccountSets()
    accountSets.value = res.data || []
    if (accountSets.value.length && !query.accountSetId) {
      query.accountSetId = accountSets.value[0].id
    }
  } catch {}
}

async function loadList() {
  if (!query.accountSetId) return
  if (dateRange.value?.length === 2) {
    query.startDate = dateRange.value[0]
    query.endDate = dateRange.value[1]
  } else {
    query.startDate = null
    query.endDate = null
  }
  loading.value = true
  try {
    const res = await getVouchers(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSelection(rows) {
  selectedIds.value = rows.map(r => r.id)
}

function openForm(row = null) {
  editId.value = row?.id || null
  formVisible.value = true
}

function openDetail(id) {
  editId.value = id
  formVisible.value = true
}

function onSaved() {
  formVisible.value = false
  loadList()
}

async function audit(id) {
  await ElMessageBox.confirm('确认审核该凭证？', '提示', { type: 'warning' })
  await auditVoucher(id)
  ElMessage.success('审核成功')
  loadList()
}

async function unaudit(id) {
  await ElMessageBox.confirm('确认反审核该凭证？', '提示', { type: 'warning' })
  await unauditVoucher(id)
  ElMessage.success('反审核成功')
  loadList()
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该凭证？', '警告', { type: 'error' })
  await deleteVoucher(id)
  ElMessage.success('删除成功')
  loadList()
}

async function batchAudit() {
  await ElMessageBox.confirm(`确认批量审核 ${selectedIds.value.length} 条凭证？`, '提示', { type: 'warning' })
  await batchAuditVouchers(selectedIds.value)
  ElMessage.success('批量审核成功')
  loadList()
}

onMounted(async () => {
  await loadAccountSets()
  loadList()
})
</script>

<style scoped>
.page-container { }
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
