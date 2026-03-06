<template>
  <el-dialog
    :title="voucherId ? '编辑凭证' : '新增凭证'"
    v-model="dialogVisible"
    width="900px"
    :close-on-click-modal="false"
    @close="$emit('close')"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-form-item label="凭证字" prop="voucherWord">
            <el-select v-model="form.voucherWord">
              <el-option label="记" value="记" />
              <el-option label="收" value="收" />
              <el-option label="付" value="付" />
              <el-option label="转" value="转" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="制单日期" prop="makeDate">
            <el-date-picker v-model="form.makeDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="附件数">
            <el-input-number v-model="form.attachCount" :min="0" style="width:100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="摘要">
        <el-input v-model="form.summary" placeholder="凭证摘要（选填）" />
      </el-form-item>

      <!-- 明细行 -->
      <div class="detail-header">
        <span class="detail-title">凭证明细</span>
        <el-button size="small" type="primary" :icon="Plus" @click="addDetail">添加行</el-button>
      </div>

      <el-table :data="form.details" class="detail-table">
        <el-table-column label="序号" width="60" type="index" />
        <el-table-column label="科目" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.accountId" filterable placeholder="选择科目" style="width:100%">
              <el-option
                v-for="a in accounts"
                :key="a.id"
                :label="`${a.accountCode} ${a.accountName}`"
                :value="a.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="摘要" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.summary" placeholder="摘要" />
          </template>
        </el-table-column>
        <el-table-column label="借方金额" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.debitAmount" :min="0" :precision="2" style="width:100%" @change="() => row.creditAmount = row.debitAmount > 0 ? 0 : row.creditAmount" />
          </template>
        </el-table-column>
        <el-table-column label="贷方金额" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.creditAmount" :min="0" :precision="2" style="width:100%" @change="() => row.debitAmount = row.creditAmount > 0 ? 0 : row.debitAmount" />
          </template>
        </el-table-column>
        <el-table-column label="" width="60">
          <template #default="{ $index }">
            <el-button link type="danger" :icon="Delete" @click="removeDetail($index)" />
          </template>
        </el-table-column>
      </el-table>

      <!-- 合计行 -->
      <div class="totals">
        <span>借方合计：<strong style="color:#409eff">{{ totalDebit.toFixed(2) }}</strong></span>
        <span style="margin-left:40px">贷方合计：<strong :style="{ color: isBalanced ? '#67c23a' : '#f56c6c' }">{{ totalCredit.toFixed(2) }}</strong></span>
        <el-tag v-if="!isBalanced" type="danger" size="small" style="margin-left:16px">借贷不平衡</el-tag>
        <el-tag v-else type="success" size="small" style="margin-left:16px">借贷平衡</el-tag>
      </div>
    </el-form>

    <template #footer>
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { getVoucherDetail, createVoucher, updateVoucher, getAccounts } from '@/api/finance'
import dayjs from 'dayjs'

const props = defineProps({
  voucherId: { type: Number, default: null },
  accountSetId: { type: Number, default: null },
})
const emit = defineEmits(['close', 'saved'])

const dialogVisible = ref(true)
const formRef = ref()
const saving = ref(false)
const accounts = ref([])

const form = reactive({
  voucherWord: '记',
  makeDate: dayjs().format('YYYY-MM-DD'),
  summary: '',
  attachCount: 0,
  details: [],
})

const rules = {
  makeDate: [{ required: true, message: '请选择制单日期', trigger: 'change' }],
}

const totalDebit = computed(() =>
  form.details.reduce((s, d) => s + (d.debitAmount || 0), 0)
)
const totalCredit = computed(() =>
  form.details.reduce((s, d) => s + (d.creditAmount || 0), 0)
)
const isBalanced = computed(() =>
  form.details.length >= 2 && Math.abs(totalDebit.value - totalCredit.value) < 0.001
)

function addDetail() {
  form.details.push({ accountId: null, summary: '', debitAmount: 0, creditAmount: 0 })
}

function removeDetail(index) {
  form.details.splice(index, 1)
}

async function save() {
  await formRef.value.validate()
  if (form.details.length < 2) {
    ElMessage.error('请至少添加两条凭证明细')
    return
  }
  saving.value = true
  try {
    const payload = {
      ...form,
      accountSetId: props.accountSetId,
      id: props.voucherId || undefined,
    }
    if (props.voucherId) {
      await updateVoucher(payload)
    } else {
      await createVoucher(payload)
    }
    ElMessage.success('保存成功')
    emit('saved')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (props.accountSetId) {
    const res = await getAccounts(props.accountSetId)
    accounts.value = (res.data || []).filter(a => a.isLeaf === 1)
  }
  if (props.voucherId) {
    const res = await getVoucherDetail(props.voucherId)
    const v = res.data
    Object.assign(form, {
      voucherWord: v.voucherWord,
      makeDate: v.makeDate,
      summary: v.summary,
      attachCount: v.attachCount,
      details: (v.details || []).map(d => ({
        id: d.id,
        accountId: d.accountId,
        summary: d.summary,
        debitAmount: d.debitAmount || 0,
        creditAmount: d.creditAmount || 0,
      })),
    })
  } else {
    addDetail()
    addDetail()
  }
})
</script>

<style scoped>
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 16px 0 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e8e8e8;
}
.detail-title { font-weight: 600; color: #303133; }
.detail-table { margin-bottom: 8px; }
.totals {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
}
</style>
