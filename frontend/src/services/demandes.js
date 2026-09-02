import { api } from '../api'

export async function getDemandes(token) {
  return api('/api/demandes-prestation', { token })
}

export async function createDemande(token, data) {
  return api('/api/demandes-prestation', {
    method: 'POST',
    body: data,
    token
  })
}

export async function updateDemande(token, id, data) {
  return api(`/api/demandes-prestation/${id}`, {
    method: 'PUT',
    body: data,
    token
  })
}

export async function deleteDemande(token, id) {
  return api(`/api/demandes-prestation/${id}`, {
    method: 'DELETE',
    token
  })
}

export async function downloadDemandePdf(token, id) {
  return api(`/api/demandes-prestation/${id}/pdf`, {
    token,
    raw: true
  })
}
