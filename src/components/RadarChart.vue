<template>
  <div ref="chartEl" class="radar-chart"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({ data: Object })
const chartEl = ref(null)
let chart = null

onMounted(() => {
  chart = echarts.init(chartEl.value, null, { renderer: 'canvas' })
  chart.setOption({
    backgroundColor: 'transparent',
    legend: {
      bottom: 4,
      textStyle: { color: 'rgba(184,212,240,0.7)', fontSize: 11 },
      icon: 'circle',
      itemWidth: 8,
      itemHeight: 8,
    },
    radar: {
      indicator: props.data.indicators,
      shape: 'polygon',
      radius: '58%',
      center: ['50%', '46%'],
      nameGap: 6,
      axisName: { color: 'rgba(184,212,240,0.8)', fontSize: 11 },
      splitLine: { lineStyle: { color: 'rgba(0,200,255,0.12)', width: 1 } },
      splitArea: { areaStyle: { color: ['rgba(0,200,255,0.03)', 'rgba(0,200,255,0.06)'] } },
      axisLine:  { lineStyle: { color: 'rgba(0,200,255,0.15)' } },
    },
    series: [{
      type: 'radar',
      data: props.data.series.map(s => ({
        name: s.name,
        value: s.value,
        lineStyle: { color: s.color, width: 1.5 },
        areaStyle: { color: s.color + '22' },
        itemStyle: { color: s.color }
      }))
    }]
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
.radar-chart { width: 100%; height: 100%; }
</style>
