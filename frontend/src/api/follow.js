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

export function getFollowingList(userId) {
  return request.get('/follow/getFollowing', { params: { followerId: userId } })
}

export function getFollowersList(userId) {
  return request.get('/follow/getFollowers', { params: { followeeId: userId } })
}
