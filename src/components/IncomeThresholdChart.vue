<template>
  <div class="threshold-wrap">
    <div
      v-for="item in data"
      :key="item.range"
      class="threshold-row"
    >
      <div class="row-label">{{ item.range }}</div>
      <div class="row-bar-wrap">
        <div
          class="row-bar"
          :style="{ width: item.pct + '%', background: item.color, boxShadow: `0 0 8px ${item.color}80` }"
        ></div>
      </div>
      <div class="row-pct" :style="{ color: item.color }">{{ item.pct }}%</div>
      <div class="row-count">{{ item.count.toLocaleString() }}村</div>
    </div>
  </div>
</template>

<script setup>
defineProps({ data: Array })
</script>

<style scoped>
.threshold-wrap {
  padding: 10px 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
  justify-content: center;
}
.threshold-row {
  display: grid;
  grid-template-columns: 64px 1fr 40px 60px;
  align-items: center;
  gap: 8px;
}
.row-label {
  font-size: 11px;
  color: var(--color-text-muted);
  text-align: right;
}
.row-bar-wrap {
  background: rgba(255,255,255,0.05);
  border-radius: 2px;
  height: 8px;
  overflow: hidden;
}
.row-bar {
  height: 100%;
  border-radius: 2px;
  transition: width 1.2s ease;
}
.row-pct {
  font-size: 12px;
  font-weight: 600;
  text-align: right;
}
.row-count {
  font-size: 11px;
  color: var(--color-text-muted);
}
</style>
