export async function checkMember(userId) {
  const res = await fetch(`/member/check?userId=${userId}`)
  return res.json()
}

export async function addMember(userId, memberType) {
  const res = await fetch(`/member/addMember?user_id=${userId}&member_type=${memberType}`)
  return res.text()
}

export async function getExpireTime(userId) {
  const res = await fetch(`/member/viewExperTime?user_id=${userId}`)
  const text = await res.text()
  return text ? JSON.parse(text) : null
}
