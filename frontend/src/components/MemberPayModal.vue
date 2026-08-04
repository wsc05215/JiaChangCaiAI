<template>
  <teleport to="body">
    <div class="modal-overlay" @click.self="$emit('close')">
      <div class="modal-card">

        <!-- 步骤1：选择套餐 -->
        <template v-if="step === 1">
          <div class="step-header">
            <div class="step-icon">&#x1F451;</div>
            <div class="step-title">开通会员，解锁全部功能</div>
            <div class="step-sub">选择适合你的会员套餐</div>
          </div>

          <div class="plans">
            <div
              v-for="plan in plans"
              :key="plan.type"
              class="plan-card"
              :class="{ 'plan-gold': plan.type === 3 }"
              @click="selectPlan(plan)"
            >
              <div class="plan-badge" v-if="plan.type === 3">推荐</div>
              <div class="plan-left">
                <div class="plan-name">{{ plan.name }}</div>
                <div class="plan-desc">{{ plan.desc }}</div>
                <div class="plan-duration">{{ plan.duration }}</div>
              </div>
              <div class="plan-right">
                <div class="plan-price">
                  <span class="price-num">¥{{ plan.price }}</span>
                  <span class="price-unit">/{{ plan.unit }}</span>
                </div>
                <div class="plan-select-btn">选择</div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button class="cancel-btn" @click="$emit('close')">暂不开通</button>
          </div>
        </template>

        <!-- 步骤2：选择支付方式 -->
        <template v-if="step === 2">
          <div class="step-header">
            <div class="step-icon">&#x1F4B3;</div>
            <div class="step-title">确认支付</div>
            <div class="step-sub">{{ selectedPlan.name }} · ¥{{ selectedPlan.price }}</div>
          </div>

          <div class="pay-methods">
            <div
              class="pay-card"
              :class="{ active: payMethod === 'wxpay' }"
              @click="payMethod = 'wxpay'"
            >
              <div class="pay-icon pay-icon-green">&#x1F4F1;</div>
              <div class="pay-name">微信支付</div>
              <div class="pay-check" v-if="payMethod === 'wxpay'">
                <svg viewBox="0 0 20 20" width="16" height="16">
                  <circle cx="10" cy="10" r="10" fill="#07C160"/>
                  <path d="M6 10l2.5 2.5L14 7.5" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
            </div>
            <div
              class="pay-card"
              :class="{ active: payMethod === 'alipay' }"
              @click="payMethod = 'alipay'"
            >
              <div class="pay-icon pay-icon-blue">&#x1F4B0;</div>
              <div class="pay-name">支付宝</div>
              <div class="pay-check" v-if="payMethod === 'alipay'">
                <svg viewBox="0 0 20 20" width="16" height="16">
                  <circle cx="10" cy="10" r="10" fill="#1677FF"/>
                  <path d="M6 10l2.5 2.5L14 7.5" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
            </div>
          </div>

          <div class="pay-summary">
            <div class="summary-row">
              <span>套餐</span>
              <span>{{ selectedPlan.name }}</span>
            </div>
            <div class="summary-row">
              <span>金额</span>
              <span class="summary-price">¥{{ selectedPlan.price }}</span>
            </div>
          </div>

          <div class="modal-footer">
            <button class="cancel-btn" @click="step = 1">上一步</button>
            <button class="pay-btn" @click="doPay">确认支付 ¥{{ selectedPlan.price }}</button>
          </div>
        </template>

        <!-- 步骤3：支付处理中 / 失败 -->
        <template v-if="step === 3">
          <div class="step-header">
            <template v-if="!payError">
              <div class="spinner"></div>
              <div class="step-title">支付处理中...</div>
              <div class="step-sub">正在调用{{ payMethod === 'wxpay' ? '微信' : '支付宝' }}支付</div>
            </template>
            <template v-else>
              <div class="error-circle">&#x2716;</div>
              <div class="step-title">支付失败</div>
              <div class="step-sub">请检查网络后重试</div>
            </template>
          </div>
          <div class="modal-footer" v-if="payError">
            <button class="cancel-btn" @click="step = 1">返回重选</button>
            <button class="pay-btn" @click="doPay">重新支付</button>
          </div>
        </template>

        <!-- 步骤4：支付成功 -->
        <template v-if="step === 4">
          <div class="step-header">
            <div class="success-circle">
              <svg viewBox="0 0 32 32" width="32" height="32">
                <circle cx="16" cy="16" r="16" fill="#07C160"/>
                <path d="M9 16l4.5 4.5L23 11.5" stroke="#fff" stroke-width="3" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="step-title">开通成功！</div>
            <div class="step-sub">你已是{{ selectedPlan.name }}，尽情使用吧</div>
          </div>
          <div class="modal-footer">
            <button class="done-btn" @click="$emit('success', selectedPlan.type)">开始使用</button>
          </div>
        </template>

      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref } from 'vue'
import { addMember } from '../api/member'
import { userStore } from '../store/user'

const emit = defineEmits(['close', 'success'])

const step = ref(1)
const payMethod = ref('wxpay')
const selectedPlan = ref(null)
const payError = ref(false)

