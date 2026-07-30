import request from '../utils/request'

export function getRecentProducts() {
  return request.get('/product/getRecent')
}

export function getSalesRanking() {
  return request.get('/product/getProductOfSales')
}

export function getSalesRankingByCategory(category) {
  return request.get('/product/getProductOfCategory', { params: { category } })
}
