<template>
  <div class="upload-page">
    <div class="nav-bar">
      <div class="back-btn" @click="$router.back()">
        <svg viewBox="0 0 24 24" width="24" height="24">
          <path d="M15 18l-6-6 6-6" stroke="#6B5E52" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      <span class="nav-title">发布菜谱</span>
      <button class="submit-btn" @click="handleSubmit" :disabled="submitting || !canSubmit">
        {{ submitting ? '发布中...' : '发布' }}
      </button>
    </div>

    <div class="form-body">

      <!-- 图片 -->
      <div class="form-section">
        <div class="section-label">封面图片 <span class="hint">(可多选，第一张为主图)</span></div>
        <div class="image-area">
          <div v-for="(url, i) in images" :key="i" class="image-thumb" @click="removeImage(i)">
            <img :src="url" />
            <div class="image-remove">×</div>
            <div v-if="i === 0" class="image-cover-tag">封面</div>
          </div>
          <label class="image-add" v-if="images.length < 9">
            <input type="file" accept="image/*" multiple hidden @change="onFilesChange" />
            <svg viewBox="0 0 24 24" width="28" height="28"><path d="M12 5v14M5 12h14" stroke="#C4B5AA" stroke-width="2" stroke-linecap="round"/></svg>
            <span v-if="uploading" class="image-uploading">上传中</span>
          </label>
        </div>
      </div>

      <!-- 标题 -->
      <div class="form-section">
        <div class="section-label">菜谱标题</div>
        <input v-model="form.title" class="input" placeholder="给你的菜谱起个名字..." maxlength="60" />
      </div>

      <!-- 简介 -->
      <div class="form-section">
        <div class="section-label">简介</div>
        <textarea v-model="form.description" class="textarea" placeholder="简单描述一下这道菜..." maxlength="200" rows="3"></textarea>
      </div>

      <!-- 烹饪信息 -->
      <div class="form-section">
        <div class="section-label">烹饪信息</div>
        <div class="info-row">
          <div class="info-item">
            <label class="info-label">时长</label>
            <input v-model="form.cookTime" class="input small" placeholder="如：30分钟" />
          </div>
          <div class="info-item">
            <label class="info-label">难度</label>
            <div class="select-wrap">
              <select v-model="form.difficulty" class="select">
                <option value="">选择</option>
                <option value="简单">简单</option>
                <option value="中等">中等</option>
                <option value="困难">困难</option>
              </select>
            </div>
          </div>
          <div class="info-item">
            <label class="info-label">卡路里</label>
            <input v-model="form.calories" class="input small" placeholder="如：350千卡" />
          </div>
        </div>
      </div>

      <!-- 食材 -->
      <div class="form-section">
        <div class="section-label">食材清单</div>
        <div class="ingredient-list">
          <div v-for="(ing, i) in form.ingredients" :key="i" class="ingredient-row">
            <input v-model="ing.name" class="input ing-name" placeholder="食材名称" />
            <input v-model="ing.amount" class="input ing-amount" placeholder="用量（如500g）" />
            <span class="ing-remove" @click="form.ingredients.splice(i, 1)">
              <svg viewBox="0 0 16 16" width="14" height="14"><path d="M4 4l8 8M12 4l-8 8" stroke="#C4B5AA" stroke-width="1.6" stroke-linecap="round"/></svg>
            </span>
          </div>
          <button class="add-row-btn" @click="form.ingredients.push({ name: '', amount: '' })">
            + 添加食材
          </button>
        </div>
      </div>

      <!-- 步骤 -->
      <div class="form-section">
        <div class="section-label">烹饪步骤</div>
        <div class="step-list">
          <div v-for="(step, i) in form.steps" :key="i" class="step-row">
            <div class="step-num">{{ i + 1 }}</div>
            <textarea v-model="step.desc" class="textarea step-desc" :placeholder="'第' + (i + 1) + '步...'" rows="2"></textarea>
            <span class="ing-remove" @click="form.steps.splice(i, 1)">
              <svg viewBox="0 0 16 16" width="14" height="14"><path d="M4 4l8 8M12 4l-8 8" stroke="#C4B5AA" stroke-width="1.6" stroke-linecap="round"/></svg>
            </span>
          </div>
          <button class="add-row-btn" @click="form.steps.push({ desc: '' })">
            + 添加步骤
          </button>
        </div>
      </div>

      <div style="height: 40px;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { uploadImages, createRecipe } from '../api/recipe'
import { userStore } from '../store/user'

const router = useRouter()
const images = ref([])
const uploading = ref(false)
const submitting = ref(false)

const form = reactive({
  title: '',
  description: '',
  cookTime: '',
  difficulty: '',
  calories: '',
  ingredients: [{ name: '', amount: '' }],
  steps: [{ desc: '' }],
})

const canSubmit = computed(() => {
  return form.title.trim() && images.value.length > 0
})

