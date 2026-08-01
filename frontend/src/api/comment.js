import request from '../utils/request'

export function getComments(recipe_id) {
  return request.get('/comment/getComment', { params: { recipe_id } })
}

export function addComment(recipeId, userId, content, parentId) {
  const params = new URLSearchParams()
  params.append('recipeId', recipeId)
  params.append('userId', userId)
  params.append('content', content)
  if (parentId) params.append('parentId', parentId)
  return request.post('/comment/addComment', params)
}
