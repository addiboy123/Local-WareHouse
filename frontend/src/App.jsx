import { useEffect, useRef, useState } from 'react'
import './App.css'
import { api, downloadFile } from './api'

function App() {
  const [session, setSession] = useState(() => JSON.parse(localStorage.getItem('warehouse-session') || 'null'))
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({ email: '', password: '', username: '' })
  const [contents, setContents] = useState({ folders: [], files: [], folderId: null, folderName: 'My Drive' })
  const [trail, setTrail] = useState([])
  const [query, setQuery] = useState('')
  const [notice, setNotice] = useState('')
  const [loading, setLoading] = useState(false)
  const fileInput = useRef(null)

  useEffect(() => { if (session) loadContents(null, []) }, [session])

  async function loadContents(folderId, nextTrail = trail) {
    setLoading(true)
    try {
      const data = await api.contents(folderId)
      setContents(data)
      setTrail(nextTrail)
      setQuery('')
    } catch (error) { setNotice(error.message) } finally { setLoading(false) }
  }

  async function submitAuth(event) {
    event.preventDefault()
    try {
      const data = await api[mode](form)
      localStorage.setItem('warehouse-session', JSON.stringify(data))
      setSession(data)
      setNotice('')
    } catch (error) { setNotice(error.message) }
  }

  async function upload(event) {
    const file = event.target.files?.[0]
    if (!file) return
    try {
      await api.upload(file, contents.folderId)
      await loadContents(contents.folderId, trail)
      setNotice(`${file.name} uploaded`)
    } catch (error) { setNotice(error.message) }
    event.target.value = ''
  }

  async function createFolder() {
    const name = window.prompt('Folder name')
    if (!name?.trim()) return
    try { await api.createFolder(name.trim(), contents.folderId); await loadContents(contents.folderId, trail) }
    catch (error) { setNotice(error.message) }
  }

  async function removeFile(file) {
    if (!window.confirm(`Delete ${file.originalName}?`)) return
    try { await api.deleteFile(file.id); await loadContents(contents.folderId, trail) }
    catch (error) { setNotice(error.message) }
  }

  async function removeFolder(folder) {
    if (!window.confirm(`Delete ${folder.name} and its contents?`)) return
    try { await api.deleteFolder(folder.id); await loadContents(contents.folderId, trail) }
    catch (error) { setNotice(error.message) }
  }

  async function shareFile(file) {
    const email = window.prompt(`Email address to share ${file.originalName} with`)
    if (!email?.trim()) return
    try {
      await api.shareFile(file.id, email.trim())
      setNotice(`${file.originalName} shared with ${email.trim()}`)
    } catch (error) { setNotice(error.message) }
  }

  async function showSharedFiles() {
    setLoading(true)
    try {
      const page = await api.sharedFiles()
      setContents({ folderId: null, folderName: 'Shared with me', folders: [], files: page.content || page || [] })
      setTrail([])
      setQuery('')
    } catch (error) { setNotice(error.message) } finally { setLoading(false) }
  }

  function logout() { localStorage.removeItem('warehouse-session'); setSession(null) }

  if (!session) return <AuthScreen mode={mode} setMode={setMode} form={form} setForm={setForm} submitAuth={submitAuth} notice={notice} />

  const filteredFolders = contents.folders.filter((folder) => folder.name.toLowerCase().includes(query.toLowerCase()))
  const filteredFiles = contents.files.filter((file) => file.originalName.toLowerCase().includes(query.toLowerCase()))

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><span className="brand-mark">W</span><span>Warehouse</span></div>
      <button className="upload-button" onClick={() => fileInput.current?.click()}>＋ Upload</button>
      <input ref={fileInput} hidden type="file" onChange={upload} />
      <nav><button className="nav-item active" onClick={() => loadContents(null, [])}>▦ <span>My Drive</span></button><button className="nav-item" onClick={showSharedFiles}>⇄ <span>Shared with me</span></button></nav>
      <div className="storage"><span>Storage</span><div className="storage-track"><i /></div><small>Local storage · ready</small></div>
      <button className="user-row" onClick={logout}><span className="avatar">{(session.email || 'U')[0].toUpperCase()}</span><span><b>{session.email}</b><small>Sign out</small></span><span>⋯</span></button>
    </aside>
    <main className="workspace">
      <header className="topbar"><div className="search"><span>⌕</span><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search your files" /><kbd>/</kbd></div><button className="icon-button" title="Notifications">♧</button></header>
      <section className="content"><div className="title-row"><div><p className="eyebrow">Your workspace</p><h1>{contents.folderName}</h1></div><button className="new-folder" onClick={createFolder}>＋ New folder</button></div>
        <div className="breadcrumbs"><button onClick={() => loadContents(null, [])}>My Drive</button>{trail.map((folder, index) => <span key={folder.id}> / <button onClick={() => loadContents(folder.id, trail.slice(0, index + 1))}>{folder.name}</button></span>)}</div>
        {notice && <div className="notice">{notice}<button onClick={() => setNotice('')}>×</button></div>}
        <div className="section-heading"><h2>{query ? 'Search results' : 'Files and folders'}</h2><span>{loading ? 'Loading…' : `${filteredFolders.length + filteredFiles.length} items`}</span></div>
        <div className="file-list"><div className="list-head"><span>Name</span><span>Type</span><span>Modified</span><span>Actions</span></div>
          {filteredFolders.map((folder) => <div className="file-row" key={folder.id}><button className="file-name" onClick={() => loadContents(folder.id, [...trail, folder])}><span className="file-icon folder-icon">▰</span><b>{folder.name}</b></button><span>Folder</span><span>—</span><span><button className="row-action" title="Delete folder" onClick={() => removeFolder(folder)}>⌫</button></span></div>)}
          {filteredFiles.map((file) => <div className="file-row" key={file.id}><span className="file-name"><span className="file-icon">{file.mimeType?.startsWith('image/') ? '▧' : '▤'}</span><b>{file.originalName}</b></span><span>{formatType(file.mimeType)}</span><span>{formatDate(file.createdAt)}</span><span><button className="row-action" title="Share file" onClick={() => shareFile(file)}>↗</button><button className="row-action" title="Download" onClick={() => downloadFile(file)}>↓</button><button className="row-action danger" title="Delete file" onClick={() => removeFile(file)}>⌫</button></span></div>)}
          {!loading && !filteredFolders.length && !filteredFiles.length && <div className="empty"><div>◌</div><h3>This folder is empty</h3><p>Upload a file or create a folder to get started.</p></div>}
        </div>
      </section>
    </main>
  </div>
}

function AuthScreen({ mode, setMode, form, setForm, submitAuth, notice }) { return <main className="auth-page"><div className="auth-panel"><div className="brand"><span className="brand-mark">W</span><span>Warehouse</span></div><p className="eyebrow">Private file storage</p><h1>{mode === 'login' ? 'Welcome back.' : 'Create your workspace.'}</h1><p className="auth-copy">Keep your files organized, available, and under your control.</p><form onSubmit={submitAuth}>{mode === 'register' && <input required placeholder="Username" value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} />}<input required type="email" placeholder="Email address" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} /><input required minLength="6" type="password" placeholder="Password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />{notice && <div className="form-notice">{notice}</div>}<button className="submit-button">{mode === 'login' ? 'Sign in' : 'Create account'} <span>→</span></button></form><button className="switch-auth" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>{mode === 'login' ? 'New here? Create an account' : 'Already have an account? Sign in'}</button></div><div className="auth-art"><div className="art-note">A calmer place for your files.</div><div className="art-shape shape-one" /><div className="art-shape shape-two" /></div></main> }
function formatDate(value) { return value ? new Date(value).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' }) : '—' }
function formatType(value) { return value?.split('/').pop()?.toUpperCase() || 'FILE' }

export default App
