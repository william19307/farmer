<template>
  <div class="panel-card">
    <div class="panel-header" v-if="title">
      <span class="header-icon"></span>
      <span class="header-title">{{ title }}</span>
      <span class="header-line"></span>
    </div>
    <div class="panel-body">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({ title: String })
</script>

<style scoped>
.panel-card {
  position: relative;
  background: var(--color-bg-panel);
  border: 1px solid var(--color-border);
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-panel);
}
.panel-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--color-primary), transparent);
  opacity: 0.6;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px 8px;
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}
.header-icon {
  width: 3px;
  height: 14px;
  background: linear-gradient(to bottom, var(--color-primary), var(--color-accent));
  border-radius: 2px;
  flex-shrink: 0;
  box-shadow: 0 0 6px var(--color-primary);
}
.header-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-title);
  letter-spacing: 1px;
  white-space: nowrap;
}
.header-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, var(--color-border-glow), transparent);
  margin-left: 6px;
}

.panel-body {
  flex: 1;
  overflow: hidden;
  position: relative;
}
</style>
