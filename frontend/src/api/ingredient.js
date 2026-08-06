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

export function getIngredientStats(userId) {
  return request.get('/ingredient/stats', { params: { userId } })
}
