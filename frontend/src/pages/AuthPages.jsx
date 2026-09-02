import React, { useState, useEffect } from 'react'
import { EyeIcon, EyeSlashIcon } from '../components/Icons'
import { api } from '../api'

const stateLabels = {
  NOUVEAU: 'Nouveau', DISPONIBLE: 'Disponible',
  EN_UTILISATION: 'En utilisation', EN_ETALONNAGE: 'Étalonnage',
  EN_MAINTENANCE: 'Maintenance', EN_PANNE: 'En panne',
  HORS_SERVICE: 'Hors service', REFORME: 'Réformé'
}
const states = Object.keys(stateLabels)

const emptyEquipment = {
  codeIdentifiant: '', codeEmplacement: '', designation: '',
  marque: '', modele: '', numeroSerie: '', fournisseur: '',
  dateAcquisition: '', dateMiseEnService: '', valeurOrigine: '',
  periodiciteValeur: '12', periodiciteUnite: 'MOIS',
  garantieJusquAu: '', observations: ''
}

function friendlyError(e) {
  return e.message || 'Une erreur est survenue.'
}

function fmtDate(d) {
  return d ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`)) : '—'
}

function Badge({ state }) {
  return <span className={`badge ${state?.toLowerCase().replaceAll('_', '-')}`}>
    {stateLabels[state] || state}
  </span>
}

function IconButton({ children, title, ...props }) {
  return <button className="icon-button" title={title} {...props}>{children}</button>
}

function getEtatStyle(etat) {
  const lower = etat?.toLowerCase().replaceAll('_', '-')
  if (lower === 'disponible') return { color: '#16835d', bg: '#e0f5ea' }
  if (lower === 'en-utilisation') return { color: '#3172ba', bg: '#e7f0fc' }
  if (lower === 'en-etalonnage' || lower === 'en-maintenance')
    return { color: '#ba7914', bg: '#fff2d7' }
  if (lower === 'en-panne' || lower === 'hors-service' || lower === 'reforme')
    return { color: '#c65355', bg: '#fce9e9' }
  return { color: '#7160bb', bg: '#f0ecff' }
}

export function Login({ onLogin, onGoToRegister, onGoToForgotPassword }) {
  const [values, setValues] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
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
    e.preventDefault()
    setBusy(true)
    setError('')
    try {
      const user = await api('/api/auth/login', {
        method: 'POST',
        body: values
      })
      if (rememberMe) {
        localStorage.setItem('remembered-username', values.username)
      } else {
        localStorage.removeItem('remembered-username')
      }
      onLogin(user)
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-aside">
        <div className="brand" style={{
          display: 'flex', flexDirection: 'column',
          alignItems: 'center', gap: '15px',
          width: '100%', marginBottom: '30px'
        }}>
          <img src="/logo_cert.png" alt="Logo CERT"
            style={{ height: '144px', width: 'auto', objectFit: 'contain' }} />
          <span style={{
            fontSize: '26px', letterSpacing: '1px',
            textAlign: 'center'
          }}>
            CERT <b>CALIBRATION HUB</b>
          </span>
        </div>
        <div className="login-hero">
          <p className="eyebrow">GESTION MÉTROLOGIQUE</p>
          <h1>La précision,<br /><em>en toute confiance.</em></h1>
          <p>
            Pilotez vos équipements, étalonnages et échéances
            depuis un espace unique et sécurisé.
          </p>
          <div className="hero-stats">
            <span><b>24/7</b> suivi centralisé</span>
            <span><b>100%</b> traçabilité</span>
          </div>
        </div>
        <small>© 2026 CERT Calibration Hub</small>
      </section>
      <section className="login-card">
        <div>
          <p className="eyebrow">BON RETOUR</p>
          <h2>Connectez-vous à votre espace</h2>
          <p className="muted">Saisissez vos identifiants pour continuer.</p>
        </div>
        <form onSubmit={submit}>
          <label>
            Nom d'utilisateur
            <input
              autoFocus
              value={values.username}
              onChange={e => setValues({ ...values, username: e.target.value })}
              placeholder="ex. jdupont"
              required
            />
          </label>
          <label>
            Mot de passe
            <div style={{ position: 'relative' }}>
              <input
                type={showPassword ? 'text' : 'password'}
                value={values.password}
                onChange={e => setValues({ ...values, password: e.target.value })}
                placeholder="••••••••"
                required
                style={{ paddingRight: '45px' }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute', right: '10px',
                  top: '50%', transform: 'translateY(-50%)',
                  background: 'none', border: 'none',
                  cursor: 'pointer', display: 'flex',
                  alignItems: 'center', color: '#6d8194',
                  padding: '5px'
                }}
              >
                {showPassword ? <EyeSlashIcon /> : <EyeIcon />}
              </button>
            </div>
          </label>
          <div className="form-row">
            <label className="checkbox">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={e => setRememberMe(e.target.checked)}
              />
              Se souvenir de moi
            </label>
            <button
              type="button"
              className="link"
              onClick={onGoToForgotPassword}
            >
              Mot de passe oublié ?
            </button>
          </div>
          {error && <div className="error">{error}</div>}
          <button className="primary wide" disabled={busy}>
            {busy ? 'Connexion…' : 'Se connecter'} <span>→</span>
          </button>
        </form>
        <p className="muted" style={{
          textAlign: 'center', fontSize: '13px', marginTop: '20px'
        }}>
          Nouveau sur la plateforme ?
          <button type="button" className="link" onClick={onGoToRegister}>
            Créer un compte
          </button>
        </p>
        <p className="login-help">
          Accès réservé aux utilisateurs autorisés.
          <br />
          Contactez votre administrateur si nécessaire.
        </p>
      </section>
    </main>
  )
}

export function Register({ onGoToLogin }) {
  const [values, setValues] = useState({
    nom: '', prenom: '', email: '',
    username: '', password: '',
    role: 'PERSONNEL_AUTORISE'
  })
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
      setSuccess(
        responseText ||
        "Compte créé avec succès. Un email d'activation a été envoyé."
      )
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-aside">
        <div className="brand" style={{
          display: 'flex', flexDirection: 'column',
          alignItems: 'center', gap: '15px',
          width: '100%', marginBottom: '30px'
        }}>
          <img src="/logo_cert.png" alt="Logo CERT"
            style={{ height: '144px', width: 'auto', objectFit: 'contain' }} />
          <span style={{
            fontSize: '26px', letterSpacing: '1px',
            textAlign: 'center'
          }}>
            CERT <b>CALIBRATION HUB</b>
          </span>
        </div>
        <div className="login-hero">
          <p className="eyebrow">GESTION MÉTROLOGIQUE</p>
          <h1>Rejoignez<br /><em>notre plateforme.</em></h1>
          <p>
            Créez votre compte pour suivre vos équipements,
            étalonnages et échéances.
          </p>
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
            <div className="notice" style={{
              background: '#e0f5ea', color: '#16835d',
              marginBottom: '20px'
            }}>
              {success}
            </div>
            <button className="primary wide" onClick={onGoToLogin}>
              Aller à la connexion
            </button>
          </div>
        ) : (
          <form onSubmit={submit}>
            <div className="form-row" style={{
              display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px'
            }}>
              <label>
                Prénom
                <input
                  value={values.prenom}
                  onChange={e => setValues({ ...values, prenom: e.target.value })}
                  placeholder="Jean"
                  required
                />
              </label>
              <label>
                Nom
                <input
                  value={values.nom}
                  onChange={e => setValues({ ...values, nom: e.target.value })}
                  placeholder="Dupont"
                  required
                />
              </label>
            </div>
            <label>
              Email
              <input
                type="email"
                value={values.email}
                onChange={e => setValues({ ...values, email: e.target.value })}
                placeholder="jean.dupont@exemple.com"
                required
              />
            </label>
            <label>
              Nom d'utilisateur
              <input
                value={values.username}
                onChange={e => setValues({ ...values, username: e.target.value })}
                placeholder="jdupont"
                required
              />
            </label>
            <label>
              Mot de passe
              <div style={{ position: 'relative' }}>
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={values.password}
                  onChange={e => setValues({ ...values, password: e.target.value })}
                  placeholder="••••••••"
                  required
                  minLength={8}
                  style={{ paddingRight: '45px' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute', right: '10px',
                    top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none',
                    cursor: 'pointer', display: 'flex',
                    alignItems: 'center', color: '#6d8194',
                    padding: '5px'
                  }}
                >
                  {showPassword ? <EyeSlashIcon /> : <EyeIcon />}
                </button>
              </div>
            </label>
            <label>
              Rôle
              <select
                value={values.role}
                onChange={e => setValues({ ...values, role: e.target.value })}
                style={{
                  display: 'block', width: '100%',
                  marginTop: '7px', border: '1px solid #d8e2e9',
                  borderRadius: '6px', padding: '10px',
                  outlineColor: '#1a988b', color: '#263c51',
                  font: 'inherit'
                }}
                required
              >
                <option value="PERSONNEL_AUTORISE">Personnel Autorisé</option>
                <option value="CHEF_LABORATOIRE">Chef de Laboratoire</option>
                <option value="ADMINISTRATEUR">Administrateur</option>
              </select>
            </label>

            {error && <div className="error">{error}</div>}

            <button className="primary wide" disabled={busy}>
              {busy ? 'Création du compte…' : "S'inscrire"} <span>→</span>
            </button>

            <p className="muted" style={{
              textAlign: 'center', fontSize: '13px', marginTop: '10px'
            }}>
              Déjà un compte ?
              <button
                type="button"
                className="link"
                onClick={onGoToLogin}
              >
                Se connecter
              </button>
            </p>
          </form>
        )}
      </section>
    </main>
  )
}

export function ForgotPassword({ onGoToLogin }) {
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
      setSuccess(
        msg ||
        'Un email de réinitialisation a été envoyé si le compte existe.'
      )
    } catch (e) {
      setError(friendlyError(e))
    } finally {
      setBusy(false)
    }
  }

  return (
    <main className="login-page">
      <section className="login-aside">
        <div className="brand" style={{
          display: 'flex', flexDirection: 'column',
          alignItems: 'center', gap: '15px',
          width: '100%', marginBottom: '30px'
        }}>
          <img src="/logo_cert.png" alt="Logo CERT"
            style={{ height: '144px', width: 'auto', objectFit: 'contain' }} />
          <span style={{
            fontSize: '26px', letterSpacing: '1px',
            textAlign: 'center'
          }}>
            CERT <b>CALIBRATION HUB</b>
          </span>
        </div>
        <div className="login-hero">
          <p className="eyebrow">RÉINITIALISATION</p>
          <h1>Pas de panique,<br /><em>on s'en occupe.</em></h1>
          <p>
            Saisissez votre adresse email pour recevoir un lien sécurisé
            de réinitialisation de mot de passe.
          </p>
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
            <div className="notice" style={{
              background: '#e0f5ea', color: '#16835d',
              marginBottom: '20px'
            }}>
              {success}
            </div>
            <button className="primary wide" onClick={onGoToLogin}>
              Retour à la connexion
            </button>
          </div>
        ) : (
          <form onSubmit={submit}>
            <label>
              Adresse email
              <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="ex. jdupont@exemple.com"
                required
                autoFocus
              />
            </label>

            {error && <div className="error">{error}</div>}

            <button className="primary wide" disabled={busy}>
              {busy ? 'Envoi en cours…' : 'Envoyer le lien'} <span>→</span>
            </button>

            <p className="muted" style={{
              textAlign: 'center', fontSize: '13px', marginTop: '10px'
            }}>
              <button
                type="button"
                className="link"
                onClick={onGoToLogin}
              >
                Retour à la connexion
              </button>
            </p>
          </form>
        )}
      </section>
    </main>
  )
}

export {
  stateLabels, states, emptyEquipment, friendlyError, fmtDate,
  Badge, IconButton, getEtatStyle
}
