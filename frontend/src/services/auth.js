import { api } from '../api'

export async function login(username, password) {
  return api('/api/auth/login', {
    method: 'POST',
    body: { username, password }
  })
}

export async function register(data) {
  return api('/api/auth/register', {
    method: 'POST',
    body: data
  })
}

export async function forgotPassword(email) {
  return api('/api/auth/forgot-password', {
    method: 'POST',
    body: { email }
  })
}

export async function me(token) {
  return api('/api/auth/me', { token })
}
