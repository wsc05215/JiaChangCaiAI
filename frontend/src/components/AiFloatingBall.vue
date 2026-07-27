<template>
  <div class="floating-ball" @click="$router.push('/ai-chat')">
    <div class="ball-inner">
      <svg viewBox="0 0 28 28" width="17" height="17" class="ball-icon">
        <!-- Sparkle icon -->
        <path d="M14 3l1.2 4.8L20 9l-4.8 1.2L14 15l-1.2-4.8L8 9l4.8-1.2z" fill="#fff"/>
        <circle cx="5" cy="6" r="1.2" fill="#fff" opacity="0.8"/>
        <circle cx="23" cy="5" r="0.8" fill="#fff" opacity="0.5"/>
        <circle cx="6" cy="21" r="1" fill="#fff" opacity="0.6"/>
        <circle cx="22" cy="22" r="1.2" fill="#fff" opacity="0.4"/>
      </svg>
      <span class="ball-text">AI</span>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.floating-ball {
  position: fixed;
  right: 14px;
  bottom: 95px;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  z-index: 999;
  cursor: pointer;
  background: linear-gradient(135deg, #FF7B3D 0%, #E85D26 100%);
  box-shadow: 0 4px 20px rgba(232, 93, 38, 0.4);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  animation: float 3s ease-in-out infinite;
}

.floating-ball:active {
  transform: scale(0.88);
  box-shadow: 0 2px 10px rgba(232, 93, 38, 0.3);
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.ball-inner {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0px;
}

.ball-icon {
  flex-shrink: 0;
}

.ball-text {
  font-size: 11px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 1px;
  line-height: 1;
  margin-top: -1px;
}
</style>
