<template>
  <div class="region-wrap">
    <div v-for="item in regions" :key="item.name" class="region-row">
      <span class="r-name">{{ item.name }}</span>
      <div class="r-bar-bg">
        <div class="r-bar" :style="{ width: item.pct + '%', background: item.color }"></div>
      </div>
      <span class="r-val">{{ item.income }}万</span>
      <span class="r-count">{{ item.villages }}村</span>
    </div>
  </div>
</template>

<script setup>
const regions = [
  { name: '海口市', income: 8420, villages: 312, pct: 100, color: '#ff4d4f' },
  { name: '三亚市', income: 6830, villages: 284, pct: 81,  color: '#ffb400' },
  { name: '儋州市', income: 4210, villages: 198, pct: 50,  color: '#00c8ff' },
  { name: '文昌市', income: 3640, villages: 176, pct: 43,  color: '#00c8ff' },
  { name: '琼海市', income: 3180, villages: 152, pct: 38,  color: '#4a90d9' },
]
</script>

<style scoped>
.region-wrap {
  padding: 8px 14px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
  height: 100%;
}
.region-row {
  display: grid;
  grid-template-columns: 52px 1fr 52px 40px;
  align-items: center;
  gap: 6px;
}
.r-name { font-size: 11px; color: var(--color-text-muted); text-align: right; }
.r-bar-bg {
  background: rgba(255,255,255,0.05);
  border-radius: 2px;
  height: 6px;
  overflow: hidden;
}
.r-bar { height: 100%; border-radius: 2px; transition: width 1s ease; }
.r-val   { font-size: 11px; color: var(--color-text-title); text-align: right; }
.r-count { font-size: 10px; color: var(--color-text-muted); }
</style>
