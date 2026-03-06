import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/finance-map/overview',
      },
      // 财务一张图
      {
        path: 'finance-map/overview',
        name: 'FinanceMap',
        component: () => import('@/views/finance-map/FinanceMap.vue'),
        meta: { title: '财务一张图' },
      },
      // 村委财务管理
      {
        path: 'village-finance',
        component: () => import('@/views/village-finance/FinanceLayout.vue'),
        meta: { title: '村委财务管理' },
        children: [
          { path: '', redirect: 'voucher' },
          { path: 'cashier/bank', name: 'BankJournal', component: () => import('@/views/village-finance/cashier/BankJournal.vue'), meta: { title: '银行日记账' } },
          { path: 'cashier/cash', name: 'CashJournal', component: () => import('@/views/village-finance/cashier/CashJournal.vue'), meta: { title: '现金日记账' } },
          { path: 'voucher', name: 'VoucherList', component: () => import('@/views/village-finance/voucher/VoucherList.vue'), meta: { title: '凭证管理' } },
          { path: 'ledger/sequential', name: 'SequentialLedger', component: () => import('@/views/village-finance/ledger/SequentialLedger.vue'), meta: { title: '序时账' } },
          { path: 'ledger/subject-balance', name: 'SubjectBalance', component: () => import('@/views/village-finance/ledger/SubjectBalance.vue'), meta: { title: '科目余额表' } },
          { path: 'ledger/detail', name: 'DetailLedger', component: () => import('@/views/village-finance/ledger/DetailLedger.vue'), meta: { title: '明细账' } },
          { path: 'report/balance-sheet', name: 'BalanceSheet', component: () => import('@/views/village-finance/report/BalanceSheet.vue'), meta: { title: '资产负债表' } },
          { path: 'report/income', name: 'IncomeStatement', component: () => import('@/views/village-finance/report/IncomeStatement.vue'), meta: { title: '收益分配表' } },
          { path: 'asset', name: 'AssetList', component: () => import('@/views/village-finance/asset/AssetList.vue'), meta: { title: '固定资产' } },
          { path: 'settings/account-set', name: 'AccountSet', component: () => import('@/views/village-finance/settings/AccountSet.vue'), meta: { title: '账套设置' } },
        ],
      },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

NProgress.configure({ showSpinner: false })

router.beforeEach((to, from, next) => {
  NProgress.start()
  const userStore = useUserStore()
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

router.afterEach((to) => {
  NProgress.done()
  if (to.meta.title) {
    document.title = `${to.meta.title} - 三农经济平台`
  }
})

export default router
