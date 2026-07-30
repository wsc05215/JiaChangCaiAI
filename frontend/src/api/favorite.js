import request from '../utils/request'

export function addFavorite(userId, recipeId) {
  return request.post('/favorite/add', null, { params: { userId, recipeId } })
}

export function removeFavorite(userId, recipeId) {
  return request.delete('/favorite/remove', { params: { userId, recipeId } })
}

export function checkFavorited(userId, recipeId) {
  return request.get('/favorite/check', { params: { userId, recipeId } })
}

export function getFavoriteRecipeIds(userId) {
  return request.get('/favorite/list', { params: { userId } })
}
