<template>
  <div class="dashboard">

    <!-- ===== 顶部 Header ===== -->
    <header class="dash-header">
      <div class="header-left">
        <div class="header-logo">
          <span class="logo-mark">农</span>
        </div>
        <div class="header-info">
          <div class="header-sub">海南省数字三农综合管理平台</div>
        </div>
      </div>

      <div class="header-center">
        <div class="header-title">农经一张图数据大屏</div>
        <div class="header-line-deco">
          <span></span><span class="deco-dot"></span><span></span>
        </div>
      </div>

      <div class="header-right">
        <div class="header-meta">
          <div class="meta-item">
            <span class="meta-label">数据更新</span>
            <span class="meta-val">{{ currentDate }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">当前时间</span>
            <span class="meta-val meta-time">{{ currentTime }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">数据状态</span>
            <span class="meta-val status-ok">
              <i class="status-dot"></i>实时在线
            </span>
          </div>
        </div>
      </div>
    </header>

    <!-- ===== 总览指标条 ===== -->
    <section class="stats-bar">
      <StatCard
        v-for="(stat, key) in overviewStats"
        :key="key"
        :label="stat.label"
        :value="stat.value"
        :unit="stat.unit"
        :bar-pct="barPcts[key]"
      />
    </section>

    <!-- ===== 主内容区（三栏）===== -->
    <main class="dash-main">

      <!-- 左栏 -->
      <aside class="col-left">
        <PanelCard title="收入阈值分布" class="panel-threshold">
          <IncomeThresholdChart :data="incomeThreshold" />
        </PanelCard>

        <PanelCard title="财务健康度雷达" class="panel-radar">
          <RadarChart :data="financialRadar" />
        </PanelCard>

        <PanelCard title="合同到期提醒" class="panel-contract">
          <ContractExpiry :data="contractExpiry" />
        </PanelCard>
      </aside>

      <!-- 中栏 · 地图 -->
      <section class="col-center">
        <PanelCard class="panel-map">
          <MapChart />
        </PanelCard>
      </section>

      <!-- 右栏 -->
      <aside class="col-right">
        <PanelCard title="预警汇总面板" class="panel-warning">
          <WarningPanel :data="warningList" />
        </PanelCard>

        <PanelCard title="区划分布概览" class="panel-region">
          <RegionOverview />
        </PanelCard>

        <PanelCard title="收入趋势（近6月）" class="panel-trend">
          <TrendChart />
        </PanelCard>
      </aside>

    </main>

    <!-- 底部装饰线 -->
    <div class="dash-footer">
      <span>海南省农业农村厅 · 村级集体经济管理系统</span>
      <span>数据来源：各市县农经管理平台实时快照</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import PanelCard            from '@/components/PanelCard.vue'
import StatCard             from '@/components/StatCard.vue'
import IncomeThresholdChart from '@/components/IncomeThresholdChart.vue'
import RadarChart           from '@/components/RadarChart.vue'
import ContractExpiry       from '@/components/ContractExpiry.vue'
import MapChart             from '@/components/MapChart.vue'
import WarningPanel         from '@/components/WarningPanel.vue'
import RegionOverview       from '@/components/RegionOverview.vue'
import TrendChart           from '@/components/TrendChart.vue'

import {
  overviewStats,
  incomeThreshold,
  financialRadar,
  warningList,
  contractExpiry
} from '@/mock/index.js'

// 时钟
const currentTime = ref('')
const currentDate = ref('')
let timer = null

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}
onMounted(() => { updateTime(); timer = setInterval(updateTime, 1000) })
onBeforeUnmount(() => clearInterval(timer))

// 进度条百分比（mock）
const barPcts = { totalAssets: 82, totalResources: 68, totalContracts: 91, totalMembers: 75, totalEquity: 60 }
</script>

<style scoped>
/* ===== 整体框架 ===== */
.dashboard {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background:
    radial-gradient(ellipse at 20% 0%, rgba(0,100,200,0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 100%, rgba(0,180,100,0.05) 0%, transparent 50%),
    var(--color-bg-base);
}

/* ===== Header ===== */
.dash-header {
  height: 64px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: linear-gradient(180deg, rgba(0,30,80,0.95) 0%, rgba(2,13,31,0.85) 100%);
  border-bottom: 1px solid var(--color-border);
  position: relative;
  z-index: 10;
}
.dash-header::after {
  content: '';
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--color-primary) 30%, var(--color-accent) 70%, transparent 100%);
  opacity: 0.5;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 280px;
}
.header-logo {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, rgba(0,200,255,0.3), rgba(0,255,153,0.2));
  border: 1px solid var(--color-border-glow);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 0 12px rgba(0,200,255,0.2);
}
.logo-mark {
  font-size: 16px;
  font-weight: 900;
  background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.header-info { display: flex; flex-direction: column; gap: 2px; }
.header-sub  { font-size: 11px; color: var(--color-text-muted); letter-spacing: 0.5px; }

.header-center {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.header-title {
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 4px;
  background: linear-gradient(90deg, #a0d8ff 0%, var(--color-primary) 40%, var(--color-accent) 70%, #a0d8ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: none;
  white-space: nowrap;
  filter: drop-shadow(0 0 8px rgba(0,200,255,0.4));
}
.header-line-deco {
  display: flex;
  align-items: center;
  gap: 0;
  width: 200px;
}
.header-line-deco span:not(.deco-dot) {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-primary));
}
.header-line-deco span:last-child {
  background: linear-gradient(90deg, var(--color-primary), transparent);
}
.deco-dot {
  flex: none !important;
  width: 5px; height: 5px;
  border-radius: 50%;
  background: var(--color-primary);
  box-shadow: 0 0 6px var(--color-primary);
  margin: 0 6px;
}

.header-right { width: 280px; display: flex; justify-content: flex-end; }
.header-meta  { display: flex; gap: 16px; align-items: center; }
.meta-item    { display: flex; flex-direction: column; align-items: center; gap: 2px; }
.meta-label   { font-size: 10px; color: var(--color-text-muted); }
.meta-val     { font-size: 12px; color: var(--color-text-title); font-variant-numeric: tabular-nums; }
.meta-time    { font-size: 14px; color: var(--color-primary); font-weight: 700; letter-spacing: 1px; }
.status-ok    { color: var(--color-accent) !important; display: flex; align-items: center; gap: 4px; }
.status-dot   {
  width: 6px; height: 6px;
  background: var(--color-accent);
  border-radius: 50%;
  box-shadow: 0 0 6px var(--color-accent);
  animation: blink 2s infinite;
}
@keyframes blink { 0%,100%{opacity:1} 50%{opacity:0.4} }

/* ===== 总览指标条 ===== */
.stats-bar {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  padding: 8px 14px;
  background: rgba(2,13,31,0.6);
  border-bottom: 1px solid var(--color-border);
}

/* ===== 主三栏 ===== */
.dash-main {
  flex: 1;
  display: grid;
  grid-template-columns: 300px 1fr 300px;
  gap: 8px;
  padding: 8px 14px;
  overflow: hidden;
  min-height: 0;
}

/* 左栏 */
.col-left, .col-right {
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;
  min-height: 0;
}

.panel-threshold { flex: 0 0 150px; }
.panel-radar     { flex: 0 0 220px; }
.panel-contract  { flex: 1; min-height: 0; }

/* 中栏 */
.col-center { display: flex; flex-direction: column; min-height: 0; }
.panel-map  { flex: 1; min-height: 0; }

/* 右栏 */
.panel-warning { flex: 0 0 220px; }
.panel-region  { flex: 0 0 160px; }
.panel-trend   { flex: 1; min-height: 0; }

/* ===== 底部 ===== */
.dash-footer {
  flex-shrink: 0;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  font-size: 10px;
  color: var(--color-text-muted);
  border-top: 1px solid var(--color-border);
  background: rgba(2,13,31,0.8);
  letter-spacing: 0.5px;
}
</style>
