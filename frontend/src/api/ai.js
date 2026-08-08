import { resolveUrl } from '../utils/native'

function getTypeParam(mode) {
  if (mode === 'customer_service') return 'CUSTOMER_SERVICE'
  if (mode === 'fridge') return 'AiFridgeFoodService'
  if (mode === 'customized_recipe') return 'CustomizedRecipe'
  if (mode === 'oneclick_menu') return 'Oneclickmenu'
  return 'CHEF'
}

export function streamChat(msg, mode, userId, onToken, onDone, onError) {
  // EventSource 不走 axios,原生 App 环境必须手动拼上后端地址
  const url = resolveUrl(`/chat/stream?msg=${encodeURIComponent(msg)}&userId=${userId}&type=${getTypeParam(mode)}`)
  const es = new EventSource(url)

  let received = false

  es.onmessage = (event) => {
    received = true
    if (event.data) {
      onToken(event.data)
    }
  }

  es.onerror = () => {
    es.close()
    if (received) {
      onDone()
    } else {
      onError()
    }
  }

  return () => es.close()
}
