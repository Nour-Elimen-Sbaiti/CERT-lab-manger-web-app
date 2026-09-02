import { api } from '../api'

export async function getDashboard(token) {
  return api('/api/dashboard', { token })
}

export async function getAlertes(token, jours = 30) {
  return api(`/api/alertes?jours=${jours}`, { token })
}
