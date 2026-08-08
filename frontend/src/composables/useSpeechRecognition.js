import { ref, onUnmounted } from 'vue'
import { isNative, BASE } from '../utils/native'

// 原生 App 环境必须用绝对地址,否则请求发到 capacitor://localhost 会失败
const API = (isNative ? BASE : '') + '/api/speech/recognize'

export function useSpeechRecognition() {
  const isListening = ref(false)
  const isRecognizing = ref(false)
  const error = ref(null)
  const supported = ref(false)

  let mediaRecorder = null
  let stream = null
  let chunks = []
  let resolvePromise = null

  // MediaRecorder is supported in all modern browsers
  if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia && window.MediaRecorder) {
    supported.value = true
  }

  async function start() {
    error.value = null

    if (!supported.value) {
      error.value = '当前浏览器不支持录音，请使用 Chrome、Edge 或 Safari 浏览器'
      return
    }

    if (mediaRecorder && mediaRecorder.state === 'recording') return

    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    } catch (e) {
      if (e.name === 'NotAllowedError') {
        error.value = '麦克风权限被拒绝，请在浏览器设置中允许麦克风访问'
      } else if (e.name === 'NotFoundError') {
        error.value = '未检测到麦克风设备'
      } else {
        error.value = '无法访问麦克风: ' + (e.message || e.name)
      }
      return
    }

    chunks = []

    const mimeType = [
      'audio/webm;codecs=opus',
      'audio/webm',
      'audio/mp4',
      'audio/ogg;codecs=opus'
    ].find(m => MediaRecorder.isTypeSupported(m)) || 'audio/webm'

    try {
      mediaRecorder = new MediaRecorder(stream, { mimeType })
    } catch (_) {
      mediaRecorder = new MediaRecorder(stream)
    }

    mediaRecorder.ondataavailable = (e) => {
      if (e.data && e.data.size > 0) {
        chunks.push(e.data)
      }
    }

    mediaRecorder.onstart = () => {
      isListening.value = true
      error.value = null
    }

    mediaRecorder.onstop = async () => {
      isListening.value = false

      if (stream) {
        stream.getTracks().forEach(t => t.stop())
        stream = null
      }

      if (chunks.length > 0) {
        isRecognizing.value = true
        const blob = new Blob(chunks, { type: mediaRecorder.mimeType || 'audio/webm' })
        const text = await uploadAndRecognize(blob)
        isRecognizing.value = false
        if (resolvePromise) {
          resolvePromise(text)
          resolvePromise = null
        }
      } else {
        if (resolvePromise) {
          resolvePromise('')
          resolvePromise = null
        }
      }
    }

    mediaRecorder.onerror = () => {
      error.value = '录音失败，请重试'
      isListening.value = false
      if (stream) {
        stream.getTracks().forEach(t => t.stop())
        stream = null
      }
    }

    mediaRecorder.start(250)
  }

  async function uploadAndRecognize(blob) {
    const formData = new FormData()
    formData.append('file', blob, 'recording.webm')

    try {
      const resp = await fetch(API, {
        method: 'POST',
        body: formData
      })
      if (!resp.ok) {
        const errText = await resp.text()
        error.value = '语音识别失败: ' + (errText || resp.statusText)
        return ''
      }
      const data = await resp.json()
      return data.text || ''
    } catch (e) {
      error.value = '语音识别请求失败，请检查网络连接'
      return ''
    }
  }

  // Returns a Promise that resolves with the recognized text
  function stop() {
    return new Promise((resolve) => {
      resolvePromise = resolve
      if (mediaRecorder && mediaRecorder.state === 'recording') {
        // onstop handler will resolve the promise
        mediaRecorder.stop()
      } else {
        // Not recording — resolve immediately
        resolve('')
        resolvePromise = null
      }
      isListening.value = false
    })
  }

  function reset() {
    if (mediaRecorder && mediaRecorder.state === 'recording') {
      mediaRecorder.stop()
    }
    isListening.value = false
    isRecognizing.value = false
    error.value = null
    resolvePromise = null
  }

  onUnmounted(() => {
    if (mediaRecorder && mediaRecorder.state === 'recording') {
      mediaRecorder.stop()
    }
    if (stream) {
      stream.getTracks().forEach(t => t.stop())
    }
    resolvePromise = null
  })

  return {
    isListening,
    isRecognizing,
    error,
    supported,
    start,
    stop,
    reset
  }
}
