import request from '../utils/request'

export function getAllRecipes() {
  return request.get('/recipe/getAllRecipe')
}

export function getFollowRecipes(userId) {
  return request.get('/recipe/getAllRecipeOfFollow', { params: { id: userId } })
}

export function getOwnRecipes(userId) {
  return request.get('/recipe/ownRecipe', { params: { id: userId } })
}

export function uploadImages(files) {
  const form = new FormData()
  files.forEach(f => form.append('files', f))
  return request.post('/upload/images', form)
}

export function uploadVideo(file) {
  const form = new FormData()
  form.append('file', file)
  return request.post('/upload/video', form)
}

export function createRecipe(data) {
  return request.post('/recipe/create', data)
}

export function deleteRecipe(recipeId, userId) {
  return request.get('/recipe/delete', { params: { recipeId, userId } })
}
