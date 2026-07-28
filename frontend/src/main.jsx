import React, { useEffect, useMemo, useState, useRef } from 'react'
import { createRoot } from 'react-dom/client'
import { api, baseUrl } from './api'
import './styles.css'

const stateLabels = { NOUVEAU: 'Nouveau', DISPONIBLE: 'Disponible', EN_UTILISATION: 'En utilisation', EN_ETALONNAGE: 'Étalonnage', EN_MAINTENANCE: 'Maintenance', EN_PANNE: 'En panne', HORS_SERVICE: 'Hors service', REFORME: 'Réformé' }
const states = Object.keys(stateLabels)
const nav = [
  ['dashboard', '▦', 'Vue d’ensemble'], ['equipements', '⌘', 'Équipements'], ['alertes', '◉', 'Alertes'],
  ['laboratoires', '◌', 'Référentiel labos'], ['demandes', '▤', 'Demandes']
]
const emptyEquipment = { codeIdentifiant:'', codeEmplacement:'', designation:'', marque:'', modele:'', numeroSerie:'', fournisseur:'', dateAcquisition:'', dateMiseEnService:'', valeurOrigine:'', periodiciteValeur:'12', periodiciteUnite:'MOIS', garantieJusquAu:'', observations:'' }

function friendlyError(e) { return e.message || 'Une erreur est survenue.' }
function fmtDate(d) { return d ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`)) : '—' }
function Badge({ state }) { return <span className={`badge ${state?.toLowerCase().replaceAll('_','-')}`}>{stateLabels[state] || state}</span> }
function IconButton({ children, title, ...props }) { return <button className="icon-button" title={title} {...props}>{children}</button> }

const EyeIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.8} stroke="currentColor" style={{ width: '20px', height: '20px' }}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M2.036 12.322a1.012 1.012 0 0 1 0-.639C3.423 7.51 7.36 4.5 12 4.5c4.638 0 8.573 3.007 9.963 7.178.07.207.07.431 0 .639C20.577 16.49 16.64 19.5 12 19.5c-4.638 0-8.573-3.007-9.963-7.178Z" />
    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z" />
  </svg>
)

const EyeSlashIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.8} stroke="currentColor" style={{ width: '20px', height: '20px' }}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M3.98 8.223A10.477 10.477 0 0 0 1.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.451 10.451 0 0 1 12 4.5c4.756 0 8.773 3.162 10.065 7.498a10.522 10.522 0 0 1-4.293 5.774M6.228 6.228 3 3m3.228 3.228 3.65 3.65m7.815 7.815 3 3m-3-3-3.65-3.65m0 0a3 3 0 1 0-4.243-4.243m4.242 4.242L9.88 9.88" />
  </svg>
)

const BellIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.8} stroke="currentColor" style={{ width: '20px', height: '20px' }}>
    <path strokeLinecap="round" strokeLinejoin="round" d="M14.857 17.082a23.848 23.848 0 0 0 5.454-1.31A8.967 8.967 0 0 1 18 9.75V9A6 6 0 0 0 6 9v.75a8.967 8.967 0 0 1-2.312 6.022c1.733.64 3.56 1.085 5.455 1.31m5.714 0a24.255 24.255 0 0 1-5.714 0m5.714 0a3 3 0 1 1-5.714 0" />
  </svg>
)

const TrashIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.7} stroke="currentColor" style={{ width: '18px', height: '18px' }}>
    <path strokeLinecap="round" strokeLinejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
  </svg>
)

function getEtatStyle(etat) {
  const lower = etat?.toLowerCase().replaceAll('_', '-');
  if (lower === 'disponible') return { color: '#16835d', bg: '#e0f5ea' };
  if (lower === 'en-utilisation') return { color: '#3172ba', bg: '#e7f0fc' };
  if (lower === 'en-etalonnage' || lower === 'en-maintenance') return { color: '#ba7914', bg: '#fff2d7' };
  if (lower === 'en-panne' || lower === 'hors-service' || lower === 'reforme') return { color: '#c65355', bg: '#fce9e9' };
  return { color: '#7160bb', bg: '#f0ecff' }; // nouveau
}

function Login({ onLogin, onGoToRegister, onGoToForgotPassword, notice }) {
  const [values, setValues] = useState({ username:'', password:'' }), [error, setError] = useState(''), [busy,setBusy] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [rememberMe, setRememberMe] = useState(false)

  useEffect(() => {
    const saved = localStorage.getItem('remembered-username')
    if (saved) {
      setValues(v => ({ ...v, username: saved }))
      setRememberMe(true)
    }
  }, [])

  async function submit(e) { 
    e.preventDefault(); 
    setBusy(true); 
    setError(''); 
    try { 
      const user = await api('/api/auth/login',{method:'POST',body:values}); 
      if (rememberMe) {
        localStorage.setItem('remembered-username', values.username)
      } else {
        localStorage.removeItem('remembered-username')
      }
      onLogin(user) 
    } catch(e) { 
      setError(friendlyError(e)) 
    } finally { 
      setBusy(false) 
    } 
  }

  return <main className="login-page"><section className="login-aside"><div className="brand" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '15px', width: '100%', marginBottom: '30px' }}><img src="/logo_cert.png" alt="Logo CERT" style={{ height: '144px', width: 'auto', objectFit: 'contain' }} /><span style={{ fontSize: '26px', letterSpacing: '1px', textAlign: 'center' }}>CERT <b>CALIBRATION HUB</b></span></div><div className="login-hero"><p className="eyebrow">GESTION MÉTROLOGIQUE</p><h1>La précision,<br/><em>en toute confiance.</em></h1><p>Pilotez vos équipements, étalonnages et échéances depuis un espace unique et sécurisé.</p><div className="hero-stats"><span><b>24/7</b> suivi centralisé</span><span><b>100%</b> traçabilité</span></div></div><small>© 2026 CERT Calibration Hub</small></section><section className="login-card"><div><p className="eyebrow">BON RETOUR</p><h2>Connectez-vous à votre espace</h2><p className="muted">Saisissez vos identifiants pour continuer.</p></div>{notice && <div className={notice.type === 'error' ? 'error' : 'notice'}>{notice.text}</div>}<form onSubmit={submit}><label>Nom d’utilisateur<input autoFocus value={values.username} onChange={e=>setValues({...values,username:e.target.value})} placeholder="ex. jdupont" required/></label><label>Mot de passe<div style={{ position: 'relative' }}><input type={showPassword ? 'text' : 'password'} value={values.password} onChange={e=>setValues({...values,password:e.target.value})} placeholder="••••••••" required style={{ paddingRight: '45px' }} /><button type="button" onClick={()=>setShowPassword(!showPassword)} style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', color: '#6d8194', padding: '5px' }}>{showPassword ? <EyeSlashIcon /> : <EyeIcon />}</button></div></label><div className="form-row"><label className="checkbox"><input type="checkbox" checked={rememberMe} onChange={e => setRememberMe(e.target.checked)}/> Se souvenir de moi</label><button type="button" className="link" onClick={onGoToForgotPassword}>Mot de passe oublié ?</button></div>{error && <div className="error">{error}</div>}<button className="primary wide" disabled={busy}>{busy ? 'Connexion…' : 'Se connecter'} <span>→</span></button></form><p className="muted" style={{ textAlign: 'center', fontSize: '13px', marginTop: '20px' }}>Nouveau sur la plateforme ? <button type="button" className="link" onClick={onGoToRegister}>Créer un compte</button></p><p className="login-help">Accès réservé aux utilisateurs autorisés.<br/>Contactez votre administrateur si nécessaire.</p></section></main>
}

function Register({ onGoToLogin }) {
  const [values, setValues] = useState({ nom: '', prenom: '', email: '', username: '', password: '', role: 'PERSONNEL_AUTORISE' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [busy, setBusy] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setBusy(true)
    setError('')
    setSuccess('')
    try {
      const responseText = await api('/api/auth/register', {
        method: 'POST',
        body: values
      })
      setSuccess(responseText || 'Compte créé avec succès. Un email d\'activation a été envoyé.')
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-aside">
        <div className="brand" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '15px', width: '100%', marginBottom: '30px' }}>
          <img src="/logo_cert.png" alt="Logo CERT" style={{ height: '144px', width: 'auto', objectFit: 'contain' }} />
          <span style={{ fontSize: '26px', letterSpacing: '1px', textAlign: 'center' }}>CERT <b>CALIBRATION HUB</b></span>
        </div>
        <div className="login-hero">
          <p className="eyebrow">GESTION MÉTROLOGIQUE</p>
          <h1>Rejoignez<br/><em>notre plateforme.</em></h1>
          <p>Créez votre compte pour suivre vos équipements, étalonnages et échéances.</p>
          <div className="hero-stats">
            <span><b>24/7</b> suivi centralisé</span>
            <span><b>100%</b> traçabilité</span>
          </div>
        </div>
        <small>© 2026 CERT Calibration Hub</small>
      </section>
      <section className="login-card">
        <div>
          <p className="eyebrow">INSCRIPTION</p>
          <h2>Créer un compte</h2>
          <p className="muted">Remplissez les informations ci-dessous.</p>
        </div>
        
        {success ? (
          <div style={{ marginTop: '24px' }}>
            <div className="notice" style={{ background: '#e0f5ea', color: '#16835d', marginBottom: '20px' }}>
              {success}
            </div>
            <button className="primary wide" onClick={onGoToLogin}>Aller à la connexion</button>
          </div>
        ) : (
          <form onSubmit={submit}>
            <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              <label>Prénom
                <input value={values.prenom} onChange={e=>setValues({...values,prenom:e.target.value})} placeholder="Jean" required/>
              </label>
              <label>Nom
                <input value={values.nom} onChange={e=>setValues({...values,nom:e.target.value})} placeholder="Dupont" required/>
              </label>
            </div>
            <label>Email
              <input type="email" value={values.email} onChange={e=>setValues({...values,email:e.target.value})} placeholder="jean.dupont@exemple.com" required/>
            </label>
            <label>Nom d’utilisateur
              <input value={values.username} onChange={e=>setValues({...values,username:e.target.value})} placeholder="jdupont" required/>
            </label>
            <label>Mot de passe
              <div style={{ position: 'relative' }}>
                <input type={showPassword ? 'text' : 'password'} value={values.password} onChange={e=>setValues({...values,password:e.target.value})} placeholder="••••••••" required minLength={8} style={{ paddingRight: '45px' }}/>
                <button type="button" onClick={()=>setShowPassword(!showPassword)} style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', color: '#6d8194', padding: '5px' }}>{showPassword ? <EyeSlashIcon /> : <EyeIcon />}</button>
              </div>
            </label>
            <label>Rôle
              <select value={values.role} onChange={e=>setValues({...values,role:e.target.value})} style={{ display: 'block', width: '100%', marginTop: '7px', border: '1px solid #d8e2e9', borderRadius: '6px', padding: '10px', outlineColor: '#1a988b', color: '#263c51', font: 'inherit' }} required>
                <option value="PERSONNEL_AUTORISE">Personnel Autorisé</option>
                <option value="CHEF_LABORATOIRE">Chef de Laboratoire</option>
                <option value="ADMINISTRATEUR">Administrateur</option>
              </select>
            </label>

            {error && <div className="error">{error}</div>}
            
            <button className="primary wide" disabled={busy}>
              {busy ? 'Création du compte…' : 'S\'inscrire'} <span>→</span>
            </button>
            
            <p className="muted" style={{ textAlign: 'center', fontSize: '13px', marginTop: '10px' }}>
              Déjà un compte ? <button type="button" className="link" onClick={onGoToLogin}>Se connecter</button>
            </p>
          </form>
        )}
      </section>
    </main>
  )
}

function ForgotPassword({ onGoToLogin }) {
  const [email, setEmail] = useState('')
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setBusy(true)
    setError('')
    setSuccess('')
    try {
      const msg = await api('/api/auth/forgot-password', {
        method: 'POST',
        body: { email }
      })
      setSuccess(msg || 'Un email de réinitialisation a été envoyé si le compte existe.')
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-aside">
        <div className="brand" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '15px', width: '100%', marginBottom: '30px' }}>
          <img src="/logo_cert.png" alt="Logo CERT" style={{ height: '144px', width: 'auto', objectFit: 'contain' }} />
          <span style={{ fontSize: '26px', letterSpacing: '1px', textAlign: 'center' }}>CERT <b>CALIBRATION HUB</b></span>
        </div>
        <div className="login-hero">
          <p className="eyebrow">RÉINITIALISATION</p>
          <h1>Pas de panique,<br/><em>on s'en occupe.</em></h1>
          <p>Saisissez votre adresse email pour recevoir un lien sécurisé de réinitialisation de mot de passe.</p>
        </div>
        <small>© 2026 CERT Calibration Hub</small>
      </section>
      <section className="login-card">
        <div>
          <p className="eyebrow">SÉCURITÉ</p>
          <h2>Mot de passe oublié ?</h2>
          <p className="muted">Saisissez votre adresse email pour continuer.</p>
        </div>

        {success ? (
          <div style={{ marginTop: '24px' }}>
            <div className="notice" style={{ background: '#e0f5ea', color: '#16835d', marginBottom: '20px' }}>
              {success}
            </div>
            <button className="primary wide" onClick={onGoToLogin}>Retour à la connexion</button>
          </div>
        ) : (
          <form onSubmit={submit}>
            <label>Adresse email
              <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="ex. jdupont@exemple.com" required autoFocus/>
            </label>

            {error && <div className="error">{error}</div>}

            <button className="primary wide" disabled={busy}>
              {busy ? 'Envoi en cours…' : 'Envoyer le lien'} <span>→</span>
            </button>

            <p className="muted" style={{ textAlign: 'center', fontSize: '13px', marginTop: '10px' }}>
              <button type="button" className="link" onClick={onGoToLogin}>Retour à la connexion</button>
            </p>
          </form>
        )}
      </section>
    </main>
  )
}

function ProfileModal({ token, onClose, onLogout, refreshKey, onPhotoChanged }) {
  const [profile, setProfile] = useState(null)
  const [error, setError] = useState('')
  const [photoBusy, setPhotoBusy] = useState(false)
  const [photoError, setPhotoError] = useState('')
  const [photoVersion, setPhotoVersion] = useState(0)
  const fileInputRef = useRef(null)

  useEffect(() => {
    api('/api/auth/me', { token })
      .then(setProfile)
      .catch(e => setError(friendlyError(e)))
  }, [token, refreshKey])

  async function handlePhotoChange(e) {
    const file = e.target.files?.[0]
    e.target.value = ''
    if (!file) return
    if (!file.type.startsWith('image/')) { setPhotoError('Veuillez sélectionner un fichier image.'); return }
    if (file.size > 5 * 1024 * 1024) { setPhotoError('L’image ne doit pas dépasser 5 Mo.'); return }
    setPhotoError('')
    setPhotoBusy(true)
    try {
      const fd = new FormData()
      fd.append('photo', file)
      const updated = await api('/api/auth/me/photo', { method: 'POST', token, body: fd, formData: true })
      setProfile(updated)
      setPhotoVersion(v => v + 1)
      onPhotoChanged?.(updated.photoUrl)
    } catch (e) {
      setPhotoError(friendlyError(e))
    } finally {
      setPhotoBusy(false)
    }
  }

  const photoSrc = profile?.photoUrl ? `${baseUrl}${profile.photoUrl}?v=${photoVersion}` : null

  return (
    <Modal title="Mon Compte" onClose={onClose}>
      {error ? (
        <Notice text={error} />
      ) : !profile ? (
        <Loader />
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', borderBottom: '1px solid #edf1f3', paddingBottom: '16px' }}>
            <div style={{ position: 'relative', width: '60px', height: '60px', flexShrink: 0 }}>
              <div className="avatar" style={{ width: '60px', height: '60px', fontSize: '20px', fontWeight: 'bold', overflow: 'hidden' }}>
                {photoSrc ? <img src={photoSrc} alt="Photo de profil" className="avatar-img" /> : profile.username?.slice(0,2).toUpperCase()}
              </div>
              <button
                type="button"
                title="Changer la photo"
                onClick={() => fileInputRef.current?.click()}
                disabled={photoBusy}
                style={{ position: 'absolute', bottom: '-2px', right: '-2px', width: '24px', height: '24px', borderRadius: '50%', border: '2px solid #fff', background: '#137d73', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: photoBusy ? 'default' : 'pointer', fontSize: '12px', padding: 0 }}
              >
                {photoBusy ? '…' : '✎'}
              </button>
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handlePhotoChange}
                style={{ display: 'none' }}
              />
            </div>
            <div>
              <h3 style={{ fontSize: '18px', color: '#1e3044' }}>{profile.prenom} {profile.nom}</h3>
              <span className="muted" style={{ fontSize: '14px' }}>@{profile.username}</span>
            </div>
          </div>

          {photoError && <div className="error">{photoError}</div>}
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px 20px' }}>
            <div>
              <small style={{ fontWeight: 'bold', textTransform: 'uppercase', fontSize: '10px', color: '#8190a0' }}>Email</small>
              <p style={{ color: '#273b4f', fontSize: '14px', marginTop: '4px' }}>{profile.email}</p>
            </div>
            <div>
              <small style={{ fontWeight: 'bold', textTransform: 'uppercase', fontSize: '10px', color: '#8190a0' }}>Rôle</small>
              <p style={{ marginTop: '4px' }}><span className="role">{profile.role?.replace('_', ' ')}</span></p>
            </div>
            <div>
              <small style={{ fontWeight: 'bold', textTransform: 'uppercase', fontSize: '10px', color: '#8190a0' }}>Statut du compte</small>
              <p style={{ color: '#273b4f', fontSize: '14px', marginTop: '4px' }}>
                <span className={profile.enabled ? 'online' : 'offline'}>
                  {profile.enabled ? '● Actif' : '● Inactif'}
                </span>
              </p>
            </div>
            <div>
              <small style={{ fontWeight: 'bold', textTransform: 'uppercase', fontSize: '10px', color: '#8190a0' }}>Membre depuis</small>
              <p style={{ color: '#273b4f', fontSize: '14px', marginTop: '4px' }}>{fmtDate(profile.createdAt?.slice(0, 10))}</p>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', borderTop: '1px solid #edf1f3', paddingTop: '16px', marginTop: '10px' }}>
            <button className="secondary" onClick={() => { onLogout(); onClose(); }} style={{ background: '#fff0ef', color: '#b54745', border: '1px solid #fce9e9' }}>
              ⇥ Se déconnecter
            </button>
            <button className="primary" onClick={onClose}>Fermer</button>
          </div>
        </div>
      )}
    </Modal>
  )
}

function Shell({ user, page, setPage, children, onLogout, onAvatarClick, alertCount, avatarSrc }) {
  const admin = user.role === 'ADMINISTRATEUR';
  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '10px', padding: '10px 0', width: '100%' }}>
          <img src="/logo_cert.png" alt="Logo CERT" style={{ height: '120px', width: 'auto', objectFit: 'contain' }} />
          <span style={{ fontSize: '15px', letterSpacing: '0.5px', textAlign: 'center', fontWeight: 'bold' }}>CERT <b>CALIBRATION HUB</b></span>
        </div>
        <nav>
          {nav.map(([id,icon,label])=><button key={id} className={page===id?'active':''} onClick={()=>setPage(id)}><i>{icon}</i>{label}</button>)}
          {admin && <><div className="nav-label">ADMINISTRATION</div><button className={page==='utilisateurs'?'active':''} onClick={()=>setPage('utilisateurs')}><i>♧</i>Utilisateurs</button></>}
        </nav>
        <div className="sidebar-bottom">
          <button className="logout" onClick={onLogout} style={{ marginTop: '20px', width: '100%' }}>⇥ Déconnexion</button>
        </div>
      </aside>
      <section className="workspace">
        <header>
          <div className="crumb">CERT CALIBRATION HUB <span>/</span> {nav.find(n=>n[0]===page)?.[2] || 'Administration'}</div>
          <div className="header-actions">
            <span className="date">{new Intl.DateTimeFormat('fr-FR',{weekday:'long',day:'numeric',month:'long'}).format(new Date())}</span>
            <button className="bell" onClick={() => setPage('alertes')} title={`${alertCount || 0} alertes actives`}>
              <BellIcon />
              {alertCount > 0 && <span className="bell-badge">{alertCount}</span>}
            </button>
            <div className="avatar" onClick={onAvatarClick} style={{ cursor: 'pointer', overflow: 'hidden' }}>
              {avatarSrc ? <img src={avatarSrc} alt="Photo de profil" className="avatar-img" /> : user.username?.slice(0,2).toUpperCase()}
            </div>
          </div>
        </header>
        {children}
      </section>
    </div>
  )
}

function Dashboard({ token, username, setPage }) {
  const [data, setData] = useState(null)
  const [alerts, setAlerts] = useState([])
  const [error, setError] = useState('')
  const [busyAlert, setBusyAlert] = useState('')

  const load = async () => {
    setError('')
    try {
      const [dashboard, alertList] = await Promise.all([
        api('/api/dashboard', { token }),
        api('/api/alertes?jours=30', { token })
      ])
      setData(dashboard)
      setAlerts(Array.isArray(alertList) ? alertList : [])
    } catch (e) {
      setError(friendlyError(e))
    }
  }

  useEffect(() => { load() }, [token])

  async function resolveAlert(alert) {
    const observation = prompt('Observation de résolution', 'Action planifiée / problème traité')
    if (observation === null) return
    setBusyAlert(alert.id)
    try {
      await api(`/api/alertes/${encodeURIComponent(alert.id)}/resoudre`, { method: 'PATCH', token, body: { observation } })
      await load()
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusyAlert('')
    }
  }

  const chart = data?.repartitionParEtat || {}
  return (
    <div className="content">
      <div className="page-intro lab-intro">
        <div>
          <p className="eyebrow">TABLEAU DE BORD</p>
          <h1>Bonjour, {username}</h1>
          <p className="muted">Une vue claire du parc métrologique, des échéances et de la traçabilité qualité.</p>
        </div>
        <div className="intro-actions">
          <button className="secondary" onClick={() => setPage('alertes')}>Voir les alertes</button>
          <button className="primary" onClick={() => setPage('equipements')}>+ Nouvel équipement</button>
        </div>
      </div>
      {error ? <Notice text={error} /> : !data ? <Loader /> : (
        <>
          <div className="stats">
            <Stat label="Équipements" value={data.totalEquipements} icon="⌘" tone="blue" />
            <Stat label="En service" value={(chart.DISPONIBLE || 0) + (chart.EN_UTILISATION || 0)} icon="✓" tone="green" />
            <Stat label="Alertes actives" value={data.nombreAlertesActives ?? alerts.length} icon="!" tone="amber" />
            <Stat label="Laboratoires" value={data.totalLabosEtalonnage ?? data.totalPersonnel} icon="◌" tone="purple" />
          </div>
          <div className="dashboard-grid">
            <section className="panel status-panel">
              <div className="panel-head">
                <div><h3>État des équipements</h3><p>Répartition du parc du laboratoire</p></div>
                <button className="link" onClick={() => setPage('equipements')}>Voir tout →</button>
              </div>
              <div className="distribution">
                {states.filter(s => chart[s]).map(s => (
                  <div key={s}>
                    <div><Badge state={s} /><b>{chart[s]}</b></div>
                    <span style={{ width: `${Math.max(8, chart[s] / Math.max(1, data.totalEquipements) * 100)}%` }} />
                  </div>
                ))}
                {!Object.keys(chart).length && <Empty text="Aucun équipement enregistré" />}
              </div>
            </section>
            <section className="panel alerts-panel">
              <div className="panel-head">
                <div><h3>Échéances à surveiller</h3><p>Alertes actives dans les 30 prochains jours</p></div>
                <button className="link" onClick={() => setPage('alertes')}>Tout voir →</button>
              </div>
              {alerts.slice(0, 4).map((a, i) => {
                const risk = getAlertRisk(a.joursRestants)
                return (
                  <div className="alert-line actionable" key={a.id || i}>
                    <span className="alert-dot" style={{ backgroundColor: risk.bg, color: risk.color }}>!</span>
                    <div><b>{a.equipementDesignation}</b><small>{a.details}</small></div>
                    <time style={{ color: risk.color }}>{a.joursRestants} j</time>
                    {a.id && <button className="secondary small" disabled={busyAlert === a.id} onClick={() => resolveAlert(a)}>Résolu</button>}
                  </div>
                )
              })}
              {!alerts.length && <Empty text="Aucune échéance proche" />}
            </section>
          </div>
        </>
      )}
    </div>
  )
}
function Stat({label,value,icon,tone}) {return <div className="stat"><span className={`stat-icon ${tone}`}>{icon}</span><div><small>{label}</small><strong>{value}</strong></div></div>}
function Loader(){return <div className="loader">Chargement des données…</div>}; function Empty({text}){return <div className="empty">{text}</div>}; function Notice({text}){return <div className="notice">{text}</div>}

function exportToCSV(items) {
  if (!items || !items.length) return;
  const headers = ['ID', 'Designation', 'Code Identifiant', 'Marque', 'Modele', 'Numero Serie', 'Emplacement', 'Etat', 'Fournisseur', 'Date Acquisition', 'Date Mise En Service', 'Valeur Origine', 'Garantie JusquAu', 'Observations'];
  const csvRows = [headers.join(',')];
  
  for (const item of items) {
    const values = [
      item.id,
      `"${(item.designation || '').replace(/"/g, '""')}"`,
      `"${(item.codeIdentifiant || '').replace(/"/g, '""')}"`,
      `"${(item.marque || '').replace(/"/g, '""')}"`,
      `"${(item.modele || '').replace(/"/g, '""')}"`,
      `"${(item.numeroSerie || '').replace(/"/g, '""')}"`,
      `"${(item.codeEmplacement || '').replace(/"/g, '""')}"`,
      `"${(item.etat || '').replace(/"/g, '""')}"`,
      `"${(item.fournisseur || '').replace(/"/g, '""')}"`,
      item.dateAcquisition || '',
      item.dateMiseEnService || '',
      item.valeurOrigine || '',
      item.garantieJusquAu || '',
      `"${(item.observations || '').replace(/"/g, '""')}"`
    ];
    csvRows.push(values.join(','));
  }
  
  const csvContent = "data:text/csv;charset=utf-8,\uFEFF" + csvRows.join("\n");
  const encodedUri = encodeURI(csvContent);
  const link = document.createElement("a");
  link.setAttribute("href", encodedUri);
  link.setAttribute("download", `equipements_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

function Equipments({token}) { 
  const [items,setItems]=useState([]),[q,setQ]=useState(''),[filter,setFilter]=useState(''),[loading,setLoading]=useState(true),[error,setError]=useState(''),[editing,setEditing]=useState(null),[selected,setSelected]=useState(null); 
  const load=async()=>{setLoading(true);try{let path='/api/equipements';const params=new URLSearchParams();if(q)params.set('q',q);if(filter)params.set('etat',filter);if(params.size)path+='?'+params;setItems(await api(path,{token}))}catch(e){setError(friendlyError(e))}finally{setLoading(false)}}; 
  useEffect(()=>{const t=setTimeout(load,220);return()=>clearTimeout(t)},[q,filter,token]); 
  
  async function changeState(id,etat){
    if (!confirm("Voulez-vous vraiment changer l'état de cet équipement ?")) return;
    try{await api(`/api/equipements/${id}/etat?etat=${etat}`,{method:'PATCH',token});load()}catch(e){setError(friendlyError(e))}
  } 

  async function removeEquipment(e){
    if (!confirm(`Voulez-vous vraiment supprimer définitivement l'équipement "${e.designation}" ?\n\nCette action est irréversible : l'équipement et tout son historique lié (interventions, étalonnages, documents, contrats de maintenance, accessoires) seront effacés.`)) return;
    try{await api(`/api/equipements/${e.id}/definitif`,{method:'DELETE',token});setSelected(null);load()}catch(err){setError(friendlyError(err))}
  }
  
  return <div className="content"><div className="page-intro"><div><p className="eyebrow">PARC MÉTROLOGIQUE</p><h1>Équipements</h1><p className="muted">Gérez et suivez tous les équipements de votre laboratoire.</p></div><button className="primary" onClick={()=>setEditing(emptyEquipment)}>+ Ajouter un équipement</button></div><div className="toolbar"><div className="search" style={{ width: '420px' }}>⌕<input value={q} onChange={e=>setQ(e.target.value)} placeholder="Rechercher (désignation, marque, modèle, n° série, emplacement...)"/></div><select value={filter} onChange={e=>setFilter(e.target.value)}><option value="">Tous les états</option>{states.map(s=><option value={s} key={s}>{stateLabels[s]}</option>)}</select><button className="secondary" onClick={() => exportToCSV(items)} style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>📥 Exporter CSV</button><span className="result-count">{items.length} résultat{items.length!==1?'s':''}</span></div>{error&&<Notice text={error}/>}<div className="panel table-wrap">{loading?<Loader/>:<table><thead><tr><th>ÉQUIPEMENT</th><th>IDENTIFIANT</th><th>EMPLACEMENT</th><th>ÉTAT</th><th>PROCHAINE ÉCHÉANCE</th><th></th></tr></thead><tbody>{items.map(e=><tr key={e.id} onClick={()=>setSelected(e)}><td><div className="equipment-cell"><span className="equipment-icon">⌘</span><div><b>{e.designation}</b><small>{e.marque} · {e.modele}</small></div></div></td><td><code>{e.codeIdentifiant}</code></td><td>{e.codeEmplacement||'—'}</td><td><select value={e.etat} onChange={ev=>{ev.stopPropagation(); changeState(e.id, ev.target.value);}} onClick={ev=>ev.stopPropagation()} style={{ ...getEtatStyle(e.etat), border:'none', borderRadius:'5px', padding:'6px 12px', fontSize:'11px', fontWeight:'bold', cursor:'pointer', outline:'none', appearance:'none', textAlign:'center', display:'inline-block' }}>{states.map(s=><option key={s} value={s} style={{ color:'#1c2c3f', background:'white', fontWeight:'normal' }}>{stateLabels[s]}</option>)}</select></td><td>{e.garantieJusquAu?fmtDate(e.garantieJusquAu):'—'}</td><td><div style={{ display:'flex', gap:'4px', justifyContent:'flex-end' }}><IconButton title="Modifier l’équipement" onClick={ev=>{ev.stopPropagation();setEditing(e)}}>•••</IconButton><IconButton title="Supprimer l’équipement" onClick={ev=>{ev.stopPropagation();removeEquipment(e)}} style={{ color:'#b54745' }}><TrashIcon/></IconButton></div></td></tr>)}{!items.length&&<tr><td colSpan="6"><Empty text="Aucun équipement ne correspond à votre recherche."/></td></tr>}</tbody></table>}</div>{editing&&<EquipmentModal initial={editing} token={token} onClose={()=>setEditing(null)} onSaved={()=>{setEditing(null);load()}}/>}{selected&&<EquipmentDetail item={selected} token={token} onClose={()=>setSelected(null)} onEdit={()=>{setEditing(selected);setSelected(null)}} onDelete={removeEquipment}/>}</div> 
}

function EquipmentModal({initial,token,onClose,onSaved}) {
  const [form,setForm]=useState({...emptyEquipment,...initial}),[error,setError]=useState(''),[busy,setBusy]=useState(false);
  function update(k,v){setForm({...form,[k]:v})} 
  
  async function submit(e){
    e.preventDefault();
    if (!confirm("Voulez-vous vraiment enregistrer ces modifications ?")) return;
    setBusy(true);
    setError('');
    const body={...form,periodiciteValeur:Number(form.periodiciteValeur),valeurOrigine:form.valeurOrigine?Number(form.valeurOrigine):null};
    try{await api(initial.id?`/api/equipements/${initial.id}`:'/api/equipements',{method:initial.id?'PUT':'POST',body,token});onSaved()}catch(e){setError(friendlyError(e))}finally{setBusy(false)}
  } 
  
  const fields=[['codeIdentifiant','Code identifiant *'],['designation','Désignation *'],['marque','Marque *'],['modele','Modèle *'],['numeroSerie','N° de série *'],['codeEmplacement','Emplacement'],['fournisseur','Fournisseur'],['valeurOrigine','Valeur d’origine','number']];
  return <Modal title={initial.id?'Modifier l’équipement':'Nouvel équipement'} onClose={onClose}><form className="form-grid" onSubmit={submit}>{fields.map(([k,label,type])=><label key={k}>{label}<input type={type||'text'} required={label.includes('*')} value={form[k]||''} onChange={e=>update(k,e.target.value)}/></label>)}<label>Date d’acquisition *<input type="date" required value={form.dateAcquisition||''} onChange={e=>update('dateAcquisition',e.target.value)}/></label><label>Mise en service<input type="date" value={form.dateMiseEnService||''} onChange={e=>update('dateMiseEnService',e.target.value)}/></label><label>Périodicité *<input type="number" min="1" required value={form.periodiciteValeur} onChange={e=>update('periodiciteValeur',e.target.value)}/></label><label>Unité<select value={form.periodiciteUnite} onChange={e=>update('periodiciteUnite',e.target.value)}><option value="MOIS">Mois</option><option value="ANNEE">Année</option></select></label><label>Garantie jusqu’au<input type="date" value={form.garantieJusquAu||''} onChange={e=>update('garantieJusquAu',e.target.value)}/></label><label className="full">Observations<textarea value={form.observations||''} onChange={e=>update('observations',e.target.value)} rows="3"/></label>{error&&<div className="error full">{error}</div>}<div className="modal-actions full"><button type="button" className="secondary" onClick={onClose}>Annuler</button><button className="primary" disabled={busy}>{busy?'Enregistrement…':'Enregistrer'}</button></div></form></Modal>
}

function EquipmentDetail({item,token,onClose,onEdit,onDelete}){
  const [history,setHistory]=useState([]),[documents,setDocuments]=useState([]),[tab,setTab]=useState('history');
  const [uploadFile, setUploadFile] = useState(null), [docType, setUploadType] = useState('RAPPORT'), [uploadBusy, setUploadBusy] = useState(false), [uploadError, setUploadError] = useState('');

  const loadDocs = () => {
    api(`/api/documents/equipement/${item.id}`,{token}).then(setDocuments).catch(()=>{})
  };

  useEffect(()=>{
    api(`/api/interventions/equipement/${item.id}`,{token}).then(setHistory).catch(()=>{});
    loadDocs();
  },[item.id,token]);

  async function download(){try{const blob=await api(`/api/equipements/${item.id}/fiche-de-vie`,{token,raw:true});const url=URL.createObjectURL(blob);const a=document.createElement('a');a.href=url;a.download=`fiche-de-vie-${item.id}.pdf`;a.click();URL.revokeObjectURL(url)}catch(e){alert(friendlyError(e))}}

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadFile) return;
    setUploadBusy(true);
    setUploadError('');
    try {
      const fd = new FormData();
      fd.append('equipementId', item.id);
      fd.append('typeDocument', docType);
      fd.append('fichier', uploadFile);
      
      await api('/api/documents', {
        method: 'POST',
        body: fd,
        token,
        formData: true
      });
      loadDocs();
      setUploadFile(null);
      const fileIn = document.getElementById('file-input');
      if (fileIn) fileIn.value = '';
    } catch (e) {
      setUploadError(friendlyError(e));
    } finally {
      setUploadBusy(false);
    }
  }

  const handleDeleteDoc = async (docId) => {
    if (!confirm('Supprimer ce document ?')) return;
    try {
      await api(`/api/documents/${docId}`, { method: 'DELETE', token });
      loadDocs();
    } catch (e) {
      alert(friendlyError(e));
    }
  }

  const qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${encodeURIComponent(`ID: ${item.codeIdentifiant}\nDesignation: ${item.designation}\nModele: ${item.modele}\nSerie: ${item.numeroSerie}`)}`;

  return (
    <Modal title={item.designation} onClose={onClose} wide>
      <div className="detail-head" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', width: '100%' }}>
        <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
          <div style={{ background: '#f5f8fb', padding: '6px', borderRadius: '8px', border: '1px solid #e2e8f0' }} title="Code QR de l'équipement">
            <img src={qrCodeUrl} alt="QR Code" style={{ width: '80px', height: '80px', display: 'block' }} />
          </div>
          <div>
            <code style={{ fontSize: '12px' }}>{item.codeIdentifiant}</code>
            <h2 style={{ fontSize: '20px', marginTop: '4px' }}>{item.designation}</h2>
            <p style={{ margin: '4px 0 0', fontSize: '13px', color: '#728397' }}>{item.marque} · {item.modele} · N° {item.numeroSerie}</p>
          </div>
        </div>
        <Badge state={item.etat}/>
      </div>
      <div className="tabs">
        <button className={tab==='history'?'selected':''} onClick={()=>setTab('history')}>Historique ({history.length})</button>
        <button className={tab==='docs'?'selected':''} onClick={()=>setTab('docs')}>Documents ({documents.length})</button>
      </div>
      {tab==='history'?(
        <div className="timeline">
          {history.map(h=><div className="timeline-item" key={h.id}><span></span><div><b>{h.type?.replaceAll('_',' ')}</b><p>{h.natureIntervention}</p><small>{fmtDate(h.dateIntervention)} · {h.intervenantSociete||h.intervenantNom||'Intervenant non renseigné'}</small></div></div>)}
          {!history.length&&<Empty text="Aucune intervention enregistrée."/>}
        </div>
      ):(
        <div>
          <div style={{ maxHeight: '200px', overflowY: 'auto', marginBottom: '20px' }}>
            {documents.map(d=><div className="document" key={d.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: '1px solid #e9eff2' }}><div>▧ <b>{d.nomFichier}</b><small>{d.typeDocument?.replaceAll('_', ' ')} · {d.uploadedAt?.slice(0,10)}</small></div><button type="button" onClick={()=>handleDeleteDoc(d.id)} style={{ background:'none', color:'#b54745', border:'none', cursor:'pointer', fontSize:'16px', fontWeight:'bold' }}>×</button></div>)}
            {!documents.length&&<Empty text="Aucun document associé."/>}
          </div>
          
          <form onSubmit={handleUpload} style={{ borderTop: '1px solid #edf1f3', paddingTop: '15px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <h4 style={{ fontSize: '13px', fontWeight: 'bold', color: '#1a988b' }}>Ajouter un document</h4>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
              <label style={{ fontSize: '11px', fontWeight: 'bold' }}>Type de document
                <select value={docType} onChange={e=>setUploadType(e.target.value)} style={{ display: 'block', width: '100%', marginTop: '5px', padding: '8px', borderRadius: '5px', border: '1px solid #d8e2e9' }}>
                  <option value="RAPPORT">Rapport</option>
                  <option value="CERTIFICAT_ETALONNAGE">Certificat d'étalonnage</option>
                  <option value="CONTRAT_MAINTENANCE">Contrat de maintenance</option>
                  <option value="MANUEL">Manuel</option>
                  <option value="GARANTIE">Garantie</option>
                  <option value="AUTRE">Autre</option>
                </select>
              </label>
              <label style={{ fontSize: '11px', fontWeight: 'bold' }}>Fichier
                <input id="file-input" type="file" required onChange={e=>setUploadFile(e.target.files[0])} style={{ display: 'block', width: '100%', marginTop: '5px', padding: '5px', borderRadius: '5px', border: '1px solid #d8e2e9' }} />
              </label>
            </div>
            {uploadError && <div className="error" style={{ fontSize: '12px', padding: '8px' }}>{uploadError}</div>}
            <button className="primary small" disabled={uploadBusy || !uploadFile} style={{ alignSelf: 'flex-start' }}>{uploadBusy ? 'Téléversement…' : 'Uploader'}</button>
          </form>
        </div>
      )}
      <div className="modal-actions">
        <button className="secondary" onClick={download}>⇩ Fiche de vie</button>
        <button className="secondary" onClick={()=>onDelete(item)} style={{ color:'#b54745', borderColor:'#fce9e9', display:'flex', alignItems:'center', gap:'6px' }}><TrashIcon/> Supprimer</button>
        <button className="primary" onClick={onEdit}>Modifier</button>
      </div>
    </Modal>
  )
}

function ResourceList({token,kind}) {
  const config={
    laboratoires:{
      title:'Référentiel des laboratoires',
      eyebrow:'RÉFÉRENTIEL UNIQUE',
      desc:'Centralisez ici les laboratoires, prestataires et contacts de référence afin d’éviter la double saisie.',
      endpoint:'/api/labos-etalonnage',
      fields:[['nom','Nom du laboratoire / prestataire *'],['contactNom','Nom du contact'],['contactPrenom','Prénom du contact'],['contactEmail','Email','email'],['contactTelephone','Téléphone']]
    }
  }[kind];
  
  const [rows,setRows]=useState([]),[form,setForm]=useState(null),[error,setError]=useState('');
  const load=()=>api(config.endpoint,{token}).then(setRows).catch(e=>setError(friendlyError(e)));
  useEffect(load,[token]);
  
  async function save(e){
    e.preventDefault();
    if (!confirm("Voulez-vous vraiment enregistrer ces informations ?")) return;
    try{await api(config.endpoint+(form.id?`/${form.id}`:''),{method:form.id?'PUT':'POST',body:form,token});setForm(null);load()}catch(e){setError(friendlyError(e))}
  } 
  
  async function remove(id){
    if(!confirm('Supprimer cet élément ?'))return;
    try{await api(`${config.endpoint}/${id}`,{method:'DELETE',token});load()}catch(e){setError(friendlyError(e))}
  }
  
  return <div className="content"><div className="page-intro"><div><p className="eyebrow">{config.eyebrow}</p><h1>{config.title}</h1><p className="muted">{config.desc}</p></div><button className="primary" onClick={()=>setForm({})}>+ Ajouter</button></div>{error&&<Notice text={error}/>}<div className="cards-grid">{rows.map(r => {
    const isLab = kind === 'laboratoires';
    const name = isLab ? r.nom : `${r.prenom || ''} ${r.nom || ''}`;
    const email = isLab ? r.contactEmail : r.email;
    const phone = isLab ? r.contactTelephone : r.telephone;
    const subtitle = isLab ? ([r.contactPrenom, r.contactNom].filter(Boolean).join(' ') || 'Contact non renseigné') : 'Agent du laboratoire';

    return (
      <article className="contact-card" key={r.id}>
        <div className="contact-avatar">{(isLab ? r.nom : r.nom || '?').slice(0,1).toUpperCase()}</div>
        <div className="contact-main">
          <h3>{name}</h3>
          <p>{subtitle}</p>
          <small style={{ marginTop: '5px' }}>
            {email ? (
              <a href={`mailto:${email}`} style={{ color: '#118378', textDecoration: 'none', fontWeight: '600' }} title="Cliquer pour envoyer un mail">
                ✉ {email}
              </a>
            ) : '✉ Email non renseigné'}
          </small>
          <small>☎ {phone || 'Téléphone non renseigné'}</small>
        </div>
        <IconButton title="Supprimer" onClick={()=>remove(r.id)}>×</IconButton>
      </article>
    )
  })}{!rows.length&&<div className="panel"><Empty text="Aucun élément enregistré."/></div>}</div>{form&&<Modal title="Ajouter un laboratoire / prestataire" onClose={()=>setForm(null)}><form className="form-grid" onSubmit={save}>{config.fields.map(([key,label,type])=><label key={key}>{label}<input required={label.includes('*')} type={type||'text'} value={form[key]||''} onChange={e=>setForm({...form,[key]:e.target.value})}/></label>)}<div className="modal-actions full"><button type="button" className="secondary" onClick={()=>setForm(null)}>Annuler</button><button className="primary">Enregistrer</button></div></form></Modal>}</div>
}

function getAlertRisk(jours) {
  if (jours <= 5) return { color: '#c65355', bg: '#fce9e9', label: 'Risque Élevé' }
  if (jours <= 15) return { color: '#ce8b18', bg: '#fff1d7', label: 'Risque Moyen' }
  return { color: '#16835d', bg: '#e0f5ea', label: 'Risque Faible' }
}

function Alerts({ token }) {
  const [alerts, setAlerts] = useState([])
  const [days, setDays] = useState(30)
  const [includeResolved, setIncludeResolved] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState('')

  const load = async () => {
    setLoading(true)
    setError('')
    try {
      const list = await api(`/api/alertes?jours=${days}&inclureResolues=${includeResolved}`, { token })
      setAlerts(Array.isArray(list) ? list : [])
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [token, days, includeResolved])

  async function resolve(alert) {
    const observation = prompt('Observation / action réalisée', alert.observationResolution || 'Action planifiée / problème traité')
    if (observation === null) return
    setBusyId(alert.id)
    try {
      await api(`/api/alertes/${encodeURIComponent(alert.id)}/resoudre`, { method: 'PATCH', token, body: { observation } })
      await load()
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusyId('')
    }
  }

  async function reopen(alert) {
    if (!confirm('Rouvrir cette notification ?')) return
    setBusyId(alert.id)
    try {
      await api(`/api/alertes/${encodeURIComponent(alert.id)}/resolution`, { method: 'DELETE', token })
      await load()
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusyId('')
    }
  }

  return (
    <div className="content">
      <div className="page-intro lab-intro">
        <div>
          <p className="eyebrow">SUIVI DES ÉCHÉANCES</p>
          <h1>Alertes</h1>
          <p className="muted">Anticipez les opérations, ajoutez une observation et marquez la notification comme résolue après traitement.</p>
        </div>
        <div className="alert-filters">
          <select value={days} onChange={e => setDays(e.target.value)}>
            <option value="15">15 prochains jours</option>
            <option value="30">30 prochains jours</option>
            <option value="60">60 prochains jours</option>
            <option value="120">120 prochains jours</option>
          </select>
          <label className="switch-line">
            <input type="checkbox" checked={includeResolved} onChange={e => setIncludeResolved(e.target.checked)} />
            Afficher les résolues
          </label>
        </div>
      </div>
      {error && <Notice text={error} />}
      <div className="panel alert-list improved-alerts">
        {loading ? <Loader /> : alerts.map((a, i) => {
          const risk = a.resolue ? { color: '#16835d', bg: '#e0f5ea', label: 'Résolue' } : getAlertRisk(a.joursRestants)
          return (
            <div className={`alert-row ${a.resolue ? 'is-resolved' : ''}`} key={a.id || i}>
              <span className="warning-icon" style={{ backgroundColor: risk.bg, color: risk.color }}>{a.resolue ? '✓' : '!'}</span>
              <div className="alert-main">
                <p className="eyebrow" style={{ color: risk.color }}>{a.type} · <span style={{ fontWeight: 'normal', textTransform: 'none' }}>{risk.label}</span></p>
                <h3>{a.equipementDesignation} <code>{a.equipementCodeIdentifiant}</code></h3>
                <p className="muted">{a.details}</p>
                {a.observationResolution && <p className="resolution-note"><b>Observation :</b> {a.observationResolution}</p>}
                {a.resolvedBy && <small className="resolved-meta">Résolue par {a.resolvedBy}{a.resolvedAt ? ` · ${new Intl.DateTimeFormat('fr-FR').format(new Date(a.resolvedAt))}` : ''}</small>}
              </div>
              <div className="alert-date">
                <b style={{ color: risk.color }}>{a.joursRestants}</b>
                <small>jours restants</small>
                <span>{fmtDate(a.dateEcheance)}</span>
              </div>
              <div className="alert-actions">
                {a.resolue ? (
                  <button className="secondary small" disabled={busyId === a.id} onClick={() => reopen(a)}>Rouvrir</button>
                ) : (
                  <button className="primary small" disabled={busyId === a.id || !a.id} onClick={() => resolve(a)}>Marquer résolu</button>
                )}
              </div>
            </div>
          )
        })}
        {!loading && !alerts.length && <Empty text="Aucune alerte pour cette période." />}
      </div>
    </div>
  )
}

function DemandesPrestation({ token }) {
  const [demandes, setDemandes] = useState([])
  const [etalonnages, setEtalonnages] = useState([])
  const [labs, setLabs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [editingItem, setEditingItem] = useState(null)
  const [form, setForm] = useState({
    etalonnageId: '',
    laboEtalonnageId: '',
    dateDemande: new Date().toISOString().slice(0, 10),
    etatALaLivraison: '',
    lieu: 'LABORATOIRE',
    comparaisonInterLabo: false,
    declarationConformite: false,
    regleDecision: 'BINAIRE_SIMPLE',
    pointsAEtalonner: '',
    niveauIncertitude: 'SUPERIEUR_1E_04'
  })
  const [submitting, setSubBusy] = useState(false)

  const loadData = async () => {
    setLoading(true)
    setError('')
    try {
      const [demList, etalList, labList] = await Promise.all([
        api('/api/demandes-prestation', { token }),
        api('/api/etalonnages', { token }),
        api('/api/labos-etalonnage', { token })
      ])
      setDemandes(demList)
      setEtalonnages(etalList)
      setLabs(labList)
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [token])

  const handleDownloadPdf = async (id, ev) => {
    ev.stopPropagation()
    try {
      const blob = await api(`/api/demandes-prestation/${id}/pdf`, { token, raw: true })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `demande-prestation-${id}.pdf`
      a.click()
      URL.revokeObjectURL(url)
    } catch (e) {
      alert(friendlyError(e))
    }
  }

  const handleEdit = (d, ev) => {
    ev.stopPropagation()
    setForm({
      etalonnageId: d.etalonnageId?.toString() || '',
      laboEtalonnageId: d.laboEtalonnageNom ? (labs.find(l => l.nom === d.laboEtalonnageNom)?.id?.toString() || '') : '',
      dateDemande: d.dateDemande || '',
      etatALaLivraison: d.etatALaLivraison || '',
      lieu: d.lieu || 'LABORATOIRE',
      comparaisonInterLabo: !!d.comparaisonInterLabo,
      declarationConformite: !!d.declarationConformite,
      regleDecision: d.regleDecision || 'BINAIRE_SIMPLE',
      pointsAEtalonner: d.pointsAEtalonner || '',
      niveauIncertitude: d.niveauIncertitude || 'SUPERIEUR_1E_04'
    })
    setEditingItem(d.id)
    setShowCreate(true)
  }

  const handleCloseModal = () => {
    setShowCreate(false)
    setEditingItem(null)
    setForm({
      etalonnageId: '',
      laboEtalonnageId: '',
      dateDemande: new Date().toISOString().slice(0, 10),
      etatALaLivraison: '',
      lieu: 'LABORATOIRE',
      comparaisonInterLabo: false,
      declarationConformite: false,
      regleDecision: 'BINAIRE_SIMPLE',
      pointsAEtalonner: '',
      niveauIncertitude: 'SUPERIEUR_1E_04'
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.etalonnageId) {
      alert('Veuillez sélectionner un étalonnage.')
      return
    }
    if (!confirm("Voulez-vous vraiment enregistrer ces informations ?")) return;
    setSubBusy(true)
    setError('')
    try {
      const body = {
        ...form,
        etalonnageId: Number(form.etalonnageId),
        laboEtalonnageId: form.laboEtalonnageId ? Number(form.laboEtalonnageId) : null
      }
      const endpoint = editingItem ? `/api/demandes-prestation/${editingItem}` : '/api/demandes-prestation';
      const method = editingItem ? 'PUT' : 'POST';

      await api(endpoint, {
        method,
        body,
        token
      })
      handleCloseModal()
      loadData()
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setSubBusy(false)
    }
  }

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">PRESTATIONS</p>
          <h1>Demandes de prestation</h1>
          <p className="muted">Gérez et générez les fiches de demande de prestation de service pour vos étalonnages.</p>
        </div>
        <button className="primary" onClick={() => setShowCreate(true)}>+ Nouvelle Demande</button>
      </div>

      {error && <Notice text={error} />}

      <div className="panel table-wrap">
        {loading ? (
          <Loader />
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>ÉQUIPEMENT</th>
                <th>LABORATOIRE</th>
                <th>DATE DEMANDE</th>
                <th>LIEU</th>
                <th>RÈGLE DÉCISION</th>
                <th>INCERTITUDE</th>
                <th>ACTIONS</th>
              </tr>
            </thead>
            <tbody>
              {demandes.map(d => (
                <tr key={d.id}>
                  <td><code>#{d.id}</code></td>
                  <td><b>ID {d.equipementId}</b> <small>Étalonnage #{d.etalonnageId}</small></td>
                  <td>{d.laboEtalonnageNom || '—'}</td>
                  <td>{fmtDate(d.dateDemande)}</td>
                  <td><span className="role">{d.lieu}</span></td>
                  <td><code>{d.regleDecision}</code></td>
                  <td><code>{d.niveauIncertitude}</code></td>
                  <td>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button className="secondary small" onClick={(ev) => handleDownloadPdf(d.id, ev)}>
                        📄 PDF
                      </button>
                      <button className="secondary small" onClick={(ev) => handleEdit(d, ev)}>
                        Modifier
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {!demandes.length && (
                <tr>
                  <td colSpan="8"><Empty text="Aucune demande de prestation enregistrée." /></td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>

      {showCreate && (
        <Modal title={editingItem ? "Modifier la demande de prestation" : "Nouvelle demande de prestation"} onClose={handleCloseModal}>
          <form onSubmit={handleSubmit} className="form-grid">
            <label className="full">Sélectionner l'Étalonnage *
              <select value={form.etalonnageId} onChange={e => setForm({ ...form, etalonnageId: e.target.value })} required style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }}>
                <option value="">-- Choisir un étalonnage --</option>
                {etalonnages.map(et => (
                  <option key={et.id} value={et.id}>
                    {et.equipementDesignation} ({et.equipementCodeIdentifiant}) - Étalonnage #{et.id}
                  </option>
                ))}
              </select>
            </label>

            <label className="full">Laboratoire d'étalonnage *
              <select value={form.laboEtalonnageId} onChange={e => setForm({ ...form, laboEtalonnageId: e.target.value })} required style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }}>
                <option value="">-- Choisir un laboratoire --</option>
                {labs.map(l => (
                  <option key={l.id} value={l.id}>{l.nom}</option>
                ))}
              </select>
            </label>

            <label>Date de demande *
              <input type="date" value={form.dateDemande} onChange={e => setForm({ ...form, dateDemande: e.target.value })} required />
            </label>

            <label>Lieu *
              <select value={form.lieu} onChange={e => setForm({ ...form, lieu: e.target.value })} required style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }}>
                <option value="LABORATOIRE">Laboratoire</option>
                <option value="SITE">Site</option>
              </select>
            </label>

            <label>Règle de décision *
              <select value={form.regleDecision} onChange={e => setForm({ ...form, regleDecision: e.target.value })} required style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }}>
                <option value="BINAIRE_SIMPLE">Binaire simple</option>
                <option value="BINAIRE_BANDE_GARDE">Binaire bande de garde</option>
                <option value="NON_BINAIRE_BANDE_GARDE">Non binaire bande de garde</option>
              </select>
            </label>

            <label>Niveau d'incertitude *
              <select value={form.niveauIncertitude} onChange={e => setForm({ ...form, niveauIncertitude: e.target.value })} required style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }}>
                <option value="SUPERIEUR_1E_04">Supérieur à 10⁻⁴</option>
                <option value="INFERIEUR_1E_04">Inférieur à 10⁻⁴</option>
              </select>
            </label>

            <label className="full">État à la livraison
              <input value={form.etatALaLivraison} onChange={e => setForm({ ...form, etatALaLivraison: e.target.value })} placeholder="ex. Parfait état, livré avec câbles..." />
            </label>

            <label className="full">Points à étalonner
              <textarea value={form.pointsAEtalonner} onChange={e => setForm({ ...form, pointsAEtalonner: e.target.value })} placeholder="ex. 10V, 50V, 100V..." rows="2" style={{ display: 'block', width: '100%', marginTop: '7px', padding: '10px', borderRadius: '6px', border: '1px solid #d8e2e9' }} />
            </label>

            <div className="form-row full" style={{ display: 'flex', gap: '20px', marginTop: '10px' }}>
              <label className="checkbox">
                <input type="checkbox" checked={form.comparaisonInterLabo} onChange={e => setForm({ ...form, comparaisonInterLabo: e.target.checked })} />
                Comparaison inter-laboratoire
              </label>
              <label className="checkbox">
                <input type="checkbox" checked={form.declarationConformite} onChange={e => setForm({ ...form, declarationConformite: e.target.checked })} />
                Déclaration de conformité
              </label>
            </div>

            <div className="modal-actions full" style={{ marginTop: '20px' }}>
              <button type="button" className="secondary" onClick={handleCloseModal}>Annuler</button>
              <button className="primary" disabled={submitting}>{submitting ? 'Enregistrement…' : (editingItem ? 'Enregistrer les modifications' : 'Créer la demande')}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}

function Users({token, currentUsername, onSelfDelete, onSelfStatusChange}){
  const[rows,setRows]=useState([]),[error,setError]=useState('');
  const load=()=>api('/api/users',{token}).then(setRows).catch(e=>setError(friendlyError(e)));
  useEffect(load,[token]);
  
  async function toggle(u){
    try{
      await api(`/api/users/${u.id}/${u.enabled?'desactiver':'activer'}`,{method:'PATCH',token});
      load();
      if (u.username === currentUsername) onSelfStatusChange?.();
    }catch(e){setError(friendlyError(e))}
  }
  
  async function removeUser(u) {
    if (!confirm("Voulez-vous vraiment supprimer définitivement cet utilisateur ?")) return;
    try {
      await api(`/api/users/${u.id}`, { method: 'DELETE', token });
      if (u.username === currentUsername) {
        // L'utilisateur vient de supprimer son propre compte : on le déconnecte immédiatement.
        onSelfDelete();
        return;
      }
      load();
    } catch (e) {
      setError(friendlyError(e));
    }
  }

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">ADMINISTRATION</p>
          <h1>Utilisateurs</h1>
          <p className="muted">Gérez les accès, les suppressions et les rôles de votre organisation.</p>
        </div>
      </div>
      {error&&<Notice text={error}/>}
      <div className="panel table-wrap">
        <table>
          <thead>
            <tr>
              <th>UTILISATEUR</th>
              <th>EMAIL</th>
              <th>RÔLE</th>
              <th>STATUT</th>
              <th>ACTIONS</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(u=>(
              <tr key={u.id}>
                <td><b>{u.prenom} {u.nom}</b><small>@{u.username}</small></td>
                <td>{u.email}</td>
                <td><span className="role">{u.role?.replace('_',' ')}</span></td>
                <td><span className={u.enabled?'online':'offline'}>{u.enabled?'● Actif':'● Désactivé'}</span></td>
                <td>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button className="secondary small" onClick={()=>toggle(u)}>{u.enabled?'Désactiver':'Activer'}</button>
                    <button className="secondary small" onClick={()=>removeUser(u)} style={{ background: '#fff0ef', color: '#b54745', borderColor: '#fce9e9' }}>Supprimer</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

function Placeholder({title}){return <div className="content"><div className="page-intro"><div><p className="eyebrow">GESTION</p><h1>{title}</h1><p className="muted">Cette section est prête à être reliée aux prochaines opérations métier.</p></div></div><div className="panel"><Empty text="Aucune donnée à afficher pour le moment."/></div></div>}
function Modal({title,onClose,children,wide}) {return <div className="modal-backdrop" onMouseDown={onClose}><section className={`modal ${wide?'wide-modal':''}`} onMouseDown={e=>e.stopPropagation()}><div className="modal-title"><h2>{title}</h2><IconButton title="Fermer" onClick={onClose}>×</IconButton></div>{children}</section></div>}

function App(){
  const [auth, setAuth] = useState(() => {
    try {
      const item = localStorage.getItem('cert-auth');
      if (!item) return null;
      const parsed = JSON.parse(item);
      return (parsed && typeof parsed === 'object' && parsed.token) ? parsed : null;
    } catch {
      return null;
    }
  })
  const [page, setPage] = useState('dashboard')
  const [authScreen, setAuthScreen] = useState('login')
  const [showProfile, setShowProfile] = useState(false)
  const [alertCount, setAlertCount] = useState(0)
  const [authNotice, setAuthNotice] = useState(null)
  const [profileVersion, setProfileVersion] = useState(0)
  const [photoUrl, setPhotoUrl] = useState(null)
  const [photoVersion, setPhotoVersion] = useState(0)

  // Si on arrive depuis le lien de verification d'email (redirection backend
  // /api/auth/verify -> frontend?token=...), on se connecte automatiquement
  // sans repasser par le formulaire de connexion.
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const token = params.get('token')
    const username = params.get('username')
    const role = params.get('role')
    const verificationError = params.get('verificationError')

    if (token && username && role) {
      login({ token, username, role })
      setAuthNotice({ type: 'success', text: 'Compte activé avec succès. Bienvenue !' })
      window.history.replaceState({}, '', window.location.pathname)
    } else if (verificationError) {
      setAuthNotice({ type: 'error', text: verificationError })
      window.history.replaceState({}, '', window.location.pathname)
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  useEffect(() => {
    if (auth?.token) {
      api('/api/alertes?jours=30', { token: auth.token })
        .then(alerts => setAlertCount(Array.isArray(alerts) ? alerts.length : 0))
        .catch(() => {})
    }
  }, [auth, page])

  useEffect(() => {
    if (auth?.token) {
      api('/api/auth/me', { token: auth.token })
        .then(p => setPhotoUrl(p.photoUrl || null))
        .catch(() => {})
    } else {
      setPhotoUrl(null)
    }
  }, [auth])

  function login(data){localStorage.setItem('cert-auth',JSON.stringify(data));setAuth(data)}
  function logout(){localStorage.removeItem('cert-auth');setAuth(null)}
  function handlePhotoChanged(newPhotoUrl){setPhotoUrl(newPhotoUrl);setPhotoVersion(v=>v+1)}

  if(!auth?.token){
    if(authScreen==='register'){return <Register onGoToLogin={()=>setAuthScreen('login')}/>}
    if(authScreen==='forgot-password'){return <ForgotPassword onGoToLogin={()=>setAuthScreen('login')}/>}
    return <Login onLogin={login} onGoToRegister={()=>setAuthScreen('register')} onGoToForgotPassword={()=>setAuthScreen('forgot-password')} notice={authNotice}/>
  }

  let view={
    dashboard:<Dashboard token={auth.token} username={auth.username} setPage={setPage}/>,
    equipements:<Equipments token={auth.token}/>,
    alertes:<Alerts token={auth.token}/>,
    laboratoires:<ResourceList token={auth.token} kind="laboratoires"/>,
    utilisateurs:<Users token={auth.token} currentUsername={auth.username} onSelfDelete={logout} onSelfStatusChange={()=>setProfileVersion(v=>v+1)}/>,
    demandes:<DemandesPrestation token={auth.token}/>
  }[page]||<Dashboard token={auth.token} username={auth.username} setPage={setPage}/>;

  return (
    <>
      {showProfile && <ProfileModal token={auth.token} onClose={()=>setShowProfile(false)} onLogout={logout} refreshKey={profileVersion} onPhotoChanged={handlePhotoChanged}/>}
      <Shell user={auth} page={page} setPage={setPage} onLogout={logout} onAvatarClick={()=>setShowProfile(true)} alertCount={alertCount} avatarSrc={photoUrl ? `${baseUrl}${photoUrl}?v=${photoVersion}` : null}>
        {authNotice?.type === 'success' && (
          <div className="success-banner" onClick={() => setAuthNotice(null)}>
            {authNotice.text} <small>(cliquer pour masquer)</small>
          </div>
        )}
        {view}
      </Shell>
    </>
  )
}

createRoot(document.getElementById('root')).render(<App />)
