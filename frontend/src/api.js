const API = '/api'

async function request(path, options = {}) {
  const token = JSON.parse(localStorage.getItem('warehouse-session') || 'null')?.token
  const headers = new Headers(options.headers)
  if (token) headers.set('Authorization', `Bearer ${token}`)
  if (options.body && !(options.body instanceof FormData)) headers.set('Content-Type', 'application/json')
  const response = await fetch(`${API}${path}`, { ...options, headers })
  if (!response.ok) {
    let message = `Request failed (${response.status})`
    try { message = (await response.json()).message || message } catch { /* non-JSON response */ }
    throw new Error(message)
  }
  return response.status === 204 ? null : response.json().catch(() => null)
}

export const api = {
  login: (body) => request('/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  register: (body) => request('/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  contents: (folderId) => request(folderId ? `/folders/${folderId}` : '/folders/root'),
  sharedFiles: () => request('/share/files?page=0&size=100'),
  shareFile: (resourceId, email, permission = 'READ') => request('/share/file/email', { method: 'POST', body: JSON.stringify({ resourceId, email, permission }) }),
  upload: (file, folderId) => { const body = new FormData(); body.append('file', file); if (folderId) body.append('folderId', folderId); return request('/files', { method: 'POST', body }) },
  createFolder: (name, parentId) => request('/folders', { method: 'POST', body: JSON.stringify({ name, parentId }) }),
  deleteFile: (id) => request(`/files/${id}`, { method: 'DELETE' }),
  deleteFolder: (id) => request(`/folders/${id}`, { method: 'DELETE' }),
}

export async function downloadFile(file) {
  const token = JSON.parse(localStorage.getItem('warehouse-session') || 'null')?.token
  const response = await fetch(`${API}/files/${file.id}`, { headers: { Authorization: `Bearer ${token}` } })
  const type = response.headers.get('content-type') || ''
  if (!response.ok || type.includes('json') || type.includes('html')) {
    let message = 'Download failed'
    try { message = (await response.json()).message || message } catch { /* response was not a JSON error */ }
    throw new Error(message)
  }
  const blob = await response.blob()
  const link = document.createElement('a'); link.href = URL.createObjectURL(blob); link.download = file.originalName; link.click(); URL.revokeObjectURL(link.href)
}