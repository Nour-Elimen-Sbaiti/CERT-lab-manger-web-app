import { api } from '../api'

export async function getAlertes(token, jours = 30) {
  return api(`/api/alertes?jours=${jours}`, { token })
}
