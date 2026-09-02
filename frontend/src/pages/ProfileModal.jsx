import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { Modal } from '../components/Modal'
import { Loader, Notice } from '../components/common'

export function ProfileModal({ token, onClose, onLogout }) {
  const [profile, setProfile] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api('/api/auth/me', { token })
      .then(setProfile)
      .catch(e => setError(e.message || 'Une erreur est survenue.'))
  }, [token])

  return (
    <Modal title="Mon Compte" onClose={onClose}>
      {error ? (
        <Notice text={error} />
      ) : !profile ? (
        <Loader />
      ) : (
        <div style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '20px'
        }}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '16px',
            borderBottom: '1px solid #edf1f3',
            paddingBottom: '16px'
          }}>
            <div className="avatar" style={{
              width: '60px',
              height: '60px',
              fontSize: '20px',
              fontWeight: 'bold'
            }}>
              {profile.username?.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <h3 style={{
                fontSize: '18px',
                color: '#1e3044'
              }}>
                {profile.prenom} {profile.nom}
              </h3>
              <span className="muted" style={{ fontSize: '14px' }}>
                @{profile.username}
              </span>
            </div>
          </div>

          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '15px 20px'
          }}>
            <div>
              <small style={{
                fontWeight: 'bold',
                textTransform: 'uppercase',
                fontSize: '10px',
                color: '#8190a0'
              }}>
                Email
              </small>
              <p style={{
                color: '#273b4f',
                fontSize: '14px',
                marginTop: '4px'
              }}>
                {profile.email}
              </p>
            </div>
            <div>
              <small style={{
                fontWeight: 'bold',
                textTransform: 'uppercase',
                fontSize: '10px',
                color: '#8190a0'
              }}>
                Rôle
              </small>
              <p style={{ marginTop: '4px' }}>
                <span className="role">
                  {profile.role?.replace('_', ' ')}
                </span>
              </p>
            </div>
            <div>
              <small style={{
                fontWeight: 'bold',
                textTransform: 'uppercase',
                fontSize: '10px',
                color: '#8190a0'
              }}>
                Statut du compte
              </small>
              <p style={{
                color: '#273b4f',
                fontSize: '14px',
                marginTop: '4px'
              }}>
                <span className={profile.enabled ? 'online' : 'offline'}>
                  {profile.enabled ? '● Actif' : '● Inactif'}
                </span>
              </p>
            </div>
            <div>
              <small style={{
                fontWeight: 'bold',
                textTransform: 'uppercase',
                fontSize: '10px',
                color: '#8190a0'
              }}>
                Membre depuis
              </small>
              <p style={{
                color: '#273b4f',
                fontSize: '14px',
                marginTop: '4px'
              }}>
                {fmtDate(profile.createdAt?.slice(0, 10))}
              </p>
            </div>
          </div>

          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            borderTop: '1px solid #edf1f3',
            paddingTop: '16px',
            marginTop: '10px'
          }}>
            <button
              className="secondary"
              onClick={() => {
                onLogout()
                onClose()
              }}
              style={{
                background: '#fff0ef',
                color: '#b54745',
                border: '1px solid #fce9e9'
              }}
            >
              ⇥ Se déconnecter
            </button>
            <button className="primary" onClick={onClose}>
              Fermer
            </button>
          </div>
        </div>
      )}
    </Modal>
  )
}

function fmtDate(d) {
  return d
    ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`))
    : '—'
}
