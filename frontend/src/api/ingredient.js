import request from '../utils/request'

export function addIngredient(userId, name, category, createTime) {
  const params = new URLSearchParams()
  params.append('userId', userId)
  params.append('name', name)
  params.append('category', category)
  params.append('createTime', createTime)
  return request.post('/ingredient/addIngredient', params)
}

export function listIngredients(userId) {
  return request.get('/ingredient/list', { params: { userId } })
}

export function deleteIngredient(ingredientId) {
  return request.delete(`/ingredient/${ingredientId}`)
}

// 拍照识别食材（小票或实物照均可，返回识别项【不】入库，待前端确认后调 saveRecognizedItems），模型推理较慢，放宽超时
export function recognizeIngredient(userId, file) {
  const form = new FormData()
  form.append('userId', userId)
  form.append('file', file)
  return request.post('/ingredient/recognize', form, { timeout: 60000 })
}

// 用户确认/编辑后的识别结果批量入库
export function saveRecognizedItems(userId, items) {
  return request.post('/ingredient/saveBatch', { userId, items })
}

export function getIngredientStats(userId) {
  return request.get('/ingredient/stats', { params: { userId } })
}
