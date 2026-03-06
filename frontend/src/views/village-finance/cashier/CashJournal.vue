<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">现金日记账</span>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
        </div>
      </template>
      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:200px" @change="loadList">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" @change="loadList" />
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="journalDate" label="日期" width="120" />
        <el-table-column prop="summary" label="摘要" show-overflow-tooltip />
        <el-table-column prop="incomeAmount" label="收入(元)" width="130" :formatter="fmtMoney" />
        <el-table-column prop="expenseAmount" label="支出(元)" width="130" :formatter="fmtMoney" />
        <el-table-column prop="balance" label="余额(元)" width="130" :formatter="fmtMoney" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadList" class="pagination" />
    </el-card>

    <el-dialog v-model="formVisible" :title="editRow ? '编辑现金日记账' : '新增现金日记账'" width="480px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="日期" required>
          <el-date-picker v-model="formData.journalDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="formData.summary" />
        </el-form-item>
        <el-form-item label="收入金额">
          <el-input-number v-model="formData.incomeAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="支出金额">
          <el-input-number v-model="formData.expenseAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getJournals, createJournal, updateJournal, deleteJournal, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const accountSets = ref([])
const formVisible = ref(false)
const editRow = ref(null)
const dateRange = ref([])

const query = reactive({ accountSetId: null, journalType: 2, startDate: null, endDate: null, pageNum: 1, pageSize: 20 })
const formData = reactive({ journalDate: dayjs().format('YYYY-MM-DD'), summary: '', incomeAmount: 0, expenseAmount: 0 })

function fmtMoney(row, col, val) { return val != null ? val.toLocaleString() : '--' }

async function loadList() {
  if (!query.accountSetId) return
  if (dateRange.value?.length === 2) { query.startDate = dateRange.value[0]; query.endDate = dateRange.value[1] }
  loading.value = true
  try {
    const res = await getJournals(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function openForm(row = null) {
  editRow.value = row
  if (row) Object.assign(formData, { id: row.id, journalDate: row.journalDate, summary: row.summary, incomeAmount: row.incomeAmount, expenseAmount: row.expenseAmount })
  else Object.assign(formData, { journalDate: dayjs().format('YYYY-MM-DD'), summary: '', incomeAmount: 0, expenseAmount: 0 })
  formVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { ...formData, accountSetId: query.accountSetId, journalType: 2 }
    if (editRow.value) await updateJournal(payload)
    else await createJournal(payload)
    ElMessage.success('保存成功')
    formVisible.value = false
    loadList()
  } finally { saving.value = false }
}

async function del(id) {
  await ElMessageBox.confirm('确认删除？', '警告', { type: 'error' })
  await deleteJournal(id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(async () => {
  const res = await getAccountSets()
  accountSets.value = res.data || []
  if (accountSets.value.length) { query.accountSetId = accountSets.value[0].id; loadList() }
})
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
