<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="page-header">
          <span class="page-title">固定资产台账</span>
          <div class="header-actions">
            <el-button type="warning" @click="openDeprecDialog">计提折旧</el-button>
            <el-button type="primary" :icon="Plus" @click="openForm()">新增资产</el-button>
          </div>
        </div>
      </template>

      <el-form inline class="search-form">
        <el-form-item label="账套">
          <el-select v-model="query.accountSetId" placeholder="选择账套" style="width:200px" @change="loadList">
            <el-option v-for="s in accountSets" :key="s.id" :label="s.setName" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.assetType" clearable placeholder="全部" style="width:120px" @change="loadList">
            <el-option label="房屋建筑" :value="1" />
            <el-option label="机器设备" :value="2" />
            <el-option label="运输设备" :value="3" />
            <el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width:120px" @change="loadList">
            <el-option label="在用" :value="1" />
            <el-option label="停用" :value="0" />
            <el-option label="已处置" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="编号/名称" clearable style="width:160px" @change="loadList" />
        </el-form-item>
      </el-form>

      <!-- 汇总卡片 -->
      <el-row :gutter="12" class="summary-cards">
        <el-col :span="6">
          <div class="sum-card">
            <div class="sum-label">资产总数</div>
            <div class="sum-value">{{ tableData.length }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="sum-card">
            <div class="sum-label">原值合计</div>
            <div class="sum-value blue">¥ {{ totalOriginal }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="sum-card">
            <div class="sum-label">累计折旧</div>
            <div class="sum-value orange">¥ {{ totalDepreciation }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="sum-card">
            <div class="sum-label">净值合计</div>
            <div class="sum-value green">¥ {{ totalNetValue }}</div>
          </div>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" border stripe size="small">
        <el-table-column prop="assetNo" label="资产编号" width="120" />
        <el-table-column prop="assetName" label="资产名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="assetType" label="类型" width="90">
          <template #default="{ row }">{{ assetTypeLabel(row.assetType) }}</template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="入账日期" width="110" />
        <el-table-column prop="originalValue" label="原值(元)" width="130" :formatter="fmtMoney" align="right" />
        <el-table-column prop="accumulatedDepreciation" label="累计折旧(元)" width="130" :formatter="fmtMoney" align="right" />
        <el-table-column prop="netValue" label="净值(元)" width="130" align="right">
          <template #default="{ row }">
            <span :class="{ 'low-value': row.netValue / row.originalValue < 0.1 }">
              {{ fmtMoney(row, null, row.netValue) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="usefulLifeMonths" label="预计月份" width="90" align="center" />
        <el-table-column prop="lastDeprecDate" label="最后折旧" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize"
        :total="total" layout="total, sizes, prev, pager, next" @change="loadList" class="pagination" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="editRow ? '编辑固定资产' : '新增固定资产'" width="560px">
      <el-form ref="formRef" :model="formData" label-width="90px" :rules="rules">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产编号" prop="assetNo">
              <el-input v-model="formData.assetNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="formData.assetType" style="width:100%">
                <el-option label="房屋建筑" :value="1" />
                <el-option label="机器设备" :value="2" />
                <el-option label="运输设备" :value="3" />
                <el-option label="其他" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="资产名称" prop="assetName">
          <el-input v-model="formData.assetName" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="入账日期" prop="purchaseDate">
              <el-date-picker v-model="formData.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原值(元)" prop="originalValue">
              <el-input-number v-model="formData.originalValue" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="残值率(%)">
              <el-input-number v-model="formData.residualRatePercent" :min="0" :max="100" :precision="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用月份">
              <el-input-number v-model="formData.usefulLifeMonths" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <!-- 计提折旧弹窗 -->
    <el-dialog v-model="deprecVisible" title="计提折旧" width="400px">
      <el-form label-width="90px">
        <el-form-item label="折旧期间">
          <el-date-picker v-model="deprecPeriod" type="month" format="YYYY年MM月" value-format="YYYYMM" style="width:100%" />
        </el-form-item>
        <el-alert type="warning" :closable="false" description="计提折旧将按平均年限法更新所有在用资产的累计折旧和净值，请谨慎操作。" />
      </el-form>
      <template #footer>
        <el-button @click="deprecVisible = false">取消</el-button>
        <el-button type="warning" :loading="deprecating" @click="runDepreciate">确认计提</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAssets, createAsset, updateAsset, deleteAsset, depreciate, getAccountSets } from '@/api/finance'
import dayjs from 'dayjs'

const loading = ref(false)
const saving = ref(false)
const deprecating = ref(false)
const tableData = ref([])
const total = ref(0)
const accountSets = ref([])
const formVisible = ref(false)
const deprecVisible = ref(false)
const editRow = ref(null)
const deprecPeriod = ref(dayjs().format('YYYYMM'))

const query = reactive({ accountSetId: null, assetType: null, status: null, keyword: '', pageNum: 1, pageSize: 20 })
const formData = reactive({ assetNo: '', assetName: '', assetType: 1, purchaseDate: dayjs().format('YYYY-MM-DD'), originalValue: 0, residualRatePercent: 5, usefulLifeMonths: 60, remark: '' })

const rules = {
  assetNo: [{ required: true, message: '请输入资产编号' }],
  assetName: [{ required: true, message: '请输入资产名称' }],
  assetType: [{ required: true }],
  purchaseDate: [{ required: true, message: '请选择入账日期' }],
  originalValue: [{ required: true }],
}

const totalOriginal = computed(() => tableData.value.reduce((s, r) => s + (r.originalValue || 0), 0).toLocaleString('zh-CN', { maximumFractionDigits: 0 }))
const totalDepreciation = computed(() => tableData.value.reduce((s, r) => s + (r.accumulatedDepreciation || 0), 0).toLocaleString('zh-CN', { maximumFractionDigits: 0 }))
const totalNetValue = computed(() => tableData.value.reduce((s, r) => s + (r.netValue || 0), 0).toLocaleString('zh-CN', { maximumFractionDigits: 0 }))

const assetTypeLabel = (v) => ({ 1: '房屋建筑', 2: '机器设备', 3: '运输设备', 4: '其他' })[v] || '--'
const statusLabel = (v) => ({ 0: '停用', 1: '在用', 2: '已处置' })[v] || '--'
const statusType = (v) => ({ 0: 'info', 1: 'success', 2: 'danger' })[v] || ''
const fmtMoney = (row, col, val) => val != null ? Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 }) : '--'

async function loadList() {
  if (!query.accountSetId) return
  loading.value = true
  try {
    const res = await getAssets(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function openForm(row = null) {
  editRow.value = row
  if (row) {
    Object.assign(formData, { ...row, residualRatePercent: (row.residualRate || 0.05) * 100, id: row.id })
  } else {
    Object.assign(formData, { assetNo: '', assetName: '', assetType: 1, purchaseDate: dayjs().format('YYYY-MM-DD'), originalValue: 0, residualRatePercent: 5, usefulLifeMonths: 60, remark: '' })
  }
  formVisible.value = true
}

function openDeprecDialog() {
  deprecPeriod.value = dayjs().format('YYYYMM')
  deprecVisible.value = true
}

async function save() {
  saving.value = true
  try {
    const payload = { ...formData, accountSetId: query.accountSetId, residualRate: formData.residualRatePercent / 100 }
    if (editRow.value) await updateAsset(payload)
    else await createAsset(payload)
    ElMessage.success('保存成功')
    formVisible.value = false
    loadList()
  } finally { saving.value = false }
}

async function del(id) {
  await ElMessageBox.confirm('确认删除该资产？', '警告', { type: 'error' })
  await deleteAsset(id)
  ElMessage.success('删除成功')
  loadList()
}

async function runDepreciate() {
  deprecating.value = true
  try {
    await depreciate({ accountSetId: query.accountSetId, period: deprecPeriod.value })
    ElMessage.success('折旧计提成功')
    deprecVisible.value = false
    loadList()
  } finally { deprecating.value = false }
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
.header-actions { display: flex; gap: 8px; }
.search-form { margin-bottom: 8px; }

.summary-cards { margin-bottom: 16px; }
.sum-card { background: #f5f7fa; border-radius: 8px; padding: 12px 16px; text-align: center; }
.sum-label { font-size: 12px; color: #909399; margin-bottom: 4px; }
.sum-value { font-size: 18px; font-weight: 700; color: #303133; }
.sum-value.blue { color: #409eff; }
.sum-value.orange { color: #e6a23c; }
.sum-value.green { color: #67c23a; }

.low-value { color: #f56c6c; font-weight: 700; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>
