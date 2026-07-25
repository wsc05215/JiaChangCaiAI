import request from '../utils/request'

export function getUserDetail(userId) {
  return request.get('/user/mydetail', { params: { id: userId } })
}

export function getWorkCount(userId) {
  return request.get('/user/works', { params: { id: userId } })
}

export function getLikeCount(userId) {
  return request.get('/user/likeCount', { params: { id: userId } })
}
