<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span class="logo-icon">🌾</span>
        <span v-show="!sidebarCollapsed" class="logo-text">三农经济平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        background-color="#1a3a2a"
        text-color="#b7d5c3"
        active-text-color="#52b788"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/finance-map/overview">
          <el-icon><Location /></el-icon>
          <template #title>财务一张图</template>
        </el-menu-item>
        <el-sub-menu index="village-finance">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>村委财务管理</span>
          </template>
          <el-sub-menu index="cashier">
            <template #title>
              <el-icon><Wallet /></el-icon>
              <span>出纳管理</span>
            </template>
            <el-menu-item index="/village-finance/cashier/bank">银行日记账</el-menu-item>
            <el-menu-item index="/village-finance/cashier/cash">现金日记账</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/village-finance/voucher">
            <el-icon><Document /></el-icon>
            <template #title>凭证管理</template>
          </el-menu-item>
          <el-sub-menu index="ledger">
            <template #title>
              <el-icon><Notebook /></el-icon>
              <span>账簿查询</span>
            </template>
            <el-menu-item index="/village-finance/ledger/sequential">序时账</el-menu-item>
            <el-menu-item index="/village-finance/ledger/subject-balance">科目余额表</el-menu-item>
            <el-menu-item index="/village-finance/ledger/detail">明细账</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="report">
            <template #title>
              <el-icon><PieChart /></el-icon>
              <span>报表中心</span>
            </template>
            <el-menu-item index="/village-finance/report/balance-sheet">资产负债表</el-menu-item>
            <el-menu-item index="/village-finance/report/income">收益分配表</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/village-finance/asset">
            <el-icon><Box /></el-icon>
            <template #title>固定资产</template>
          </el-menu-item>
          <el-menu-item index="/village-finance/settings/account-set">
            <el-icon><Setting /></el-icon>
            <template #title>账套设置</template>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
            <component :is="sidebarCollapsed ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" style="background:#40916c">{{ userStore.realName?.[0] }}</el-avatar>
              <span class="username">{{ userStore.realName || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const sidebarCollapsed = ref(false)

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta?.title || '')

async function handleCommand(command) {
  if (command === 'logout') {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
    userStore.logout()
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background: #1a3a2a;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo-icon { font-size: 24px; }

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.header-right .user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.username { font-size: 14px; }

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
