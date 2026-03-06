<template>
  <div ref="chartEl" class="trend-chart"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const chartEl = ref(null)
let chart = null

const months = ['10月', '11月', '12月', '1月', '2月', '3月']
const seriesData = [
  { name: '经营性收入', data: [2840, 3120, 2960, 3540, 3820, 4120], color: '#00c8ff' },
  { name: '资产性收入', data: [1240, 1380, 1290, 1480, 1610, 1750], color: '#00ff99' },
  { name: '转移性收入', data: [620,  680,  710,  690,  740,  780],  color: '#ffb400' },
]

onMounted(() => {
  chart = echarts.init(chartEl.value, null, { renderer: 'canvas' })
  chart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 28, right: 14, bottom: 36, left: 46, containLabel: false },
    legend: {
      top: 4, right: 0,
      textStyle: { color: 'rgba(184,212,240,0.65)', fontSize: 10 },
      icon: 'circle', itemWidth: 7, itemHeight: 7,
    },
    xAxis: {
      type: 'category',
      data: months,
      axisLine:  { lineStyle: { color: 'rgba(0,200,255,0.2)' } },
      axisTick:  { show: false },
      axisLabel: { color: 'rgba(184,212,240,0.6)', fontSize: 10 },
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(0,200,255,0.08)', type: 'dashed' } },
      axisLabel: { color: 'rgba(184,212,240,0.5)', fontSize: 10, formatter: v => v >= 1000 ? (v/1000).toFixed(1)+'k' : v },
    },
    series: seriesData.map(s => ({
      name: s.name,
      type: 'line',
      data: s.data,
      smooth: true,
      symbol: 'circle',
      symbolSize: 4,
      lineStyle: { color: s.color, width: 1.5 },
      itemStyle: { color: s.color },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: s.color + '30' },
          { offset: 1, color: s.color + '05' },
        ])
      },
    })),
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(2,13,31,0.92)',
      borderColor: 'rgba(0,200,255,0.3)',
      textStyle: { color: '#e8f4ff', fontSize: 11 },
    }
  })

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
.trend-chart { width: 100%; height: 100%; }
</style>
