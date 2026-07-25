import request from '../utils/request'

export function login(username, password) {
  const params = new URLSearchParams()
  params.append('username', username)
  params.append('password', password)
  return request.post('/user/login', params)
}

export function getUserDetail(userId) {
  return request.get('/user/mydetail', { params: { id: userId } })
}
