import React from 'react'
import { BellIcon } from './Icons'
import { IconButton } from './Icons'

const nav = [
  ['dashboard', '▦', 'Vue d\'ensemble'],
  ['equipements', '⌘', 'Équipements'],
  ['alertes', '◉', 'Alertes'],
  ['laboratoires', '◌', 'Laboratoires'],
  ['personnel', '♙', 'Personnel'],
  ['demandes', '▤', 'Demandes']
]

export function AppShell({ user, page, setPage, onLogout, onAvatarClick, alertCount }) {
  const admin = user.role === 'ADMINISTRATEUR'

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand" style={{
          display: 'flex', flexDirection: 'column',
          alignItems: 'center', gap: '10px',
          padding: '10px 0', width: '100%'
        }}>
          <img src="/logo_cert.png" alt="Logo CERT"
            style={{ height: '120px', width: 'auto', objectFit: 'contain' }} />
          <span style={{
            fontSize: '15px', letterSpacing: '0.5px',
            textAlign: 'center', fontWeight: 'bold'
          }}>
            CERT <b>CALIBRATION HUB</b>
          </span>
        </div>
        <nav>
          {nav.map(([id, icon, label]) => (
            <button
              key={id}
              className={page === id ? 'active' : ''}
              onClick={() => setPage(id)}
            >
              <i>{icon}</i>{label}
            </button>
          ))}
          {admin && (
            <>
              <div className="nav-label">ADMINISTRATION</div>
              <button
                className={page === 'utilisateurs' ? 'active' : ''}
                onClick={() => setPage('utilisateurs')}
              >
                <i>♧</i>Utilisateurs
              </button>
            </>
          )}
        </nav>
        <div className="sidebar-bottom">
          <button className="logout" onClick={onLogout}
            style={{ marginTop: '20px', width: '100%' }}>
            ⇥ Déconnexion
          </button>
        </div>
      </aside>
      <section className="workspace">
        <header>
          <div className="crumb">
            CERT CALIBRATION HUB <span>/</span>
            {nav.find(n => n[0] === page)?.[2] || 'Administration'}
          </div>
          <div className="header-actions">
            <span className="date">
              {new Intl.DateTimeFormat('fr-FR', {
                weekday: 'long', day: 'numeric', month: 'long'
              }).format(new Date())}
            </span>
            <button className="bell" onClick={() => setPage('alertes')}
              title={`${alertCount || 0} alertes actives`}>
              <BellIcon />
              {alertCount > 0 && (
                <span className="bell-badge">{alertCount}</span>
              )}
            </button>
            <div className="avatar" onClick={onAvatarClick}
              style={{ cursor: 'pointer' }}>
              {user.username?.slice(0, 2).toUpperCase()}
            </div>
          </div>
        </header>
        {/* children will be rendered here */}
      </section>
    </div>
  )
}
