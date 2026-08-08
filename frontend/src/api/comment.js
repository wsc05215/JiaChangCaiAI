import request from '../utils/request'

export function getComments(recipe_id) {
  return request.get('/comment/getComment', { params: { recipe_id } })
}

export function addComment(recipeId, userId, content, parentId) {
  const params = new URLSearchParams()
  params.append('recipeId', recipeId)
  params.append('userId', userId)
  params.append('content', content)
  if (parentId) params.append('parentId', parentId)
  return request.post('/comment/addComment', params)
}

export function toggleCommentLike(commentId, userId) {
  const params = new URLSearchParams()
  params.append('commentId', commentId)
  params.append('userId', userId)
  return request.post('/comment-like/toggle', params)
}

export function checkCommentLiked(commentId, userId) {
  return request.get('/comment-like/hasLiked', { params: { commentId, userId } })
}

export function getNotifications(userId) {
  return request.get('/notification/list', { params: { userId } })
}

export function getUnreadCount(userId) {
  return request.get('/notification/unreadCount', { params: { userId } })
}

export function markNotificationRead(notificationId) {
  const params = new URLSearchParams()
  params.append('notificationId', notificationId)
  return request.post('/notification/markRead', params)
}

export function markAllNotificationsRead(userId) {
  const params = new URLSearchParams()
  params.append('userId', userId)
  return request.post('/notification/markAllRead', params)
}
