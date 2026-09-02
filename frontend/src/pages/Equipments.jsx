import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { Modal } from '../components/Modal'
import { getEquipements, createEquipement, updateEquipement, changeEquipementState, getDocumentsByEquipement, uploadDocument, deleteDocument, getFicheDeVie, getInterventionsByEquipement } from '../services/equipements'
import { StateLabels, Badge, IconButton, getEtatStyle, fmtDate } from './AuthPages'

function exportToCSV(items) {
  if (!items || !items.length) return
  const headers = [
    'ID', 'Designation', 'Code Identifiant', 'Marque', 'Modele',
    'Numero Serie', 'Emplacement', 'Etat', 'Fournisseur',
    'Date Acquisition', 'Date Mise En Service', 'Valeur Origine',
    'Garantie JusquAu', 'Observations'
  ]
  const csvRows = [headers.join(',')]

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
    ]
    csvRows.push(values.join(','))
  }

  const csvContent = "data:text/csv;charset=utf-8,\uFEFF" + csvRows.join("\n")
  const encodedUri = encodeURI(csvContent)
  const link = document.createElement("a")
  link.setAttribute("href", encodedUri)
  link.setAttribute("download", `equipements_${new Date().toISOString().slice(0, 10)}.csv`)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

export function Equipments({ token }) {
  const [items, setItems] = useState([])
  const [q, setQ] = useState('')
  const [filter, setFilter] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editing, setEditing] = useState(null)
  const [selected, setSelected] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const params = {}
      if (q) params.q = q
      if (filter) params.etat = filter
      setItems(await getEquipements(token, params))
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    const t = setTimeout(load, 220)
    return () => clearTimeout(t)
  }, [q, filter, token])

  async function changeState(id, etat) {
    if (!confirm("Voulez-vous vraiment changer l'état de cet équipement ?")) return
    try {
      await changeEquipementState(token, id, etat)
      load()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    }
  }

  return (
    <div className="content">
      <div className="page-intro">
        <div>
          <p className="eyebrow">PARC MÉTROLOGIQUE</p>
          <h1>Équipements</h1>
          <p className="muted">Gérez et suivez tous les équipements de votre laboratoire.</p>
        </div>
        <button className="primary" onClick={() => setEditing(emptyEquipment)}>
          + Ajouter un équipement
        </button>
      </div>
      <div className="toolbar">
        <div className="search" style={{ width: '420px' }}>
          ⌕
          <input
            value={q}
            onChange={e => setQ(e.target.value)}
            placeholder="Rechercher (désignation, marque, modèle, n° série, emplacement...)"
          />
        </div>
        <select value={filter} onChange={e => setFilter(e.target.value)}>
          <option value="">Tous les états</option>
          {StateLabels.map(s => (
            <option value={s} key={s}>{s}</option>
          ))}
        </select>
        <button
          className="secondary"
          onClick={() => exportToCSV(items)}
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
        >
          📥 Exporter CSV
        </button>
        <span className="result-count">
          {items.length} résultat{items.length !== 1 ? 's' : ''}
        </span>
      </div>
      {error && <div className="notice">{error}</div>}
      <div className="panel table-wrap">
        {loading ? (
          <div className="loader">Chargement des données…</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ÉQUIPEMENT</th>
                <th>IDENTIFIANT</th>
                <th>EMPLACEMENT</th>
                <th>ÉTAT</th>
                <th>PROCHAINE ÉCHÉANCE</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {items.map(e => (
                <tr key={e.id} onClick={() => setSelected(e)}>
                  <td>
                    <div className="equipment-cell">
                      <span className="equipment-icon">⌘</span>
                      <div>
                        <b>{e.designation}</b>
                        <small>{e.marque} · {e.modele}</small>
                      </div>
                    </div>
                  </td>
                  <td><code>{e.codeIdentifiant}</code></td>
                  <td>{e.codeEmplacement || '—'}</td>
                  <td>
                    <select
                      value={e.etat}
                      onChange={ev => {
                        ev.stopPropagation()
                        changeState(e.id, ev.target.value)
                      }}
                      onClick={ev => ev.stopPropagation()}
                      style={{
                        ...getEtatStyle(e.etat),
                        border: 'none',
                        borderRadius: '5px',
                        padding: '6px 12px',
                        fontSize: '11px',
                        fontWeight: 'bold',
                        cursor: 'pointer',
                        outline: 'none',
                        appearance: 'none',
                        textAlign: 'center',
                        display: 'inline-block'
                      }}
                    >
                      {StateLabels.map(s => (
                        <option key={s} value={s} style={{
                          color: '#1c2c3f',
                          background: 'white',
                          fontWeight: 'normal'
                        }}>{s}</option>
                      ))}
                    </select>
                  </td>
                  <td>{e.garantieJusquAu ? fmtDate(e.garantieJusquAu) : '—'}</td>
                  <td>
                    <IconButton
                      title="Modifier l'équipement"
                      onClick={ev => {
                        ev.stopPropagation()
                        setEditing(e)
                      }}
                    >
                      •••
                    </IconButton>
                  </td>
                </tr>
              ))}
              {!items.length && (
                <tr>
                  <td colSpan="6">
                    <Empty text="Aucun équipement ne correspond à votre recherche." />
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>
      {editing && (
        <EquipmentModal
          initial={editing}
          token={token}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null)
            load()
          }}
        />
      )}
      {selected && (
        <EquipmentDetail
          item={selected}
          token={token}
          onClose={() => setSelected(null)}
          onEdit={() => {
            setEditing(selected)
            setSelected(null)
          }}
        />
      )}
    </div>
  )
}

