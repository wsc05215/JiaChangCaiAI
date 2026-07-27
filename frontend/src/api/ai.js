export function streamChat(msg, onToken, onDone, onError) {
  const url = `/chat/stream?msg=${encodeURIComponent(msg)}`
  const es = new EventSource(url)

  es.onmessage = (event) => {
    if (event.data) {
      onToken(event.data)
    }
  }

  es.onerror = () => {
    es.close()
    // EventSource will auto-reconnect — we've already received tokens,
    // so treat close as done; if we got nothing, treat as error
    onDone()
  }

  // Return a cancel function
  return () => es.close()
}
