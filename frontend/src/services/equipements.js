import { api } from '../api'

export async function getEquipements(token, params = {}) {
  const queryString = new URLSearchParams(params).toString()
  const path = queryString
    ? `/api/equipements?${queryString}`
    : '/api/equipements'
  return api(path, { token })
}

export async function createEquipement(token, data) {
  return api('/api/equipements', {
    method: 'POST',
    body: data,
    token
  })
}

export async function updateEquipement(token, id, data) {
  return api(`/api/equipements/${id}`, {
    method: 'PUT',
    body: data,
    token
  })
}

export async function changeEquipementState(token, id, etat) {
  return api(`/api/equipements/${id}/etat?etat=${etat}`, {
    method: 'PATCH',
    token
  })
}

export async function getDocumentsByEquipement(token, equipementId) {
  return api(`/api/documents/equipement/${equipementId}`, { token })
}

export async function uploadDocument(token, equipementId, typeDocument, fichier) {
  const formData = new FormData()
  formData.append('equipementId', equipementId)
  formData.append('typeDocument', typeDocument)
  formData.append('fichier', fichier)
  return api('/api/documents', {
    method: 'POST',
    body: formData,
    token,
    formData: true
  })
}

export async function deleteDocument(token, documentId) {
  return api(`/api/documents/${documentId}`, {
    method: 'DELETE',
    token
  })
}

export async function getFicheDeVie(token, equipementId) {
  return api(`/api/equipements/${equipementId}/fiche-de-vie`, {
    token,
    raw: true
  })
}

export async function getInterventionsByEquipement(token, equipementId) {
  return api(`/api/interventions/equipement/${equipementId}`, { token })
}
