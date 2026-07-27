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

export function getFollowingCount(userId) {
  return request.get('/follow/followingCount', { params: { id: userId } })
}

export function getFollowerCount(userId) {
  return request.get('/follow/followeeCount', { params: { id: userId } })
}
