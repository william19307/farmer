<template>
  <div class="warning-list">
    <div
      v-for="item in data"
      :key="item.id"
      class="warning-item"
      :class="`level-${item.level}`"
    >
      <div class="warn-dot" :class="`dot-${item.level}`"></div>
      <div class="warn-body">
        <div class="warn-top">
          <span class="warn-type">{{ item.type }}</span>
          <span class="warn-region">{{ item.region }}</span>
          <span class="warn-time">{{ item.time }}</span>
        </div>
        <div class="warn-desc">{{ item.desc }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ data: Array })
</script>

<style scoped>
.warning-list {
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 7px;
  height: 100%;
  overflow-y: auto;
}
.warning-item {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding: 7px 10px;
  border-radius: 3px;
  border-left: 2px solid transparent;
  background: rgba(255,255,255,0.02);
  transition: background 0.2s;
}
.warning-item:hover { background: rgba(255,255,255,0.05); }

.level-high  { border-left-color: var(--color-danger); }
.level-mid   { border-left-color: var(--color-warn); }
.level-low   { border-left-color: var(--color-primary); }

.warn-dot {
  width: 6px; height: 6px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
  animation: blink 1.8s infinite;
}
.dot-high  { background: var(--color-danger);  box-shadow: 0 0 6px var(--color-danger); }
.dot-mid   { background: var(--color-warn);    box-shadow: 0 0 6px var(--color-warn); }
.dot-low   { background: var(--color-primary); box-shadow: 0 0 6px var(--color-primary); }

@keyframes blink {
  0%,100% { opacity: 1; } 50% { opacity: 0.3; }
}

.warn-body { flex: 1; min-width: 0; }
.warn-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 3px;
  flex-wrap: nowrap;
}
.warn-type {
  font-size: 11px;
  font-weight: 600;
  color: var(--color-text-title);
  white-space: nowrap;
}
.warn-region {
  font-size: 10px;
  color: var(--color-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}
.warn-time {
  font-size: 10px;
  color: var(--color-text-muted);
  white-space: nowrap;
}
.warn-desc {
  font-size: 11px;
  color: rgba(184,212,240,0.65);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
