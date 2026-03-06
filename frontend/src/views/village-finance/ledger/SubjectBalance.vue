<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">科目余额表</span>
          <div class="header-actions">
            <el-button :icon="Download" @click="exportExcel">导出Excel</el-button>
            <el-button :icon="Printer" @click="print">打印</el-button>
          </div>
        </div>
      </template>

      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:220px" @change="loadData">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会计期间">
          <el-date-picker
            v-model="query.period"
            type="month"
            placeholder="选择期间"
            format="YYYY年MM月"
            value-format="YYYYMM"
            style="width:160px"
            @change="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        show-summary
        :summary-method="getSummaries"
        row-key="accountId"
        :tree-props="{ children: 'children' }"
        size="small"
      >
        <el-table-column prop="accountCode" label="科目编码" width="120" fixed />
        <el-table-column prop="accountName" label="科目名称" min-width="140" fixed />
        <el-table-column label="期初余额" align="center">
          <el-table-column prop="openingDebit" label="借方" width="120" :formatter="fmtMoney" align="right" />
          <el-table-column prop="openingCredit" label="贷方" width="120" :formatter="fmtMoney" align="right" />
        </el-table-column>
        <el-table-column label="本期发生额" align="center">
          <el-table-column prop="periodDebit" label="借方" width="120" :formatter="fmtMoney" align="right" />
          <el-table-column prop="periodCredit" label="贷方" width="120" :formatter="fmtMoney" align="right" />
        </el-table-column>
        <el-table-column label="本年累计发生额" align="center">
          <el-table-column prop="yearDebit" label="借方" width="120" :formatter="fmtMoney" align="right" />
          <el-table-column prop="yearCredit" label="贷方" width="120" :formatter="fmtMoney" align="right" />
        </el-table-column>
        <el-table-column label="期末余额" align="center">
          <el-table-column prop="closingDebit" label="借方" width="120" :formatter="fmtMoney" align="right" />
          <el-table-column prop="closingCredit" label="贷方" width="120" :formatter="fmtMoney" align="right" />
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Download, Printer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getSubjectBalance, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const accountSets = ref([])

const query = reactive({
  accountSetId: null,
  period: dayjs().format('YYYYMM'),
})

function fmtMoney(row, col, val) {
  if (!val || val === 0) return ''
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

function getSummaries({ columns, data }) {
  const sums = []
  columns.forEach((col, index) => {
    if (index === 0) { sums[index] = '合计'; return }
    if (index === 1) { sums[index] = ''; return }
    const prop = col.property
    if (!prop) { sums[index] = ''; return }
    const total = data.reduce((s, row) => s + (Number(row[prop]) || 0), 0)
    sums[index] = total > 0 ? total.toLocaleString('zh-CN', { minimumFractionDigits: 2 }) : ''
  })
  return sums
}

async function loadData() {
  if (!query.accountSetId) return
  loading.value = true
  try {
    const res = await getSubjectBalance(query)
    tableData.value = buildTree(res.data || [])
  } finally {
    loading.value = false
  }
}

function buildTree(list) {
  const map = {}
  list.forEach(item => { map[item.accountId] = { ...item, children: [] } })
  const roots = []
  list.forEach(item => {
    const node = map[item.accountId]
    const parent = list.find(p => p.accountCode === item.accountCode.slice(0, -2))
    if (parent && map[parent.accountId]) {
      map[parent.accountId].children.push(node)
    } else {
      roots.push(node)
    }
  })
  // Remove empty children arrays
  const clean = (nodes) => nodes.map(n => {
    if (n.children.length === 0) delete n.children
    else n.children = clean(n.children)
    return n
  })
  return clean(roots)
}

function exportExcel() {
  ElMessage.info('导出功能开发中')
}

function print() {
  window.print()
}

onMounted(async () => {
  const res = await getAccountSets()
  accountSets.value = res.data || []
  if (accountSets.value.length) {
    query.accountSetId = accountSets.value[0].id
    loadData()
  }
})
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 16px; }
.header-actions { display: flex; gap: 8px; }
</style>
