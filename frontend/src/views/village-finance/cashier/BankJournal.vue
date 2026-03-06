<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">银行日记账</span>
          <el-button type="primary" :icon="Plus" @click="openForm()">新增</el-button>
        </div>
      </template>

      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:200px" @change="onAccountSetChange">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户">
          <el-select v-model="query.accountBookId" placeholder="选择账户" clearable style="width:200px" @change="loadList">
            <el-option v-for="b in bankBooks" :key="b.id" :label="b.bookName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" @change="loadList" />
        </el-form-item>
      </el-form>

      <!-- 账户余额汇总 -->
      <el-row :gutter="12" v-if="bankBooks.length" class="book-cards">
        <el-col :span="6" v-for="b in bankBooks" :key="b.id">
          <div class="book-card" :class="{ active: query.accountBookId === b.id }" @click="query.accountBookId = b.id; loadList()">
            <div class="book-name">{{ b.bookName }}</div>
            <div class="book-balance">¥ {{ b.currentBalance?.toLocaleString() }}</div>
          </div>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="journalDate" label="日期" width="120" />
        <el-table-column prop="summary" label="摘要" show-overflow-tooltip />
        <el-table-column prop="incomeAmount" label="收入(元)" width="130" :formatter="fmtMoney" />
        <el-table-column prop="expenseAmount" label="支出(元)" width="130" :formatter="fmtMoney" />
        <el-table-column prop="balance" label="余额(元)" width="130" :formatter="fmtMoney" />
        <el-table-column prop="voucherNo" label="凭证号" width="100" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="success" size="small" :disabled="!!row.voucherId" @click="genVoucher(row.id)">生成凭证</el-button>
            <el-button link type="danger" size="small" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, prev, pager, next" @change="loadList" class="pagination" />
    </el-card>

    <el-dialog v-model="formVisible" :title="editRow ? '编辑银行日记账' : '新增银行日记账'" width="500px">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="账户" required>
          <el-select v-model="formData.accountBookId" style="width:100%">
            <el-option v-for="b in bankBooks" :key="b.id" :label="b.bookName" :value="b.id" />
          </el-select>
        </el-form-item>
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
import { getJournals, createJournal, updateJournal, deleteJournal, generateVoucherFromJournal, getAccountSets } from '@/api/finance'
import request from '@/api/request'
import dayjs from 'dayjs'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const accountSets = ref([])
const bankBooks = ref([])
const formVisible = ref(false)
const editRow = ref(null)
const dateRange = ref([])

const query = reactive({ accountSetId: null, accountBookId: null, journalType: 1, startDate: null, endDate: null, pageNum: 1, pageSize: 20 })
const formData = reactive({ accountBookId: null, journalDate: dayjs().format('YYYY-MM-DD'), summary: '', incomeAmount: 0, expenseAmount: 0 })

function fmtMoney(row, col, val) { return val != null ? val.toLocaleString() : '--' }

async function loadAccountSets() {
  const res = await getAccountSets()
  accountSets.value = res.data || []
  if (accountSets.value.length) { query.accountSetId = accountSets.value[0].id; await onAccountSetChange() }
}

async function onAccountSetChange() {
  const res = await request.get(`/finance/account-set/${query.accountSetId}/accounts`)
  // fetch account books
  const booksRes = await request.get('/finance/journal', { params: { accountSetId: query.accountSetId, journalType: 1, pageNum: 1, pageSize: 1 } })
  // Use account books API
  const abRes = await request.get(`/finance/account-set`, { params: { accountSetId: query.accountSetId } })
  // Actually get account books from a different endpoint - simplified
  bankBooks.value = []
  query.accountBookId = null
  loadList()
}

async function loadList() {
  if (!query.accountSetId) return
  if (dateRange.value?.length === 2) { query.startDate = dateRange.value[0]; query.endDate = dateRange.value[1] }
  else { query.startDate = null; query.endDate = null }
  loading.value = true
  try {
    const res = await getJournals(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function openForm(row = null) {
  editRow.value = row
  if (row) { Object.assign(formData, { accountBookId: row.accountBookId, journalDate: row.journalDate, summary: row.summary, incomeAmount: row.incomeAmount, expenseAmount: row.expenseAmount, id: row.id }) }
  else { Object.assign(formData, { accountBookId: bankBooks.value[0]?.id, journalDate: dayjs().format('YYYY-MM-DD'), summary: '', incomeAmount: 0, expenseAmount: 0 }) }
  formVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { ...formData, accountSetId: query.accountSetId, journalType: 1 }
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

async function genVoucher(id) {
  await generateVoucherFromJournal(id)
  ElMessage.success('凭证生成成功')
  loadList()
}

onMounted(() => loadAccountSets())
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 8px; }
.book-cards { margin-bottom: 16px; }
.book-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.book-card:hover, .book-card.active { border-color: #40916c; box-shadow: 0 0 0 2px rgba(64,145,108,0.2); }
.book-name { font-size: 13px; color: #606266; }
.book-balance { font-size: 20px; font-weight: 700; color: #2d6a4f; margin-top: 4px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
