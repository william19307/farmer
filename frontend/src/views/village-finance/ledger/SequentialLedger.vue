<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">序时账（记账凭证汇总）</span>
          <el-button :icon="Download" @click="exportExcel">导出Excel</el-button>
        </div>
      </template>

      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:220px" @change="loadData">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
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
            @change="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计信息 -->
      <el-row :gutter="16" class="stat-row" v-if="tableData.length">
        <el-col :span="6">
          <div class="stat-item">
            <span class="stat-label">凭证总数</span>
            <span class="stat-value">{{ tableData.length }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <span class="stat-label">借方合计</span>
            <span class="stat-value blue">¥ {{ totalDebit }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <span class="stat-label">贷方合计</span>
            <span class="stat-value green">¥ {{ totalCredit }}</span>
          </div>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" border size="small">
        <el-table-column prop="makeDate" label="日期" width="110" />
        <el-table-column label="凭证字号" width="100">
          <template #default="{ row }">{{ row.voucherWord }}字第{{ row.voucherNo }}号</template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" show-overflow-tooltip />
        <el-table-column label="明细" min-width="300">
          <template #default="{ row }">
            <div v-for="(d, i) in row.details" :key="i" class="detail-row">
              <span class="account-name">{{ d.accountName }}</span>
              <span v-if="d.debitAmount > 0" class="debit">借: {{ fmtNum(d.debitAmount) }}</span>
              <span v-if="d.creditAmount > 0" class="credit">贷: {{ fmtNum(d.creditAmount) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="借方金额" width="130" align="right">
          <template #default="{ row }">
            {{ fmtNum(row.details?.reduce((s, d) => s + (d.debitAmount || 0), 0)) }}
          </template>
        </el-table-column>
        <el-table-column label="贷方金额" width="130" align="right">
          <template #default="{ row }">
            {{ fmtNum(row.details?.reduce((s, d) => s + (d.creditAmount || 0), 0)) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getSequentialLedger, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const accountSets = ref([])
const dateRange = ref([
  dayjs().startOf('month').format('YYYY-MM-DD'),
  dayjs().endOf('month').format('YYYY-MM-DD'),
])

const query = reactive({ accountSetId: null, startDate: null, endDate: null })

const totalDebit = computed(() => {
  const sum = tableData.value.reduce((s, v) =>
    s + (v.details || []).reduce((ds, d) => ds + (d.debitAmount || 0), 0), 0)
  return sum.toLocaleString('zh-CN', { minimumFractionDigits: 2 })
})

const totalCredit = computed(() => {
  const sum = tableData.value.reduce((s, v) =>
    s + (v.details || []).reduce((ds, d) => ds + (d.creditAmount || 0), 0), 0)
  return sum.toLocaleString('zh-CN', { minimumFractionDigits: 2 })
})

function fmtNum(val) {
  if (!val || val === 0) return ''
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

async function loadData() {
  if (!query.accountSetId) return
  if (dateRange.value?.length === 2) {
    query.startDate = dateRange.value[0]
    query.endDate = dateRange.value[1]
  }
  loading.value = true
  try {
    const res = await getSequentialLedger(query)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function exportExcel() {
  ElMessage.info('导出功能开发中')
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
.search-form { margin-bottom: 8px; }

.stat-row { margin-bottom: 16px; }
.stat-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-label { color: #909399; font-size: 13px; }
.stat-value { font-weight: 700; font-size: 16px; color: #303133; }
.stat-value.blue { color: #409eff; }
.stat-value.green { color: #67c23a; }

.detail-row {
  display: flex;
  gap: 12px;
  padding: 2px 0;
  font-size: 12px;
  border-bottom: 1px dashed #f0f0f0;
}
.detail-row:last-child { border-bottom: none; }
.account-name { color: #606266; flex: 1; }
.debit { color: #409eff; white-space: nowrap; }
.credit { color: #67c23a; white-space: nowrap; }
</style>
