<template>
  <div class="map-container">
    <div ref="chartEl" class="map-echarts"></div>

    <!-- 地图标题浮层 -->
    <div class="map-overlay-title">
      <span class="dot-pulse"></span>
      全省村集体经济收入分布图
    </div>

    <!-- 图例 -->
    <div class="map-legend">
      <div class="legend-title">年收入（万元）</div>
      <div v-for="(item, i) in legend" :key="i" class="legend-row">
        <div class="legend-dot" :style="{ background: item.color }"></div>
        <span>{{ item.label }}</span>
      </div>
    </div>

    <!-- 下钻提示 -->
    <div class="map-tip">点击区域可下钻查看详情</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { incomeMapData } from '@/mock/index.js'

const chartEl = ref(null)
let chart = null

const legend = [
  { label: '8000+',     color: '#ff4d4f' },
  { label: '5000-8000', color: '#ffb400' },
  { label: '2000-5000', color: '#00c8ff' },
  { label: '1000-2000', color: '#4a90d9' },
  { label: '<1000',     color: '#2d5a8a' },
]

function getColor(val) {
  if (val >= 8000) return '#ff4d4f'
  if (val >= 5000) return '#ffb400'
  if (val >= 2000) return '#00c8ff'
  if (val >= 1000) return '#4a90d9'
  return '#2d5a8a'
}

onMounted(async () => {
  // 加载海南地图 GeoJSON
  let geoJSON = null
  try {
    const resp = await fetch('/geo/hainan.json')
    geoJSON = await resp.json()
  } catch {
    // 若没有 GeoJSON，用散点图代替
  }

  chart = echarts.init(chartEl.value, null, { renderer: 'canvas' })

  if (geoJSON) {
    echarts.registerMap('hainan', geoJSON)
    chart.setOption({
      backgroundColor: 'transparent',
      geo: {
        map: 'hainan',
        roam: true,
        label: { show: false },
        itemStyle: {
          areaColor: 'rgba(4,40,100,0.6)',
          borderColor: 'rgba(0,200,255,0.4)',
          borderWidth: 1,
        },
        emphasis: {
          itemStyle: { areaColor: 'rgba(0,200,255,0.15)' },
          label: { show: true, color: '#e8f4ff', fontSize: 12 }
        }
      },
      series: [{
        type: 'effectScatter',
        coordinateSystem: 'geo',
        data: incomeMapData.map(d => ({
          name: d.name,
          value: [d.lng, d.lat, d.value],
        })),
        symbolSize: val => Math.max(8, Math.min(22, val[2] / 500)),
        itemStyle: { color: item => getColor(item.value[2]) },
        rippleEffect: { brushType: 'fill', period: 3, scale: 2.5 },
        label: {
          show: true,
          formatter: '{b}',
          position: 'right',
          fontSize: 10,
          color: 'rgba(184,212,240,0.8)',
        },
        tooltip: { formatter: p => `${p.name}<br/>收入：${p.value[2]}万元` }
      }],
      tooltip: { trigger: 'item', backgroundColor: 'rgba(2,13,31,0.9)', borderColor: 'rgba(0,200,255,0.3)', textStyle: { color: '#e8f4ff' } }
    })
  } else {
    // Fallback：散点图在空白背景上
    chart.setOption({
      backgroundColor: 'transparent',
      xAxis: { show: false, min: 108, max: 112 },
      yAxis: { show: false, min: 18, max: 21 },
      series: [{
        type: 'scatter',
        data: incomeMapData.map(d => [d.lng, d.lat, d.value, d.name]),
        symbolSize: d => Math.max(10, Math.min(28, d[2] / 400)),
        itemStyle: { color: item => getColor(item.value[2]) },
        label: {
          show: true,
          formatter: p => p.value[3],
          position: 'right',
          fontSize: 11,
          color: 'rgba(184,212,240,0.85)',
        },
        tooltip: { formatter: p => `${p.value[3]}<br/>收入：${p.value[2]}万元` }
      }],
      tooltip: { trigger: 'item', backgroundColor: 'rgba(2,13,31,0.9)', borderColor: 'rgba(0,200,255,0.3)', textStyle: { color: '#e8f4ff' } }
    })
  }

  const ro = new ResizeObserver(() => chart?.resize())
  ro.observe(chartEl.value)
  chartEl.value._ro = ro
})

onBeforeUnmount(() => {
  chartEl.value?._ro?.disconnect()
  chart?.dispose()
})
</script>

<style scoped>
.map-container {
  position: relative;
  width: 100%;
  height: 100%;
  background: radial-gradient(ellipse at 50% 60%, rgba(0,80,160,0.18) 0%, transparent 70%);
}
.map-echarts { width: 100%; height: 100%; }

.map-overlay-title {
  position: absolute;
  top: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-title);
  letter-spacing: 1.5px;
  pointer-events: none;
  text-shadow: 0 0 12px rgba(0,200,255,0.6);
}
.dot-pulse {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: var(--color-accent);
  box-shadow: 0 0 8px var(--color-accent);
  animation: blink 2s infinite;
}
@keyframes blink { 0%,100%{opacity:1} 50%{opacity:0.3} }

.map-legend {
  position: absolute;
  bottom: 20px;
  left: 14px;
  background: rgba(2,13,31,0.75);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  padding: 8px 12px;
  pointer-events: none;
}
.legend-title {
  font-size: 10px;
  color: var(--color-text-muted);
  margin-bottom: 6px;
}
.legend-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
  font-size: 10px;
  color: rgba(184,212,240,0.7);
}
.legend-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }

.map-tip {
  position: absolute;
  bottom: 10px;
  right: 14px;
  font-size: 10px;
  color: var(--color-text-muted);
  pointer-events: none;
}
</style>
