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

export function getProductById(id) {
  return request.get('/product/getById', { params: { id } })
}

export function addToCart(product_id, user_id) {
  const params = new URLSearchParams()
  params.append('product_id', product_id)
  params.append('user_id', user_id)
  return request.post('/cart/addCart', params)
}

export function getCart(userId) {
  return request.get('/cart/getCart', { params: { user_id: userId } })
}

export function addAddress(address) {
  const params = new URLSearchParams()
  params.append('userId', address.userId)
  params.append('receiver', address.receiver)
  params.append('phone', address.phone)
  params.append('province', address.province)
  params.append('city', address.city)
  params.append('district', address.district)
  params.append('detail', address.detail)
  return request.post('/address/addAddress', params)
}

export function getAddressList(userId) {
  return request.get('/address/getAddress', { params: { userId } })
}

export function deleteAddress(addressId) {
  return request.delete('/address/deleAddress', { params: { addressId } })
}

export function addOrder(orderData) {
  const params = new URLSearchParams()
  params.append('userId', orderData.userId)
  params.append('orderId', orderData.orderId)
  params.append('productId', orderData.productId)
  params.append('productName', orderData.productName)
  params.append('productImage', orderData.productImage)
  params.append('price', orderData.price)
  params.append('quantity', orderData.quantity)
  params.append('totalPrice', orderData.totalPrice)
  return request.post('/order-item/addOrder', params)
}

export function getOrderItems(userId) {
  return request.get('/order-item/getOrderitem', { params: { user_id: userId } })
}

export function confirmReceive(itemId) {
  const params = new URLSearchParams()
  params.append('itemId', itemId)
  return request.post('/order-item/confirmReceive', params)
}

export function requestReturn(itemId, reason) {
  const params = new URLSearchParams()
  params.append('itemId', itemId)
  params.append('reason', reason)
  return request.post('/order-item/requestReturn', params)
}

export function cancelReturn(itemId) {
  const params = new URLSearchParams()
  params.append('itemId', itemId)
  return request.post('/order-item/cancelReturn', params)
}

export function getReturnOrders(userId) {
  return request.get('/order-item/getReturnOrders', { params: { user_id: userId } })
}
