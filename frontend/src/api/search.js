import request from '../utils/request'

export function searchRecipes(keyword) {
  return request.get('/search', { params: { keyword } })
}

export function searchProducts(keyword) {
  return request.get('/search/searchProduct', { params: { keyword } })
}
