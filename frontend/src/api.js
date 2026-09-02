export const baseUrl = import.meta.env.VITE_API_URL || ''

function message(data, fallback) {
  if (typeof data === 'string') return data
  if (data?.message) return data.message
  if (data?.error) return data.error
  if (data?.errors) return Object.values(data.errors).join(', ')
  return fallback
}

export async function api(path, { method = 'GET', body, token, formData = false, raw = false } = {}) {
  const headers = {}
  if (token) headers.Authorization = `Bearer ${token}`
  if (body && !formData) headers['Content-Type'] = 'application/json'
  const response = await fetch(`${baseUrl}${path}`, {
    method, headers, body: body ? (formData ? body : JSON.stringify(body)) : undefined
  })
  if (raw) {
    if (!response.ok) throw new Error(await response.text() || 'Téléchargement impossible')
    return response.blob()
  }
  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json') ? await response.json() : await response.text()
  if (!response.ok) throw new Error(message(data, `Erreur ${response.status}`))
  return data
}
