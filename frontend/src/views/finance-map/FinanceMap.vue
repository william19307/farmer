<template>
  <div class="finance-map">
    <!-- 顶部信息栏 -->
    <div class="top-bar">
      <div class="time-weather">
        <span class="time">{{ currentTime }}</span>
        <span class="weather">{{ weather }} {{ temperature }}</span>
      </div>
      <div class="title">
        <h2>村集体财务管理驾驶舱</h2>
      </div>
      <div class="filters">
        <el-select v-model="selectedRegion" placeholder="选择区域" clearable style="width:160px" @change="loadData">
          <el-option v-for="r in regions" :key="r.id" :label="r.regionName" :value="r.id" />
        </el-select>
        <el-date-picker
          v-model="selectedMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY年MM月"
          value-format="YYYYMM"
          style="width:160px"
          @change="loadData"
        />
      </div>
    </div>

    <!-- 概览卡片 -->
    <el-row :gutter="16" class="overview-cards">
      <el-col :span="6" v-for="card in overviewCards" :key="card.label">
        <div class="stat-card" :style="{ borderTopColor: card.color }">
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>村集体收入分级统计</span></template>
          <v-chart :option="incomeChartOption" style="height:300px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>财务指标汇总</span></template>
          <v-chart :option="indicatorChartOption" style="height:300px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 收入明细表格 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>村集体收入明细</span>
          <el-radio-group v-model="incomeFilter" size="small" @change="loadIncomeDetail">
            <el-radio-button label="above">高于阈值</el-radio-button>
            <el-radio-button label="below">低于阈值</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="incomeDetail" stripe size="small">
        <el-table-column prop="villageId" label="村庄ID" width="100" />
        <el-table-column prop="totalIncome" label="总收入(元)" :formatter="fmtMoney" />
        <el-table-column prop="businessIncome" label="经营收入" :formatter="fmtMoney" />
        <el-table-column prop="subsidyIncome" label="补助收入" :formatter="fmtMoney" />
        <el-table-column prop="investmentIncome" label="投资收益" :formatter="fmtMoney" />
        <el-table-column prop="otherIncome" label="其他收入" :formatter="fmtMoney" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { use } from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'
import { getOverview, getIncomeStats, getIncomeDetailAbove, getIncomeDetailBelow, getIndicators } from '@/api/finance-map'

use([BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent, CanvasRenderer])

const currentTime = ref('')
const weather = ref('晴')
const temperature = ref('18°C ~ 25°C')
const selectedRegion = ref(null)
const selectedMonth = ref(dayjs().format('YYYYMM'))
const regions = ref([])
const overview = ref({})
const incomeStats = ref([])
const incomeDetail = ref([])
const incomeFilter = ref('above')
const indicators = ref({})

let timer = null

const overviewCards = computed(() => [
  { label: '区县数量', value: overview.value.countyCount ?? '--', color: '#409eff' },
  { label: '乡镇数量', value: overview.value.townshipCount ?? '--', color: '#67c23a' },
  { label: '村集体总数', value: overview.value.villageTotal ?? '--', color: '#e6a23c' },
  { label: '未结账村数', value: overview.value.unclosedCount ?? '--', color: '#f56c6c' },
])

const incomeChartOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['高于阈值', '低于阈值'] },
  xAxis: { type: 'category', data: incomeStats.value.map(s => `${(s.threshold / 10000).toFixed(0)}万`) },
  yAxis: { type: 'value', name: '村数' },
  series: [
    { name: '高于阈值', type: 'bar', data: incomeStats.value.map(s => s.aboveCount), itemStyle: { color: '#67c23a' } },
    { name: '低于阈值', type: 'bar', data: incomeStats.value.map(s => s.belowCount), itemStyle: { color: '#f56c6c' } },
  ],
}))

const indicatorChartOption = computed(() => {
  const d = indicators.value
  if (!d || !d.monetaryFunds) return {}
  return {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', right: 10 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: d.monetaryFunds, name: '货币资金' },
        { value: d.receivables, name: '应收账款' },
        { value: d.inventory, name: '存货' },
        { value: d.fixedAssets, name: '固定资产' },
      ],
    }],
  }
})

function fmtMoney(row, col, val) {
  return val != null ? val.toLocaleString() : '--'
}

async function loadData() {
  const params = { regionId: selectedRegion.value, statMonth: selectedMonth.value }
  try {
    const [ovRes, statsRes, indRes] = await Promise.all([
      getOverview(params),
      getIncomeStats(params),
      getIndicators(params),
    ])
    overview.value = ovRes.data || {}
    weather.value = overview.value.weather || '晴'
    temperature.value = overview.value.temperature || '18°C ~ 25°C'
    incomeStats.value = statsRes.data || []
    indicators.value = indRes.data || {}
  } catch {}
  await loadIncomeDetail()
}

async function loadIncomeDetail() {
  const params = { regionId: selectedRegion.value, statMonth: selectedMonth.value, pageNum: 1, pageSize: 50 }
  try {
    const fn = incomeFilter.value === 'above' ? getIncomeDetailAbove : getIncomeDetailBelow
    const res = await fn(params)
    incomeDetail.value = res.data?.records || []
  } catch {}
}

function updateTime() {
  currentTime.value = dayjs().format('YYYY年MM月DD日 HH:mm:ss')
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadData()
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
.finance-map {
  min-height: 100%;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #1a3a2a, #2d6a4f);
  color: #fff;
  padding: 12px 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.top-bar h2 { margin: 0; font-size: 20px; }
.time { font-size: 14px; margin-right: 16px; }
.weather { font-size: 14px; opacity: 0.8; }
.filters { display: flex; gap: 8px; }

.overview-cards { margin-bottom: 16px; }

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border-top: 4px solid;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.stat-value { font-size: 32px; font-weight: 700; }
.stat-label { color: #909399; margin-top: 8px; font-size: 14px; }

.chart-row { margin-bottom: 16px; }
.chart-card, .table-card { height: 100%; }

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
