import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { Modal } from '../components/Modal'
import { IconButton } from '../components/Icons'
import { Loader, Empty, Notice } from '../components/common'

function fmtDate(d) {
  return d
    ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`))
    : '—'
}

function Users({ token }) {
  const [rows, setRows] = useState([])
  const [error, setError] = useState('')

  const load = () =>
    api('/api/users', { token })
      .then(setRows)
      .catch(e => setError(e.message || 'Une erreur est survenue.'))

  useEffect(load, [token])

  async function toggle(u) {
    try {
      await api(
        `/api/users/${u.id}/${u.enabled ? 'desactiver' : 'activer'}`,
        { method: 'PATCH', token }
      )
      load()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    }
  }

  async function removeUser(id) {
    if (
      !confirm(
        "Voulez-vous vraiment supprimer définitivement cet utilisateur ?"
      )
    )
      return
    try {
      await api(`/api/users/${id}`, { method: 'DELETE', token })
      load()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    }
  }

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">ADMINISTRATION</p>
          <h1>Utilisateurs</h1>
          <p className="muted">
            Gérez les accès, les suppressions et les rôles de votre
            organisation.
          </p>
        </div>
      </div>
      {error && <Notice text={error} />}
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
            {rows.map(u => (
              <tr key={u.id}>
                <td>
                  <b>{u.prenom} {u.nom}</b>
                  <small>@{u.username}</small>
                </td>
                <td>{u.email}</td>
                <td>
                  <span className="role">{u.role?.replace('_', ' ')}</span>
                </td>
                <td>
                  <span className={u.enabled ? 'online' : 'offline'}>
                    {u.enabled ? '● Actif' : '● Désactivé'}
                  </span>
                </td>
                <td>
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button
                      className="secondary small"
                      onClick={() => toggle(u)}
                    >
                      {u.enabled ? 'Désactiver' : 'Activer'}
                    </button>
                    <button
                      className="secondary small"
                      onClick={() => removeUser(u.id)}
                      style={{
                        background: '#fff0ef',
                        color: '#b54745',
                        borderColor: '#fce9e9'
                      }}
                    >
                      Supprimer
                    </button>
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

export default Users
