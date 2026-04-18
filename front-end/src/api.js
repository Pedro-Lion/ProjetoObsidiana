// src/services/api.js (ou onde você configura o axios)
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080'
})

// Adiciona o token em toda requisição
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Intercepta respostas 401 e tenta renovar o token
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Se recebeu 401 e ainda não tentou renovar
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Já está renovando — enfileira a requisição
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`
          return api(originalRequest)
        }).catch(err => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      const tokenAtual = localStorage.getItem('token')

      try {
        // Chama o endpoint de refresh no microserviço
        const response = await axios.post(
          'http://localhost:8081/auth/refresh',
          {},
          { headers: { Authorization: `Bearer ${tokenAtual}` } }
        )

        const novoToken = response.data.token
        localStorage.setItem('token', novoToken)
        api.defaults.headers.common.Authorization = `Bearer ${novoToken}`

        processQueue(null, novoToken)

        // Reexecuta a requisição original com o novo token
        originalRequest.headers.Authorization = `Bearer ${novoToken}`
        return api(originalRequest)

      } catch (refreshError) {
        processQueue(refreshError, null)
        // Refresh falhou — redireciona para login
        localStorage.removeItem('token')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default api