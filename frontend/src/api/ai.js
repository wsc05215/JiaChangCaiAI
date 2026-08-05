const ENDPOINTS = {
  chat: '/chat/stream',
  recipe: '/RecipechatAI/stream',
  menu: '/MyRecipeChatOfAI/stream',
  customer_service: '/chat/stream'
}

export function streamChat(msg, mode, userId, onToken, onDone, onError) {
  const base = ENDPOINTS[mode] || ENDPOINTS.chat
  const typeParam = mode === 'customer_service' ? '&type=CUSTOMER_SERVICE' : ''
  const url = `${base}?msg=${encodeURIComponent(msg)}&userId=${userId}${typeParam}`
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
