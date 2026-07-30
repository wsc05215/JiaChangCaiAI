export async function checkMember(userId) {
  const res = await fetch(`/member/check?userId=${userId}`)
  return res.json()
}
