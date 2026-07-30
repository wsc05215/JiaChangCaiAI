import request from '../utils/request'

export function searchRecipes(keyword) {
  return request.get('/search', { params: { keyword } })
}