const plans = [
  { type: 1, name: '食谱定制会员', desc: '根据需求和口味偏好定制专属食谱', duration: '有效时长 2小时', price: '5.9', unit: '次' },
  { type: 2, name: '食材管理会员', desc: '管理家中食材，根据食材推荐食谱', duration: '有效时长 1个月', price: '18', unit: '月' },
  { type: 3, name: '尊享会员', desc: '享以上两种会员全部权益，性价比之选', duration: '有效时长 1年', price: '128', unit: '年' },
]

function selectPlan(plan) {
  selectedPlan.value = plan
  step.value = 2
}

async function doPay() {
  step.value = 3
  payError.value = false

  // 模拟支付延迟
  await new Promise(r => setTimeout(r, 1500))

  const uid = userStore.user?.userId
  if (!uid) {
    payError.value = true
    return
  }

  try {
    const result = await addMember(uid, selectedPlan.value.type)
    if (result === 'ok') {
      step.value = 4
    } else {
      payError.value = true
    }
  } catch (e) {
    payError.value = true
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.25s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 20px 20px 0 0;
  padding: 28px 20px 20px;
  animation: slideUp 0.3s var(--ease-smooth);
  max-height: 85vh;
  overflow-y: auto;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.step-header {
  text-align: center;
  margin-bottom: 24px;
}

.step-icon { font-size: 40px; margin-bottom: 12px; }

.step-title {
  font-size: 20px; font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.step-sub {
  font-size: 13px;
  color: var(--text-muted);
}

/* plans */
.plans {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.plan-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: 16px;
  border: 2px solid var(--border);
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  background: #fff;
}

.plan-card:active { transform: scale(0.98); }

.plan-gold {
  border-color: #F5C34B;
  background: #FFFDF6;
  box-shadow: 0 2px 16px rgba(240,165,0,0.08);
}

.plan-badge {
  position: absolute;
  top: -10px; right: 16px;
  background: var(--gradient-gold);
  color: #fff;
  font-size: 11px; font-weight: 800;
  padding: 3px 12px;
  border-radius: 10px;
  letter-spacing: 1px;
}

.plan-left { flex: 1; min-width: 0; }

.plan-name {
  font-size: 16px; font-weight: 800;
  color: var(--text-primary);
}

.plan-desc {
  font-size: 12px; color: var(--text-muted);
  margin-top: 4px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.plan-duration {
  font-size: 11px; color: var(--primary);
  margin-top: 4px; font-weight: 600;
}

.plan-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  flex-shrink: 0;
}

.price-num {
  font-size: 24px; font-weight: 900;
  color: var(--text-primary);
}

.price-unit {
  font-size: 12px; color: var(--text-muted);
}

.plan-select-btn {
  padding: 6px 20px;
  border-radius: 20px;
  font-size: 13px; font-weight: 700;
  background: var(--primary-bg);
  color: var(--primary);
}

.plan-gold .plan-select-btn {
  background: var(--gradient-gold);
  color: #fff;
}

/* pay methods */
.pay-methods {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.pay-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  border-radius: 14px;
  border: 2px solid var(--border);
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.pay-card.active {
  border-color: var(--primary);
  background: var(--primary-bg);
}

.pay-icon { font-size: 24px; }

.pay-name {
  font-size: 14px; font-weight: 700;
  color: var(--text-primary);
}

.pay-check {
  position: absolute;
  top: 8px; right: 8px;
}

.pay-summary {
  background: #F9F7F2;
  border-radius: 14px;
  padding: 14px 16px;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-secondary);
  padding: 4px 0;
}

.summary-price {
  font-weight: 800; color: var(--primary);
  font-size: 16px;
}

/* footer */
.modal-footer {
  display: flex;
  gap: 10px;
}

.cancel-btn {
  flex: 1;
  padding: 14px;
  border-radius: 28px;
  background: #F5F3EF;
  color: var(--text-muted);
  font-size: 15px; font-weight: 700;
  transition: all 0.2s;
}

.cancel-btn:active { opacity: 0.7; }

.pay-btn {
  flex: 2;
  padding: 14px;
  border-radius: 28px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 15px; font-weight: 800;
  box-shadow: 0 4px 16px rgba(255,122,51,0.3);
  transition: all 0.2s;
}

.pay-btn:active { opacity: 0.8; transform: scale(0.97); }

.done-btn {
  flex: 1;
  padding: 14px;
  border-radius: 28px;
  background: var(--gradient-gold);
  color: #fff;
  font-size: 16px; font-weight: 800;
  box-shadow: var(--shadow-gold);
  transition: all 0.2s;
}

.done-btn:active { opacity: 0.8; transform: scale(0.97); }

/* spinner & error */
.spinner {
  width: 44px; height: 44px;
  border: 4px solid #eee;
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-circle {
  width: 44px; height: 44px;
  border-radius: 50%;
  background: #FF4D4F;
  color: #fff;
  font-size: 20px;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 16px;
}

/* success */
.success-circle {
  margin: 0 auto 16px;
  animation: popIn 0.4s var(--ease-bounce);
}

@keyframes popIn {
  0% { transform: scale(0); }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); }
}
</style>
