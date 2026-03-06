<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">收益及收益分配表</span>
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

      <div class="report-wrap" v-loading="loading">
        <div class="report-title">
          <h3>收益及收益分配表</h3>
          <p>编制单位：{{ accountSetName }}　　报告期间：{{ periodLabel }}　　单位：元</p>
        </div>

        <table class="report-table">
          <thead>
            <tr>
              <th class="col-no">行次</th>
              <th class="col-name">项目</th>
              <th class="col-amount">本年累计数</th>
              <th class="col-amount">上年同期数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in reportRows" :key="row.no" :class="{ 'total-row': row.isTotal, 'sub-row': row.isSub }">
              <td class="center">{{ row.no }}</td>
              <td :style="{ paddingLeft: row.isSub ? '32px' : '12px' }">{{ row.name }}</td>
              <td class="num-cell">{{ fmtNum(row.current) }}</td>
              <td class="num-cell">{{ fmtNum(row.previous) }}</td>
            </tr>
          </tbody>
        </table>

        <!-- 图表展示 -->
        <div class="chart-wrap">
          <v-chart :option="incomeChartOption" style="height:280px" autoresize />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Download, Printer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import { getSubjectBalance, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

use([BarChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

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

function getAmount(code) {
  const item = balanceData.value.find(b => b.accountCode === code)
  return item ? (item.periodDebit > 0 ? item.periodDebit : item.periodCredit) || 0 : 0
}

const reportRows = computed(() => {
  const bizIncome = getAmount('4001')
  const contractIncome = getAmount('4002')
  const investmentIncome = getAmount('4003')
  const subsidyIncome = getAmount('4004')
  const otherIncome = getAmount('4005')
  const totalIncome = bizIncome + contractIncome + investmentIncome + subsidyIncome + otherIncome

  const bizExpense = getAmount('5001')
  const mgmtExpense = getAmount('5002')
  const otherExpense = getAmount('5003')
  const totalExpense = bizExpense + mgmtExpense + otherExpense

  const currentProfit = totalIncome - totalExpense

  return [
    { no: '一', name: '总收入', current: totalIncome, previous: 0, isTotal: true },
    { no: '1', name: '经营收入', current: bizIncome, previous: 0, isSub: true },
    { no: '2', name: '发包及上交收入', current: contractIncome, previous: 0, isSub: true },
    { no: '3', name: '投资收益', current: investmentIncome, previous: 0, isSub: true },
    { no: '4', name: '补助收入', current: subsidyIncome, previous: 0, isSub: true },
    { no: '5', name: '其他收入', current: otherIncome, previous: 0, isSub: true },
    { no: '二', name: '总支出', current: totalExpense, previous: 0, isTotal: true },
    { no: '6', name: '经营支出', current: bizExpense, previous: 0, isSub: true },
    { no: '7', name: '管理费用', current: mgmtExpense, previous: 0, isSub: true },
    { no: '8', name: '其他支出', current: otherExpense, previous: 0, isSub: true },
    { no: '三', name: '本年收益（总收入－总支出）', current: currentProfit, previous: 0, isTotal: true },
  ]
})

const incomeChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['金额'] },
  xAxis: {
    type: 'category',
    data: ['经营收入', '补助收入', '投资收益', '其他收入', '经营支出', '管理费用'],
    axisLabel: { fontSize: 12 },
  },
  yAxis: { type: 'value', name: '元' },
  series: [{
    name: '金额',
    type: 'bar',
    data: [
      { value: getAmount('4001'), itemStyle: { color: '#67c23a' } },
      { value: getAmount('4004'), itemStyle: { color: '#409eff' } },
      { value: getAmount('4003'), itemStyle: { color: '#e6a23c' } },
      { value: getAmount('4005'), itemStyle: { color: '#909399' } },
      { value: getAmount('5001'), itemStyle: { color: '#f56c6c' } },
      { value: getAmount('5002'), itemStyle: { color: '#fa8c16' } },
    ],
  }],
}))

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

.report-wrap { max-width: 800px; margin: 0 auto; }
.report-title { text-align: center; margin-bottom: 16px; }
.report-title h3 { font-size: 18px; font-weight: 700; }
.report-title p { color: #606266; font-size: 13px; margin-top: 4px; }

.report-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  margin-bottom: 24px;
}
.report-table th, .report-table td {
  border: 1px solid #dcdfe6;
  padding: 7px 12px;
}
.report-table th { background: #f5f7fa; font-weight: 600; text-align: center; }
.col-no { width: 60px; }
.col-name { }
.col-amount { width: 150px; text-align: center; }
.num-cell { text-align: right; font-family: monospace; }
.center { text-align: center; }
.total-row td { background: #f0f9eb; font-weight: 700; }
.sub-row td { color: #606266; }
.chart-wrap { margin-top: 8px; }
</style>
