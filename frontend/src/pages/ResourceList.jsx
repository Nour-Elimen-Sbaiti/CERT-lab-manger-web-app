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

function ResourceList({ token, kind }) {
  const config = {
    laboratoires: {
      title: "Laboratoires d'étalonnage",
      eyebrow: 'RÉFÉRENTIEL',
      desc: "Gérez les contacts et laboratoires d'étalonnage externes.",
      endpoint: '/api/labos-etalonnage',
      fields: [
        ['nom', 'Nom du laboratoire *'],
        ['contactNom', 'Nom du contact'],
        ['contactPrenom', "Prénom du contact"],
        ['contactEmail', 'Email', 'email'],
        ['contactTelephone', 'Téléphone']
      ]
    },
    personnel: {
      title: 'Personnel',
      eyebrow: 'ÉQUIPE',
      desc: 'Gérez les agents et techniciens qui travaillent dans le laboratoire.',
      endpoint: '/api/personnel',
      fields: [
        ['nom', 'Nom *'],
        ['prenom', 'Prénom'],
        ['email', 'Email', 'email'],
        ['telephone', 'Téléphone']
      ]
    }
  }[kind]

  const [rows, setRows] = useState([])
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')

  const load = () =>
    api(config.endpoint, { token })
      .then(setRows)
      .catch(e => setError(e.message || 'Une erreur est survenue.'))

  useEffect(load, [token])

  async function save(e) {
    e.preventDefault()
    if (!confirm("Voulez-vous vraiment enregistrer ces informations ?"))
      return
    try {
      await api(
        config.endpoint + (form.id ? `/${form.id}` : ''),
        { method: form.id ? 'PUT' : 'POST', body: form, token }
      )
      setForm(null)
      load()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    }
  }

  async function remove(id) {
    if (!confirm('Supprimer cet élément ?')) return
    try {
      await api(`${config.endpoint}/${id}`, {
        method: 'DELETE',
        token
      })
      load()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    }
  }

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">{config.eyebrow}</p>
          <h1>{config.title}</h1>
          <p className="muted">{config.desc}</p>
        </div>
        <button className="primary" onClick={() => setForm({})}>
          + Ajouter
        </button>
      </div>
      {error && <Notice text={error} />}
      <div className="cards-grid">
        {rows.map(r => {
          const isLab = kind === 'laboratoires'
          const name = isLab
            ? r.nom
            : `${r.prenom || ''} ${r.nom || ''}`
          const email = isLab ? r.contactEmail : r.email
          const phone = isLab ? r.contactTelephone : r.telephone
          const subtitle = isLab
            ? ([r.contactPrenom, r.contactNom].filter(Boolean).join(' ') ||
                'Contact non renseigné')
            : 'Agent du laboratoire'

          return (
            <article className="contact-card" key={r.id}>
              <div className="contact-avatar">
                {(isLab ? r.nom : r.nom || '?').slice(0, 1).toUpperCase()}
              </div>
              <div className="contact-main">
                <h3>{name}</h3>
                <p>{subtitle}</p>
                <small style={{ marginTop: '5px' }}>
                  {email ? (
                    <a
                      href={`mailto:${email}`}
                      style={{
                        color: '#118378',
                        textDecoration: 'none',
                        fontWeight: '600'
                      }}
                      title="Cliquer pour envoyer un mail"
                    >
                      ✉ {email}
                    </a>
                  ) : (
                    '✉ Email non renseigné'
                  )}
                </small>
                <small>
                  ☎ {phone || 'Téléphone non renseigné'}
                </small>
              </div>
              <IconButton
                title="Supprimer"
                onClick={() => remove(r.id)}
              >
                ×
              </IconButton>
            </article>
          )
        })}
        {!rows.length && (
          <div className="panel">
            <Empty text="Aucun élément enregistré." />
          </div>
        )}
      </div>
      {form && (
        <Modal
          title={`Ajouter ${kind === 'personnel' ? 'un membre' : 'un laboratoire'}`}
          onClose={() => setForm(null)}
        >
          <form className="form-grid" onSubmit={save}>
            {config.fields.map(([key, label, type]) => (
              <label key={key}>
                {label}
                <input
                  required={label.includes('*')}
                  type={type || 'text'}
                  value={form[key] || ''}
                  onChange={e =>
                    setForm({ ...form, [key]: e.target.value })
                  }
                />
              </label>
            ))}
            <div className="modal-actions full">
              <button
                type="button"
                className="secondary"
                onClick={() => setForm(null)}
              >
                Annuler
              </button>
              <button className="primary">Enregistrer</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}

export default ResourceList
