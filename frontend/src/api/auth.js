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

export function sendEmailCode(email) {
  const params = new URLSearchParams()
  params.append('email', email)
  return request.post('/user/sendCode', params)
}

export function emailLogin(email, code) {
  const params = new URLSearchParams()
  params.append('email', email)
  params.append('code', code)
  return request.post('/user/emailLogin', params)
}

export function isFirstLogin(userId) {
  return request.get('/user/isFirstLogin', { params: { userId } })
}

export function findEmail(email) {
  const params = new URLSearchParams()
  params.append('email', email)
  return request.post('/user/findEmail', params)
}

export function reastPassword(email, code, newPassword) {
  const params = new URLSearchParams()
  params.append('email', email)
  params.append('code', code)
  params.append('newPassword', newPassword)
  return request.post('/user/reastPassword', params)
}

export function deleteUser(userId) {
  return request.delete('/user/deleUser', { params: { user_id: userId } })
}

export function alterUser(user) {
  const params = new URLSearchParams()
  params.append('userId', user.userId)
  params.append('username', user.username || '')
  params.append('password', user.password || '')
  params.append('nickName', user.nickName || '')
  params.append('phone', user.phone || '')
  params.append('email', user.email || '')
  return request.post('/user/alterUser', params)
}
