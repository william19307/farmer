<template>
  <div class="stat-card">
    <div class="stat-label">{{ label }}</div>
    <div class="stat-value-row">
      <span class="stat-value">{{ value }}</span>
      <span class="stat-unit">{{ unit }}</span>
    </div>
    <div class="stat-bar">
      <div class="stat-bar-fill" :style="{ width: barPct + '%' }"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  label: String,
  value: [String, Number],
  unit:  String,
  barPct: { type: Number, default: 75 }
})
</script>

<style scoped>
.stat-card {
  padding: 10px 14px;
  background: rgba(0, 200, 255, 0.04);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  transition: border-color 0.3s;
  cursor: default;
}
.stat-card:hover {
  border-color: var(--color-border-glow);
  background: rgba(0, 200, 255, 0.08);
}
.stat-label {
  font-size: 11px;
  color: var(--color-text-muted);
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}
.stat-value-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
}
.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-primary);
  text-shadow: 0 0 10px rgba(0,200,255,0.5);
  font-variant-numeric: tabular-nums;
}
.stat-unit {
  font-size: 11px;
  color: var(--color-text-muted);
}
.stat-bar {
  margin-top: 6px;
  height: 2px;
  background: rgba(0,200,255,0.1);
  border-radius: 1px;
  overflow: hidden;
}
.stat-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), var(--color-accent));
  border-radius: 1px;
  box-shadow: 0 0 4px var(--color-primary);
  transition: width 1s ease;
}
</style>
