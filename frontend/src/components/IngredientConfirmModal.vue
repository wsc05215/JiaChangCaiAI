<template>
  <div class="confirm-overlay" @click="onClose">
    <div class="confirm-card" @click.stop>
      <div class="confirm-header">
        <span class="confirm-title">确认食材信息</span>
        <span class="confirm-close" @click="onClose">&times;</span>
      </div>

      <div class="confirm-body">
        <div class="confirm-tip">识别出的食材已列出，可修改名称、分类、数量、单位、保质期或删除，确认后统一入库</div>

        <div v-if="draft.length === 0" class="empty-hint">没有可确认的食材，请重新识别</div>
        <div v-for="(item, i) in draft" :key="i" class="confirm-row">
          <div class="row-main">
            <input v-model="item.name" class="field-input name-input" placeholder="食材名称" />
            <select v-model="item.category" class="field-input category-select">
              <option v-for="c in categories" :key="c" :value="c">{{ c }}</option>
            </select>
          </div>
          <div class="row-sub">
            <input v-model.number="item.quantity" type="number" min="1" class="field-input qty-input" placeholder="数量" />
            <input v-model="item.unit" class="field-input unit-input" placeholder="单位" />
            <input v-model="item.purchaseDate" type="date" class="field-input date-input" :max="today" />
          </div>
          <div class="row-sub">
            <input v-model.number="item.expireDays" type="number" min="1" max="365" class="field-input expire-input" placeholder="保质天数" />
            <span class="expire-unit">天</span>
            <div class="row-grow"></div>
            <button class="row-del" @click="removeRow(i)">
              <svg viewBox="0 0 20 20" width="16" height="16">
                <path d="M6 6l8 8M14 6l-8 8" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>

      <div class="confirm-footer">
        <button class="cancel-btn" @click="onClose" :disabled="saving">取消</button>
        <button class="save-btn" @click="onConfirm" :disabled="saving || !canSave">
          {{ saving ? '保存中...' : `确认入库（${draft.length}件）` }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  items: { type: Array, default: () => [] },
  saving: { type: Boolean, default: false },
})
const emit = defineEmits(['confirm', 'close'])

const categories = ['蔬菜', '生禽', '蛋类', '水产', '豆制品', '其他']
const today = new Date().toISOString().slice(0, 10)
// 保质期兜底值（与后端规则表"冷藏"默认一致），AI 没给时用
const CATEGORY_DEFAULT_DAYS = { '蔬菜': 7, '生禽': 3, '蛋类': 30, '水产': 2, '豆制品': 5, '其他': 7 }
const draft = ref([])

watch(
  () => props.items,
  (list) => {
    draft.value = (list || []).map((it) => ({
      name: it.name || '',
      category: it.category || '其他',
      quantity: it.quantity && it.quantity > 0 ? it.quantity : 1,
      unit: it.unit || '',
      purchaseDate: it.purchaseDate || today,
      expireDays: it.expireDays && it.expireDays > 0 ? it.expireDays : (CATEGORY_DEFAULT_DAYS[it.category || '其他'] || 7),
    }))
  },
  { immediate: true }
)

const canSave = computed(() => draft.value.some((i) => i.name && i.name.trim()))

function removeRow(i) {
  draft.value.splice(i, 1)
}

function onConfirm() {
  const valid = draft.value
    .filter((i) => i.name && i.name.trim())
    .map((i) => ({
      name: i.name.trim(),
      category: i.category,
      quantity: i.quantity > 0 ? i.quantity : 1,
      unit: i.unit || '',
      purchaseDate: i.purchaseDate,
      expireDays: i.expireDays > 0 ? i.expireDays : 7,
    }))
  if (!valid.length) return
  emit('confirm', valid)
}

function onClose() {
  if (props.saving) return
  emit('close')
}
</script>

<style scoped>
.confirm-overlay {
  position: fixed;
  inset: 0;
  background: rgba(20, 14, 8, 0.55);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.confirm-card {
  background: #fff;
  border-radius: 20px;
  width: 340px;
  max-width: 92vw;
  max-height: 84vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.18);
}

.confirm-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px 0;
  flex-shrink: 0;
}

.confirm-title {
  font-size: 17px;
  font-weight: 800;
  color: var(--text-primary);
}

.confirm-close {
  font-size: 26px;
  color: var(--text-muted);
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.confirm-close:active { background: #F5F0E8; }

.confirm-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 18px;
}

.confirm-tip {
  font-size: 12px;
  color: var(--text-muted);
  line-height: 1.5;
  margin-bottom: 12px;
}

.empty-hint {
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
  padding: 24px 0;
}

.confirm-row {
  background: #FAF7F2;
  border-radius: 12px;
  padding: 10px 12px;
  margin-bottom: 10px;
  border: 1.5px solid rgba(0, 0, 0, 0.04);
}

.row-main {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.row-sub {
  display: flex;
  gap: 8px;
  align-items: center;
}

.field-input {
  height: 38px;
  border: 1.5px solid var(--border);
  border-radius: 10px;
  padding: 0 10px;
  font-size: 13px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.field-input:focus {
  border-color: var(--primary);
}

.name-input { flex: 1.4; }
.category-select { flex: 1; cursor: pointer; color: var(--text-primary); }
.qty-input { width: 58px; }
.unit-input { width: 58px; }
.date-input { flex: 1; }
.expire-input { width: 80px; }
.expire-unit { font-size: 12px; color: var(--text-muted); margin-left: 2px; }
.row-grow { flex: 1; }

.row-del {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  flex-shrink: 0;
  background: transparent;
  border: none;
}

.row-del:active { background: #F5F0E8; }

.confirm-footer {
  display: flex;
  gap: 10px;
  padding: 14px 18px 18px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.cancel-btn,
.save-btn {
  height: 44px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  flex: 1;
  background: #F5F0E8;
  color: var(--text-secondary);
  border: none;
}

.save-btn {
  flex: 1.6;
  background: var(--gradient-primary);
  color: #fff;
  border: none;
  box-shadow: 0 4px 16px rgba(255, 122, 51, 0.25);
}

.save-btn:disabled,
.cancel-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.save-btn:not(:disabled):active,
.cancel-btn:not(:disabled):active {
  transform: scale(0.96);
}
</style>
