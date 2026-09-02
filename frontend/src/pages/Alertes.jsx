import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { getAlertes } from '../services/alertes'
import { Empty } from '../components/Empty'
import { Notice } from '../components/Notice'

function getAlertRisk(jours) {
  if (jours <= 5)
    return { color: '#c65355', bg: '#fce9e9', label: 'Risque Élevé' }
  if (jours <= 15)
    return { color: '#ce8b18', bg: '#fff1d7', label: 'Risque Moyen' }
  return { color: '#16835d', bg: '#e0f5ea', label: 'Risque Faible' }
}

export function Alerts({ token }) {
  const [alerts, setAlerts] = useState([])
  const [days, setDays] = useState(30)
  const [error, setError] = useState('')

  useEffect(() => {
    getAlertes(token, days)
      .then(setAlerts)
      .catch(e => setError(e.message || 'Une erreur est survenue.'))
  }, [token, days])

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">SUIVI DES ÉCHÉANCES</p>
          <h1>Alertes</h1>
          <p className="muted">
            Anticipez les opérations qui nécessitent votre attention.
          </p>
        </div>
        <select value={days} onChange={e => setDays(e.target.value)}>
          <option value="15">15 prochains jours</option>
          <option value="30">30 prochains jours</option>
          <option value="60">60 prochains jours</option>
        </select>
      </div>
      {error && <Notice text={error} />}
      <div className="panel alert-list">
        {alerts.map((a, i) => {
          const risk = getAlertRisk(a.joursRestants)
          return (
            <div className="alert-row" key={i}>
              <span
                className="warning-icon"
                style={{ backgroundColor: risk.bg, color: risk.color }}
              >
                !
              </span>
              <div>
                <p className="eyebrow" style={{ color: risk.color }}>
                  {a.type} ·
                  <span style={{ fontWeight: 'normal', textTransform: 'none' }}>
                    {risk.label}
                  </span>
                </p>
                <h3>
                  {a.equipementDesignation}{' '}
                  <code>{a.equipementCodeIdentifiant}</code>
                </h3>
                <p className="muted">{a.details}</p>
              </div>
              <div className="alert-date">
                <b style={{ color: risk.color }}>{a.joursRestants}</b>
                <small>jours restants</small>
                <span>{fmtDate(a.dateEcheance)}</span>
              </div>
            </div>
          )
        })}
        {!alerts.length && (
          <Empty text="Aucune alerte pour cette période." />
        )}
      </div>
    </div>
  )
}

function fmtDate(d) {
  return d
    ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`))
    : '—'
}
