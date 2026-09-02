import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { getDashboard, getAlertes } from '../services/dashboard'
import { StateLabels, Badge, IconButton, getEtatStyle, fmtDate } from './AuthPages'

export function Dashboard({ token, username, setPage }) {
  const [data, setData] = useState(null)
  const [alerts, setAlerts] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([
      getDashboard(token),
      getAlertes(token, 30)
    ]).then(([d, a]) => {
      setData(d)
      setAlerts(a)
    }).catch(e => setError(e.message || 'Une erreur est survenue.'))
  }, [token])

  const chart = data?.repartitionParEtat || {}

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">TABLEAU DE BORD</p>
          <h1>Bonjour, bienvenue {username}</h1>
          <p className="muted">Voici un aperçu de l'activité de votre laboratoire.</p>
        </div>
        <button className="primary" onClick={() => setPage('equipements')}>
          + Nouvel équipement
        </button>
      </div>
      {error ? (
        <div className="notice">{error}</div>
      ) : !data ? (
        <div className="loader">Chargement des données…</div>
      ) : (
        <>
          <div className="stats">
            <Stat
              label="Équipements"
              value={data.totalEquipements}
              icon="⌘"
              tone="blue"
            />
            <Stat
              label="En service"
              value={(chart.DISPONIBLE || 0) + (chart.EN_UTILISATION || 0)}
              icon="✓"
              tone="green"
            />
            <Stat
              label="Alertes actives"
              value={data.nombreAlertesActives}
              icon="!"
              tone="amber"
            />
            <Stat
              label="Personnel"
              value={data.totalPersonnel}
              icon="♙"
              tone="purple"
            />
          </div>
          <div className="dashboard-grid">
            <section className="panel status-panel">
              <div className="panel-head">
                <div>
                  <h3>État des équipements</h3>
                  <p>Répartition de votre parc</p>
                </div>
                <button
                  className="link"
                  onClick={() => setPage('equipements')}
                >
                  Voir tout →
                </button>
              </div>
              <div className="distribution">
                {Object.keys(chart)
                  .filter(s => chart[s])
                  .map(s => (
                    <div key={s}>
                      <div>
                        <Badge state={s} />
                        <b>{chart[s]}</b>
                      </div>
                      <span style={{
                        width: `${Math.max(8, chart[s] / Math.max(1, data.totalEquipements) * 100)}%`
                      }} />
                    </div>
                  ))}
                {!Object.keys(chart).length && (
                  <Empty text="Aucun équipement enregistré" />
                )}
              </div>
            </section>
            <section className="panel alerts-panel">
              <div className="panel-head">
                <div>
                  <h3>Échéances à surveiller</h3>
                  <p>Dans les 30 prochains jours</p>
                </div>
                <button
                  className="link"
                  onClick={() => setPage('alertes')}
                >
                  Tout voir →
                </button>
              </div>
              {alerts.slice(0, 4).map((a, i) => (
                <div className="alert-line" key={i}>
                  <span className="alert-dot">!</span>
                  <div>
                    <b>{a.equipementDesignation}</b>
                    <small>{a.details}</small>
                  </div>
                  <time>{a.joursRestants} j</time>
                </div>
              ))}
              {!alerts.length && (
                <Empty text="Aucune échéance proche" />
              )}
            </section>
          </div>
        </>
      )}
    </div>
  )
}

function Stat({ label, value, icon, tone }) {
  return (
    <div className="stat">
      <span className={`stat-icon ${tone}`}>{icon}</span>
      <div>
        <small>{label}</small>
        <strong>{value}</strong>
      </div>
    </div>
  )
}

function Empty({ text }) {
  return <div className="empty">{text}</div>
}
