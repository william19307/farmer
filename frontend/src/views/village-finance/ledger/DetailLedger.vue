<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">明细账</span>
          <el-button :icon="Download" @click="exportExcel">导出Excel</el-button>
        </div>
      </template>

      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:200px" @change="onAccountSetChange">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="query.accountId" placeholder="全部科目" clearable filterable style="width:240px" @change="loadData">
            <el-option
              v-for="a in accounts"
              :key="a.id"
              :label="`${a.accountCode} ${a.accountName}`"
              :value="a.id"
            />
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

      <!-- 科目信息 -->
      <div v-if="selectedAccount" class="account-info">
        <span>科目：<strong>{{ selectedAccount.accountCode }} {{ selectedAccount.accountName }}</strong></span>
        <span style="margin-left:24px">余额方向：<strong>{{ selectedAccount.balanceDirection === 'debit' ? '借方' : '贷方' }}</strong></span>
      </div>

      <el-table :data="flatDetails" v-loading="loading" border stripe size="small">
        <el-table-column prop="makeDate" label="日期" width="110" />
        <el-table-column label="凭证字号" width="110">
          <template #default="{ row }">
            <span v-if="row.isDetail">{{ row.voucherWord }}字第{{ row.voucherNo }}号</span>
            <span v-else class="group-label">{{ row.label }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" show-overflow-tooltip />
        <el-table-column prop="debitAmount" label="借方金额" width="130" align="right">
          <template #default="{ row }">
            <span :class="{ 'amount-positive': row.debitAmount > 0 }">
              {{ row.debitAmount > 0 ? fmtNum(row.debitAmount) : '' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="creditAmount" label="贷方金额" width="130" align="right">
          <template #default="{ row }">
            <span :class="{ 'amount-positive': row.creditAmount > 0 }">
              {{ row.creditAmount > 0 ? fmtNum(row.creditAmount) : '' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="runningBalance" label="方向" width="60" align="center">
          <template #default="{ row }">
            <span v-if="row.isDetail">{{ row.balanceDir }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="runningBalance" label="余额" width="130" align="right">
          <template #default="{ row }">
            <span v-if="row.isDetail">{{ fmtNum(Math.abs(row.runningBalance)) }}</span>
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
import { getDetailLedger, getAccountSets, getAccounts } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const rawData = ref([])
const accountSets = ref([])
const accounts = ref([])
const dateRange = ref([
  dayjs().startOf('month').format('YYYY-MM-DD'),
  dayjs().endOf('month').format('YYYY-MM-DD'),
])

const query = reactive({ accountSetId: null, accountId: null, startDate: null, endDate: null })

const selectedAccount = computed(() =>
  accounts.value.find(a => a.id === query.accountId) || null
)

// 将凭证列表展开为带余额的明细行
const flatDetails = computed(() => {
  const rows = []
  let balance = 0
  const isDebit = selectedAccount.value?.balanceDirection === 'debit'

  rawData.value.forEach(voucher => {
    const details = (voucher.details || []).filter(d =>
      !query.accountId || d.accountId === query.accountId
    )
    details.forEach(d => {
      const dr = d.debitAmount || 0
      const cr = d.creditAmount || 0
      balance += isDebit ? (dr - cr) : (cr - dr)
      rows.push({
        isDetail: true,
        makeDate: voucher.makeDate,
        voucherWord: voucher.voucherWord,
        voucherNo: voucher.voucherNo,
        summary: d.summary || voucher.summary,
        debitAmount: dr,
        creditAmount: cr,
        runningBalance: balance,
        balanceDir: balance >= 0 ? (isDebit ? '借' : '贷') : (isDebit ? '贷' : '借'),
      })
    })
  })
  return rows
})

function fmtNum(val) {
  if (!val && val !== 0) return ''
  return Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

async function onAccountSetChange() {
  query.accountId = null
  if (query.accountSetId) {
    const res = await getAccounts(query.accountSetId)
    accounts.value = (res.data || []).filter(a => a.isLeaf === 1)
  }
  loadData()
}

async function loadData() {
  if (!query.accountSetId) return
  if (dateRange.value?.length === 2) {
    query.startDate = dateRange.value[0]
    query.endDate = dateRange.value[1]
  }
  loading.value = true
  try {
    const res = await getDetailLedger(query)
    rawData.value = res.data || []
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
    await onAccountSetChange()
  }
})
</script>

<style scoped>
.page-header { display: flex; align-items: center; justify-content: space-between; }
.page-title { font-size: 16px; font-weight: 600; }
.search-form { margin-bottom: 8px; }
.account-info {
  background: #ecf5ff;
  border: 1px solid #b3d8ff;
  border-radius: 6px;
  padding: 8px 16px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}
.amount-positive { color: #303133; font-weight: 500; }
.group-label { font-weight: 700; color: #303133; }
</style>
