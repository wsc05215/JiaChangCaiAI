import request from '../utils/request'

export function follow(followerId, followeeId) {
  return request.post('/follow/add', null, { params: { followerId, followeeId } })
}

export function unfollow(followerId, followeeId) {
  return request.delete('/follow/remove', { params: { followerId, followeeId } })
}

export function checkFollowing(followerId, followeeId) {
  return request.get('/follow/check', { params: { followerId, followeeId } })
}
