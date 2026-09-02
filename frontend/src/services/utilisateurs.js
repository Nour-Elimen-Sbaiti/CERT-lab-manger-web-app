import { api } from '../api'

export async function getUsers(token) {
  return api('/api/users', { token })
}

export async function toggleUser(token, userId, enabled) {
  const action = enabled ? 'desactiver' : 'activer'
  return api(`/api/users/${userId}/${action}`, {
    method: 'PATCH',
    token
  })
}

export async function deleteUser(token, userId) {
  return api(`/api/users/${userId}`, {
    method: 'DELETE',
    token
  })
}