function EquipmentModal({ initial, token, onClose, onSaved }) {
  const [form, setForm] = useState({ ...emptyEquipment, ...initial })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  function update(k, v) {
    setForm({ ...form, [k]: v })
  }

  async function submit(e) {
    e.preventDefault()
    if (!confirm("Voulez-vous vraiment enregistrer ces modifications ?")) return
    setBusy(true)
    setError('')
    const body = {
      ...form,
      periodiciteValeur: Number(form.periodiciteValeur),
      valeurOrigine: form.valeurOrigine ? Number(form.valeurOrigine) : null
    }
    try {
      const method = initial.id ? 'PUT' : 'POST'
      const endpoint = initial.id
        ? `/api/equipements/${initial.id}`
        : '/api/equipements'
      await api(endpoint, { method, body, token })
      onSaved()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
    } finally {
      setBusy(false)
    }
  }

  const fields = [
    ['codeIdentifiant', 'Code identifiant *'],
    ['designation', 'Désignation *'],
    ['marque', 'Marque *'],
    ['modele', 'Modèle *'],
    ['numeroSerie', 'N° de série *'],
    ['codeEmplacement', 'Emplacement'],
    ['fournisseur', 'Fournisseur'],
    ['valeurOrigine', 'Valeur d'origine', 'number']
  ]

  return (
    <Modal title={initial.id ? 'Modifier l'équipement' : 'Nouvel équipement'} onClose={onClose}>
      <form className="form-grid" onSubmit={submit}>
        {fields.map(([k, label, type]) => (
          <label key={k}>
            {label}
            <input
              type={type || 'text'}
              required={label.includes('*')}
              value={form[k] || ''}
              onChange={e => update(k, e.target.value)}
            />
          </label>
        ))}
        <label>
          Date d'acquisition *
          <input
            type="date"
            required
            value={form.dateAcquisition || ''}
            onChange={e => update('dateAcquisition', e.target.value)}
          />
        </label>
        <label>
          Mise en service
          <input
            type="date"
            value={form.dateMiseEnService || ''}
            onChange={e => update('dateMiseEnService', e.target.value)}
          />
        </label>
        <label>
          Périodicité *
          <input
            type="number"
            min="1"
            required
            value={form.periodiciteValeur}
            onChange={e => update('periodiciteValeur', e.target.value)}
          />
        </label>
        <label>
          Unité
          <select
            value={form.periodiciteUnite}
            onChange={e => update('periodiciteUnite', e.target.value)}
          >
            <option value="MOIS">Mois</option>
            <option value="ANNEE">Année</option>
          </select>
        </label>
        <label>
          Garantie jusqu'au
          <input
            type="date"
            value={form.garantieJusquAu || ''}
            onChange={e => update('garantieJusquAu', e.target.value)}
          />
        </label>
        <label className="full">
          Observations
          <textarea
            value={form.observations || ''}
            onChange={e => update('observations', e.target.value)}
            rows="3"
          />
        </label>
        {error && <div className="error full">{error}</div>}
        <div className="modal-actions full">
          <button
            type="button"
            className="secondary"
            onClick={onClose}
          >
            Annuler
          </button>
          <button
            className="primary"
            disabled={busy}
          >
            {busy ? 'Enregistrement…' : 'Enregistrer'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

function EquipmentDetail({ item, token, onClose, onEdit }) {
  const [history, setHistory] = useState([])
  const [documents, setDocuments] = useState([])
  const [tab, setTab] = useState('history')
  const [uploadFile, setUploadFile] = useState(null)
  const [docType, setUploadType] = useState('RAPPORT')
  const [uploadBusy, setUploadBusy] = useState(false)
  const [uploadError, setUploadError] = useState('')

  useEffect(() => {
    api(`/api/interventions/equipement/${item.id}`, { token })
      .then(setHistory)
      .catch(() => {})
    getDocumentsByEquipement(token, item.id).then(setDocuments).catch(() => {})
  }, [item.id, token])

  async function download() {
    try {
      const blob = await getFicheDeVie(token, item.id)
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `fiche-de-vie-${item.id}.pdf`
      a.click()
      URL.revokeObjectURL(url)
    } catch (e) {
      alert(e.message || 'Une erreur est survenue.')
    }
  }

  const handleUpload = async (e) => {
    e.preventDefault()
    if (!uploadFile) return
    setUploadBusy(true)
    setUploadError('')
    try {
      await uploadDocument(token, item.id, docType, uploadFile)
      getDocumentsByEquipement(token, item.id).then(setDocuments).catch(() => {})
      setUploadFile(null)
      const fileIn = document.getElementById('file-input')
      if (fileIn) fileIn.value = ''
    } catch (e) {
      setUploadError(e.message || 'Une erreur est survenue.')
    } finally {
      setUploadBusy(false)
    }
  }

  const handleDeleteDoc = async (docId) => {
    if (!confirm('Supprimer ce document ?')) return
    try {
      await deleteDocument(token, docId)
      getDocumentsByEquipement(token, item.id).then(setDocuments).catch(() => {})
    } catch (e) {
      alert(e.message || 'Une erreur est survenue.')
    }
  }

  const qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${encodeURIComponent(
    `ID: ${item.codeIdentifiant}\nDesignation: ${item.designation}\nModele: ${item.modele}\nSerie: ${item.numeroSerie}`
  )}`

  return (
    <Modal title={item.designation} onClose={onClose} wide>
      <div className="detail-head" style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'flex-start',
        width: '100%'
      }}>
        <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
          <div style={{
            background: '#f5f8fb',
            padding: '6px',
            borderRadius: '8px',
            border: '1px solid #e2e8f0'
          }} title="Code QR de l'équipement">
            <img
              src={qrCodeUrl}
              alt="QR Code"
              style={{ width: '80px', height: '80px', display: 'block' }}
            />
          </div>
          <div>
            <code style={{ fontSize: '12px' }}>{item.codeIdentifiant}</code>
            <h2 style={{ fontSize: '20px', marginTop: '4px' }}>{item.designation}</h2>
            <p style={{
              margin: '4px 0 0', fontSize: '13px',
              color: '#728397'
            }}>
              {item.marque} · {item.modele} · N° {item.numeroSerie}
            </p>
          </div>
        </div>
        <Badge state={item.etat} />
      </div>
      <div className="tabs">
        <button
          className={tab === 'history' ? 'selected' : ''}
          onClick={() => setTab('history')}
        >
          Historique ({history.length})
        </button>
        <button
          className={tab === 'docs' ? 'selected' : ''}
          onClick={() => setTab('docs')}
        >
          Documents ({documents.length})
        </button>
      </div>
      {tab === 'history' ? (
        <div className="timeline">
          {history.map(h => (
            <div className="timeline-item" key={h.id}>
              <span />
              <div>
                <b>{h.type?.replaceAll('_', ' ')}</b>
                <p>{h.natureIntervention}</p>
                <small>
                  {fmtDate(h.dateIntervention)}
                  · {h.intervenantSociete || h.intervenantNom || 'Intervenant non renseigné'}
                </small>
              </div>
            </div>
          ))}
          {!history.length && (
            <Empty text="Aucune intervention enregistrée." />
          )}
        </div>
      ) : (
        <div>
          <div style={{
            maxHeight: '200px',
            overflowY: 'auto',
            marginBottom: '20px'
          }}>
            {documents.map(d => (
              <div
                className="document"
                key={d.id}
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '10px 0',
                  borderBottom: '1px solid #e9eff2'
                }}
              >
                <div>
                  ▧ <b>{d.nomFichier}</b>
                  <small>
                    {d.typeDocument?.replaceAll('_', ' ')}
                    · {d.uploadedAt?.slice(0, 10)}
                  </small>
                </div>
                <button
                  type="button"
                  onClick={() => handleDeleteDoc(d.id)}
                  style={{
                    background: 'none',
                    color: '#b54745',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '16px',
                    fontWeight: 'bold'
                  }}
                >
                  ×
                </button>
              </div>
            ))}
            {!documents.length && (
              <Empty text="Aucun document associé." />
            )}
          </div>

          <form
            onSubmit={handleUpload}
            style={{
              borderTop: '1px solid #edf1f3',
              paddingTop: '15px',
              display: 'flex',
              flexDirection: 'column',
              gap: '10px'
            }}
          >
            <h4 style={{
              fontSize: '13px',
              fontWeight: 'bold',
              color: '#1a988b'
            }}>
              Ajouter un document
            </h4>
            <div style={{
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              gap: '10px'
            }}>
              <label style={{ fontSize: '11px', fontWeight: 'bold' }}>
                Type de document
                <select
                  value={docType}
                  onChange={e => setUploadType(e.target.value)}
                  style={{
                    display: 'block',
                    width: '100%',
                    marginTop: '5px',
                    padding: '8px',
                    borderRadius: '5px',
                    border: '1px solid #d8e2e9'
                  }}
                >
                  <option value="RAPPORT">Rapport</option>
                  <option value="CERTIFICAT_ETALONNAGE">
                    Certificat d'étalonnage
                  </option>
                  <option value="CONTRAT_MAINTENANCE">
                    Contrat de maintenance
                  </option>
                  <option value="MANUEL">Manuel</option>
                  <option value="GARANTIE">Garantie</option>
                  <option value="AUTRE">Autre</option>
                </select>
              </label>
              <label style={{ fontSize: '11px', fontWeight: 'bold' }}>
                Fichier
                <input
                  id="file-input"
                  type="file"
                  required
                  onChange={e => setUploadFile(e.target.files[0])}
                  style={{
                    display: 'block',
                    width: '100%',
                    marginTop: '5px',
                    padding: '5px',
                    borderRadius: '5px',
                    border: '1px solid #d8e2e9'
                  }}
                />
              </label>
            </div>
            {uploadError && (
              <div className="error" style={{ fontSize: '12px', padding: '8px' }}>
                {uploadError}
              </div>
            )}
            <button
              className="primary small"
              disabled={uploadBusy || !uploadFile}
              style={{ alignSelf: 'flex-start' }}
            >
              {uploadBusy ? 'Téléversement…' : 'Uploader'}
            </button>
          </form>
        </div>
      )}
      <div className="modal-actions">
        <button className="secondary" onClick={download}>
          ⇩ Fiche de vie
        </button>
        <button className="primary" onClick={onEdit}>
          Modifier
        </button>
      </div>
    </Modal>
  )
}

function Empty({ text }) {
  return <div className="empty">{text}</div>
}
