import { resolveUrl } from '../utils/native'

export async function checkMember(userId) {
  const res = await fetch(resolveUrl(`/member/check?userId=${userId}`))
  return res.json()
}

export async function addMember(userId, memberType) {
  const res = await fetch(resolveUrl(`/member/addMember?user_id=${userId}&member_type=${memberType}`))
  return res.text()
}

export async function getExpireTime(userId) {
  const res = await fetch(resolveUrl(`/member/viewExperTime?user_id=${userId}`))
  const text = await res.text()
  return text ? JSON.parse(text) : null
}

export async function getMemberInfo(userId) {
  const res = await fetch(resolveUrl(`/member/info?userId=${userId}`))
  return res.json()
}
