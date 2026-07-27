import request from '../utils/request'

export function getAllRecipes() {
  return request.get('/recipe/getAllRecipe')
}

export function getFollowRecipes(userId) {
  return request.get('/recipe/getAllRecipeOfFollow', { params: { id: userId } })
}
