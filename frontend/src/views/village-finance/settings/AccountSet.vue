<template>
  <div class="page-container">
    <el-row :gutter="20">
      <!-- 左：账套列表 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>账套管理</span>
              <el-button type="primary" size="small" :icon="Plus" @click="openAccountSetForm()">新增</el-button>
            </div>
          </template>

          <div
            v-for="s in accountSets"
            :key="s.id"
            class="set-item"
            :class="{ active: selectedSet?.id === s.id }"
            @click="selectSet(s)"
          >
            <div class="set-name">{{ s.setName }}</div>
            <div class="set-meta">{{ s.accountYear }}年度 · {{ s.currency }}</div>
            <el-tag :type="s.status === 1 ? 'success' : 'info'" size="small">
              {{ s.status === 1 ? '启用' : '停用' }}
            </el-tag>
            <el-button link size="small" style="margin-left:4px" @click.stop="openAccountSetForm(s)">编辑</el-button>
          </div>

          <el-empty v-if="!accountSets.length" description="暂无账套" :image-size="80" />
        </el-card>
      </el-col>

      <!-- 右：科目管理 -->
      <el-col :span="16">
        <el-card v-if="selectedSet">
          <template #header>
            <div class="card-header">
              <span>会计科目 — {{ selectedSet.setName }}</span>
              <el-button type="primary" size="small" :icon="Plus" @click="openAccountForm()">新增科目</el-button>
            </div>
          </template>

          <el-table
            :data="accountTree"
            v-loading="accountLoading"
            row-key="id"
            :tree-props="{ children: 'children' }"
            border
            size="small"
            default-expand-all
          >
            <el-table-column prop="accountCode" label="科目编码" width="120" />
            <el-table-column prop="accountName" label="科目名称" min-width="140" />
            <el-table-column prop="accountType" label="科目类型" width="100">
              <template #default="{ row }">{{ accountTypeLabel(row.accountType) }}</template>
            </el-table-column>
            <el-table-column prop="balanceDirection" label="余额方向" width="90">
              <template #default="{ row }">{{ row.balanceDirection === 'debit' ? '借' : '贷' }}</template>
            </el-table-column>
            <el-table-column prop="level" label="级次" width="60" align="center" />
            <el-table-column prop="initBalance" label="期初余额" width="120" align="right">
              <template #default="{ row }">{{ fmtMoney(row.initBalance) }}</template>
            </el-table-column>
            <el-table-column prop="isLeaf" label="末级" width="60" align="center">
              <template #default="{ row }">
                <el-tag :type="row.isLeaf ? 'success' : 'info'" size="small">{{ row.isLeaf ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-empty v-else description="请从左侧选择账套" :image-size="120" style="margin-top:60px" />
      </el-col>
    </el-row>

    <!-- 账套表单弹窗 -->
    <el-dialog v-model="setFormVisible" :title="editSet ? '编辑账套' : '新增账套'" width="480px">
      <el-form ref="setFormRef" :model="setFormData" label-width="90px" :rules="setRules">
        <el-form-item label="账套名称" prop="setName">
          <el-input v-model="setFormData.setName" placeholder="如：某村2026年度账套" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="会计年度" prop="accountYear">
              <el-input-number v-model="setFormData.accountYear" :min="2000" :max="2099" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="本位币">
              <el-select v-model="setFormData.currency" style="width:100%">
                <el-option label="人民币(CNY)" value="CNY" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="村庄ID" prop="villageId">
          <el-input-number v-model="setFormData.villageId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="区划ID" prop="regionId">
          <el-input-number v-model="setFormData.regionId" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="setFormData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="setFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="setFormSaving" @click="saveAccountSet">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAccountSets, createAccountSet, updateAccountSet, getAccounts } from '@/api/finance'
import dayjs from 'dayjs'

const accountSets = ref([])
const selectedSet = ref(null)
const accountTree = ref([])
const accountLoading = ref(false)
const setFormVisible = ref(false)
const setFormSaving = ref(false)
const editSet = ref(null)
const setFormRef = ref()

const setFormData = reactive({
  setName: '',
  accountYear: dayjs().year(),
  currency: 'CNY',
  villageId: null,
  regionId: null,
  status: 1,
})

const setRules = {
  setName: [{ required: true, message: '请输入账套名称' }],
  accountYear: [{ required: true }],
  villageId: [{ required: true, message: '请输入村庄ID' }],
  regionId: [{ required: true, message: '请输入区划ID' }],
}

const accountTypeLabel = (v) => ({ 1: '资产', 2: '负债', 3: '所有者权益', 4: '收入', 5: '支出' })[v] || '--'
const fmtMoney = (val) => val != null && val !== 0 ? Number(val).toLocaleString('zh-CN', { minimumFractionDigits: 2 }) : ''

async function loadAccountSets() {
  const res = await getAccountSets()
  accountSets.value = res.data || []
}

async function selectSet(s) {
  selectedSet.value = s
  accountLoading.value = true
  try {
    const res = await getAccounts(s.id)
    accountTree.value = buildTree(res.data || [])
  } finally {
    accountLoading.value = false
  }
}

function buildTree(list) {
  const map = {}
  list.forEach(item => { map[item.id] = { ...item, children: [] } })
  const roots = []
  list.forEach(item => {
    const node = map[item.id]
    if (item.parentId && map[item.parentId]) {
      map[item.parentId].children.push(node)
    } else {
      roots.push(node)
    }
  })
  const clean = (nodes) => nodes.map(n => {
    if (!n.children.length) delete n.children
    else n.children = clean(n.children)
    return n
  })
  return clean(roots)
}

function openAccountSetForm(s = null) {
  editSet.value = s
  if (s) {
    Object.assign(setFormData, { setName: s.setName, accountYear: s.accountYear, currency: s.currency, villageId: s.villageId, regionId: s.regionId, status: s.status, id: s.id })
  } else {
    Object.assign(setFormData, { setName: '', accountYear: dayjs().year(), currency: 'CNY', villageId: null, regionId: null, status: 1 })
  }
  setFormVisible.value = true
}

function openAccountForm() {
  ElMessage.info('科目新增功能开发中')
}

async function saveAccountSet() {
  await setFormRef.value.validate()
  setFormSaving.value = true
  try {
    if (editSet.value) await updateAccountSet(setFormData)
    else await createAccountSet(setFormData)
    ElMessage.success('保存成功')
    setFormVisible.value = false
    await loadAccountSets()
  } finally { setFormSaving.value = false }
}

onMounted(async () => {
  await loadAccountSets()
  if (accountSets.value.length) selectSet(accountSets.value[0])
})
</script>

<style scoped>
.card-header { display: flex; align-items: center; justify-content: space-between; }

.set-item {
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.2s;
  position: relative;
}
.set-item:hover { border-color: #40916c; background: #f6fffa; }
.set-item.active { border-color: #40916c; background: #f0f9f4; box-shadow: 0 0 0 2px rgba(64,145,108,0.15); }
.set-name { font-weight: 600; color: #303133; margin-bottom: 4px; }
.set-meta { font-size: 12px; color: #909399; margin-bottom: 6px; }
</style>
