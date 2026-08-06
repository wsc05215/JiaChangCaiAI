import request from '../utils/request'

export function listCustomRecords(userId, type) {
  const params = { userId }
  if (type) params.type = type
  return request.get('/custom/list', { params })
}

export function getCustomRecordDetail(id) {
  return request.get(`/custom/detail/${id}`)
}

export function deleteCustomRecord(id) {
  return request.delete(`/custom/${id}`)
}
