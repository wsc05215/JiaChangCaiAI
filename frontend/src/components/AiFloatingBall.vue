<template>
  <div class="ai-entry" @click="$router.push('/ai-chat')">
    <div class="ai-pill">
      <svg class="ai-spark" viewBox="0 0 20 20" width="16" height="16">
        <path d="M10 2l.8 3.2L14 6l-3.2.8L10 10l-.8-3.2L6 6l3.2-.8z" fill="currentColor"/>
        <circle cx="5" cy="4" r="0.9" fill="currentColor" opacity="0.7"/>
        <circle cx="16" cy="3" r="0.6" fill="currentColor" opacity="0.5"/>
        <circle cx="4" cy="14" r="0.7" fill="currentColor" opacity="0.5"/>
      </svg>
      <span class="ai-label">AI</span>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.ai-entry {
  position: fixed;
  right: 12px;
  bottom: 90px;
  z-index: 900;
  cursor: pointer;
}

.ai-pill {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #fff;
  border-radius: var(--radius-full);
  box-shadow: 0 2px 16px rgba(255, 122, 51, 0.10), 0 1px 3px rgba(0,0,0,0.04);
  border: 1px solid rgba(255, 122, 51, 0.08);
  transition: all 0.3s var(--ease-smooth);
  position: relative;
}

.ai-pill::after {
  content: '';
  position: absolute;
  inset: -2px;
  border-radius: var(--radius-full);
  background: var(--gradient-primary);
  opacity: 0;
  z-index: -1;
  transition: opacity 0.3s;
}

.ai-pill:active {
  transform: scale(0.94);
  box-shadow: 0 1px 8px rgba(255, 122, 51, 0.12);
}

.ai-spark {
  color: var(--primary);
  flex-shrink: 0;
  animation: sparkPulse 2.5s ease-in-out infinite;
}

@keyframes sparkPulse {
  0%, 100% { opacity: 0.7; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.12); }
}

.ai-label {
  font-family: var(--font-heading);
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: 1px;
}
</style>
