<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">资产负债表</span>
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
        <el-form-item label="报告期间">
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
      </el-form>

      <!-- 报表主体 -->
      <div class="report-wrap" v-loading="loading">
        <div class="report-title">
          <h3>资产负债表</h3>
          <p>编制单位：{{ accountSetName }}　　报告期间：{{ periodLabel }}　　单位：元</p>
        </div>

        <table class="report-table">
          <thead>
            <tr>
              <th class="col-name">资产</th>
              <th class="col-amount">期末余额</th>
              <th class="col-amount">年初余额</th>
              <th class="col-name">负债及所有者权益</th>
              <th class="col-amount">期末余额</th>
              <th class="col-amount">年初余额</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, i) in reportRows" :key="i" :class="{ 'total-row': row.isTotal }">
              <td>{{ row.assetName }}</td>
              <td class="num-cell">{{ fmtNum(row.assetClosing) }}</td>
              <td class="num-cell">{{ fmtNum(row.assetOpening) }}</td>
              <td>{{ row.liabName }}</td>
              <td class="num-cell">{{ fmtNum(row.liabClosing) }}</td>
              <td class="num-cell">{{ fmtNum(row.liabOpening) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Download, Printer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getSubjectBalance, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const accountSets = ref([])
const balanceData = ref([])

const query = reactive({
  accountSetId: null,
  period: dayjs().format('YYYYMM'),
})

const accountSetName = computed(() =>
  accountSets.value.find(s => s.id === query.accountSetId)?.setName || ''
)
const periodLabel = computed(() => {
  if (!query.period) return ''
  return `${query.period.slice(0, 4)}年${query.period.slice(4)}月`
})

// 从科目余额数据中提取特定科目的期末余额
function getBalance(code) {
  const item = balanceData.value.find(b => b.accountCode === code)
  return item ? (item.closingBalance || 0) : 0
}
function getOpeningBalance(code) {
  const item = balanceData.value.find(b => b.accountCode === code)
  return item ? (item.openingBalance || 0) : 0
}

// 报表行数据
const reportRows = computed(() => {
  const monetary = getBalance('1001') + getBalance('1002') + getBalance('1003')
  const openMonetary = getOpeningBalance('1001') + getOpeningBalance('1002') + getOpeningBalance('1003')
  const receivables = getBalance('1101')
  const openReceivables = getOpeningBalance('1101')
  const inventory = getBalance('1201')
  const openInventory = getOpeningBalance('1201')
  const fixedAssets = getBalance('1501') - getBalance('1502')
  const openFixedAssets = getOpeningBalance('1501') - getOpeningBalance('1502')
  const totalAsset = monetary + receivables + inventory + fixedAssets
  const openTotalAsset = openMonetary + openReceivables + openInventory + openFixedAssets

  const payables = getBalance('2001')
  const openPayables = getOpeningBalance('2001')
  const otherPayables = getBalance('2101')
  const openOtherPayables = getOpeningBalance('2101')
  const capital = getBalance('3001')
  const openCapital = getOpeningBalance('3001')
  const reserve = getBalance('3101')
  const openReserve = getOpeningBalance('3101')
  const undistributed = getBalance('3201')
  const openUndistributed = getOpeningBalance('3201')
  const totalLiab = payables + otherPayables + capital + reserve + undistributed
  const openTotalLiab = openPayables + openOtherPayables + openCapital + openReserve + openUndistributed

  return [
    { assetName: '货币资金',   assetClosing: monetary,     assetOpening: openMonetary,     liabName: '应付账款',       liabClosing: payables,       liabOpening: openPayables },
    { assetName: '应收账款',   assetClosing: receivables,  assetOpening: openReceivables,  liabName: '其他应付款',     liabClosing: otherPayables,  liabOpening: openOtherPayables },
    { assetName: '存货',       assetClosing: inventory,    assetOpening: openInventory,    liabName: '资本',           liabClosing: capital,        liabOpening: openCapital },
    { assetName: '固定资产净值', assetClosing: fixedAssets, assetOpening: openFixedAssets, liabName: '公积公益金',     liabClosing: reserve,        liabOpening: openReserve },
    { assetName: '',          assetClosing: null,         assetOpening: null,             liabName: '未分配收益',     liabClosing: undistributed,  liabOpening: openUndistributed },
    { assetName: '资产合计', assetClosing: totalAsset, assetOpening: openTotalAsset, liabName: '负债及权益合计', liabClosing: totalLiab, liabOpening: openTotalLiab, isTotal: true },
  ]
})

function fmtNum(val) {
  if (val === null || val === undefined) return ''
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

async function loadData() {
  if (!query.accountSetId) return
  loading.value = true
  try {
    const res = await getSubjectBalance(query)
    balanceData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function exportExcel() { ElMessage.info('导出功能开发中') }
function print() { window.print() }

onMounted(async () => {
  const res = await getAccountSets()
  accountSets.value = res.data || []
  if (accountSets.value.length) { query.accountSetId = accountSets.value[0].id; loadData() }
})
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.header-actions { display: flex; gap: 8px; }
.search-form { margin-bottom: 16px; }

.report-wrap { max-width: 1000px; margin: 0 auto; }
.report-title { text-align: center; margin-bottom: 16px; }
.report-title h3 { font-size: 18px; font-weight: 700; color: #303133; }
.report-title p { color: #606266; font-size: 13px; margin-top: 4px; }

.report-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.report-table th, .report-table td {
  border: 1px solid #dcdfe6;
  padding: 8px 12px;
}
.report-table th {
  background: #f5f7fa;
  font-weight: 600;
  text-align: center;
}
.col-name { width: 28%; }
.col-amount { width: 11%; text-align: center; }
.num-cell { text-align: right; font-family: monospace; }
.total-row td { background: #f0f9eb; font-weight: 700; }

@media print {
  .search-form, .page-header .header-actions { display: none; }
}
</style>