async function onFilesChange(e) {
  const files = Array.from(e.target.files)
  if (files.length === 0) return
  uploading.value = true
  try {
    const res = await uploadImages(files)
    images.value.push(...res.data)
  } catch (e) {
    console.error('上传失败:', e)
    const msg = e.response?.data?.message || e.response?.statusText || e.message || '未知错误'
    alert('图片上传失败：' + msg)
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

function removeImage(i) {
  images.value.splice(i, 1)
}

async function handleSubmit() {
  if (!canSubmit.value || submitting.value) return

  const uid = userStore.user?.userId
  if (!uid) {
    alert('请先登录')
    return
  }

  submitting.value = true
  try {
    const data = {
      title: form.title.trim(),
      description: form.description.trim(),
      cookTime: form.cookTime.trim(),
      difficulty: form.difficulty,
      calories: form.calories.trim(),
      coverImages: JSON.stringify(images.value),
      ingredients: JSON.stringify(
        form.ingredients.filter(i => i.name.trim()).map(i => ({ name: i.name.trim(), amount: i.amount.trim() }))
      ),
      steps: JSON.stringify(
        form.steps.filter(s => s.desc.trim()).map((s, idx) => ({ step: idx + 1, desc: s.desc.trim() }))
      ),
      authorId: uid,
    }
    await createRecipe(data)
    alert('发布成功！')
    router.back()
  } catch {
    alert('发布失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  min-height: 100dvh;
  background: var(--gradient-page);
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px;
  background: rgba(249,247,242,0.88);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0,0,0,0.04);
  position: sticky; top: 0; z-index: 10;
}

.back-btn {
  width: 36px; height: 36px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%;
}

.nav-title { font-size: 17px; font-weight: 800; color: var(--text-primary); }

.submit-btn {
  padding: 7px 18px;
  border-radius: 20px;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 13px; font-weight: 800;
  box-shadow: 0 3px 12px rgba(255,122,51,0.25);
  transition: all 0.2s;
}

.submit-btn:disabled {
  opacity: 0.4;
  box-shadow: none;
}

.submit-btn:active:not(:disabled) { transform: scale(0.94); }

.form-body { padding: 20px 16px; }

.form-section { margin-bottom: 22px; }

.section-label {
  font-size: 14px; font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 10px;
}

.hint { font-weight: 400; font-size: 12px; color: var(--text-muted); }

/* images */
.image-area { display: flex; gap: 10px; flex-wrap: wrap; }

.image-thumb {
  width: 90px; height: 90px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  box-shadow: var(--shadow-xs);
}

.image-thumb img {
  width: 100%; height: 100%; object-fit: cover;
}

.image-remove {
  position: absolute; top: 4px; right: 4px;
  width: 22px; height: 22px;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  color: #fff; font-size: 14px;
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.2s;
}

.image-thumb:active .image-remove { opacity: 1; }

.image-cover-tag {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(0,0,0,0.5);
  color: #fff; font-size: 10px; font-weight: 700;
  text-align: center; padding: 3px 0;
}

.image-add {
  width: 90px; height: 90px;
  border-radius: 12px;
  border: 2px dashed var(--border);
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
  gap: 4px; cursor: pointer;
  background: #fff;
  transition: all 0.2s;
}

.image-add:active { border-color: var(--primary); background: var(--primary-bg); }

.image-uploading { font-size: 10px; color: var(--primary); font-weight: 600; }

/* inputs */
.input {
  width: 100%;
  height: 46px;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 0 14px;
  font-size: 14px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.input:focus { border-color: var(--primary); }

.input.small { width: 100%; }

.textarea {
  width: 100%;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 14px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.textarea:focus { border-color: var(--primary); }

/* info row */
.info-row { display: flex; gap: 10px; }

.info-item { flex: 1; min-width: 0; }

.info-label { display: block; font-size: 12px; color: var(--text-muted); margin-bottom: 6px; }

.select-wrap { position: relative; }

.select {
  width: 100%;
  height: 46px;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  padding: 0 14px;
  font-size: 14px;
  color: var(--text-primary);
  background: #fff;
  outline: none;
  appearance: none;
  -webkit-appearance: none;
}

.select:focus { border-color: var(--primary); }

/* ingredients */
.ingredient-list, .step-list { display: flex; flex-direction: column; gap: 8px; }

.ingredient-row {
  display: flex; gap: 8px; align-items: center;
}

.ing-name { flex: 3; }

.ing-amount { flex: 2; }

.ing-remove {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; border-radius: 50%; flex-shrink: 0;
}

.ing-remove:active { background: #f5f0e8; }

/* steps */
.step-row {
  display: flex; gap: 10px; align-items: flex-start;
}

.step-num {
  width: 28px; height: 28px;
  border-radius: 50%;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 13px; font-weight: 800;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
  margin-top: 12px;
}

.step-desc { flex: 1; }

.add-row-btn {
  display: block;
  width: 100%;
  padding: 10px;
  border-radius: 12px;
  border: 1.5px dashed var(--border);
  background: #fff;
  color: var(--text-muted);
  font-size: 13px; font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.add-row-btn:active {
  border-color: var(--primary);
  color: var(--primary);
  background: var(--primary-bg);
}

.input::placeholder, .textarea::placeholder { color: var(--text-placeholder); }
</style>
