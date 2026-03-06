<template>
  <div class="contract-list">
    <div v-for="item in data" :key="item.id" class="contract-row">
      <div class="c-status" :class="item.status === 'urgent' ? 'urgent' : 'normal'">
        {{ item.status === 'urgent' ? '紧急' : '正常' }}
      </div>
      <div class="c-body">
        <div class="c-name">{{ item.name }}</div>
        <div class="c-meta">
          <span>{{ item.region }}</span>
          <span>到期：{{ item.expire }}</span>
          <span class="c-days" :class="item.status === 'urgent' ? 'urgent' : ''">
            剩 {{ item.daysLeft }} 天
          </span>
        </div>
      </div>
      <div class="c-amount">{{ item.amount }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({ data: Array })
</script>

<style scoped>
.contract-list {
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  height: 100%;
  overflow-y: auto;
}
.contract-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 8px;
  background: rgba(255,255,255,0.02);
  border-radius: 3px;
  border: 1px solid rgba(255,255,255,0.04);
  transition: background 0.2s;
}
.contract-row:hover { background: rgba(255,255,255,0.05); }

.c-status {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 2px;
  white-space: nowrap;
  flex-shrink: 0;
}
.c-status.urgent { background: rgba(255,77,79,0.2);  color: #ff4d4f; border: 1px solid rgba(255,77,79,0.3); }
.c-status.normal { background: rgba(0,200,255,0.1);  color: #00c8ff; border: 1px solid rgba(0,200,255,0.2); }

.c-body { flex: 1; min-width: 0; }
.c-name {
  font-size: 12px;
  color: var(--color-text-title);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 2px;
}
.c-meta {
  display: flex;
  gap: 10px;
  font-size: 10px;
  color: var(--color-text-muted);
}
.c-days { font-weight: 600; color: var(--color-text-muted); }
.c-days.urgent { color: var(--color-warn); }

.c-amount {
  font-size: 12px;
  font-weight: 600;
  color: var(--color-accent);
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
