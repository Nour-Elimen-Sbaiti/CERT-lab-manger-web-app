import React, { useState, useEffect } from 'react'
import { api } from '../api'
import { getDemandes, getDemandePdf } from '../services/demandes'
import { Modal } from '../components/Modal'
import { IconButton } from '../components/Icons'
import { Loader, Empty, Notice } from '../components/common'

function fmtDate(d) {
  return d
    ? new Intl.DateTimeFormat('fr-FR').format(new Date(`${d}T00:00:00`))
    : '—'
}

export function DemandesPrestation({ token }) {
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
        getDemandes(token),
        api('/api/etalonnages', { token }),
        api('/api/labos-etalonnage', { token })
      ])
      setDemandes(demList)
      setEtalonnages(etalList)
      setLabs(labList)
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
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
      const blob = await getDemandePdf(token, id)
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `demande-prestation-${id}.pdf`
      a.click()
      URL.revokeObjectURL(url)
    } catch (e) {
      alert(e.message || 'Une erreur est survenue.')
    }
  }

  const handleEdit = (d, ev) => {
    ev.stopPropagation()
    setForm({
      etalonnageId: d.etalonnageId?.toString() || '',
      laboEtalonnageId: d.laboEtalonnageNom
        ? (labs.find(l => l.nom === d.laboEtalonnageNom)?.id?.toString() || '')
        : '',
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
    if (!confirm("Voulez-vous vraiment enregistrer ces informations ?"))
      return
    setSubBusy(true)
    setError('')
    try {
      const body = {
        ...form,
        etalonnageId: Number(form.etalonnageId),
        laboEtalonnageId: form.laboEtalonnageId
          ? Number(form.laboEtalonnageId)
          : null
      }
      const endpoint = editingItem
        ? `/api/demandes-prestation/${editingItem}`
        : '/api/demandes-prestation'
      const method = editingItem ? 'PUT' : 'POST'

      await api(endpoint, { method, body, token })
      handleCloseModal()
      loadData()
    } catch (e) {
      setError(e.message || 'Une erreur est survenue.')
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
          <p className="muted">
            Gérez et générez les fiches de demande de prestation de service
            pour vos étalonnages.
          </p>
        </div>
        <button className="primary" onClick={() => setShowCreate(true)}>
          + Nouvelle Demande
        </button>
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
                  <td>
                    <b>ID {d.equipementId}</b>{' '}
                    <small>Étalonnage #{d.etalonnageId}</small>
                  </td>
                  <td>{d.laboEtalonnageNom || '—'}</td>
                  <td>{fmtDate(d.dateDemande)}</td>
                  <td>
                    <span className="role">{d.lieu}</span>
                  </td>
                  <td><code>{d.regleDecision}</code></td>
                  <td><code>{d.niveauIncertitude}</code></td>
                  <td>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button
                        className="secondary small"
                        onClick={(ev) => handleDownloadPdf(d.id, ev)}
                      >
                        📄 PDF
                      </button>
                      <button
                        className="secondary small"
                        onClick={(ev) => handleEdit(d, ev)}
                      >
                        Modifier
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {!demandes.length && (
                <tr>
                  <td colSpan="8">
                    <Empty text="Aucune demande de prestation enregistrée." />
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </div>

      {showCreate && (
        <Modal
          title={
            editingItem
              ? "Modifier la demande de prestation"
              : "Nouvelle demande de prestation"
          }
          onClose={handleCloseModal}
        >
          <form onSubmit={handleSubmit} className="form-grid">
            <label className="full">
              Sélectionner l'Étalonnage *
              <select
                value={form.etalonnageId}
                onChange={e =>
                  setForm({ ...form, etalonnageId: e.target.value })
                }
                required
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              >
                <option value="">-- Choisir un étalonnage --</option>
                {etalonnages.map(et => (
                  <option key={et.id} value={et.id}>
                    {et.equipementDesignation} ({et.equipementCodeIdentifiant}) - Étalonnage #{et.id}
                  </option>
                ))}
              </select>
            </label>

            <label className="full">
              Laboratoire d'étalonnage *
              <select
                value={form.laboEtalonnageId}
                onChange={e =>
                  setForm({ ...form, laboEtalonnageId: e.target.value })
                }
                required
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              >
                <option value="">-- Choisir un laboratoire --</option>
                {labs.map(l => (
                  <option key={l.id} value={l.id}>
                    {l.nom}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Date de demande *
              <input
                type="date"
                value={form.dateDemande}
                onChange={e =>
                  setForm({ ...form, dateDemande: e.target.value })
                }
                required
              />
            </label>

            <label>
              Lieu *
              <select
                value={form.lieu}
                onChange={e =>
                  setForm({ ...form, lieu: e.target.value })
                }
                required
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              >
                <option value="LABORATOIRE">Laboratoire</option>
                <option value="SITE">Site</option>
              </select>
            </label>

            <label>
              Règle de décision *
              <select
                value={form.regleDecision}
                onChange={e =>
                  setForm({ ...form, regleDecision: e.target.value })
                }
                required
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              >
                <option value="BINAIRE_SIMPLE">Binaire simple</option>
                <option value="BINAIRE_BANDE_GARDE">
                  Binaire bande de garde
                </option>
                <option value="NON_BINAIRE_BANDE_GARDE">
                  Non binaire bande de garde
                </option>
              </select>
            </label>

            <label>
              Niveau d'incertitude *
              <select
                value={form.niveauIncertitude}
                onChange={e =>
                  setForm({ ...form, niveauIncertitude: e.target.value })
                }
                required
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              >
                <option value="SUPERIEUR_1E_04">
                  Supérieur à 10⁻⁴
                </option>
                <option value="INFERIEUR_1E_04">
                  Inférieur à 10⁻⁴
                </option>
              </select>
            </label>

            <label className="full">
              État à la livraison
              <input
                value={form.etatALaLivraison}
                onChange={e =>
                  setForm({ ...form, etatALaLivraison: e.target.value })
                }
                placeholder="ex. Parfait état, livré avec câbles..."
              />
            </label>

            <label className="full">
              Points à étalonner
              <textarea
                value={form.pointsAEtalonner}
                onChange={e =>
                  setForm({ ...form, pointsAEtalonner: e.target.value })
                }
                placeholder="ex. 10V, 50V, 100V..."
                rows="2"
                style={{
                  display: 'block',
                  width: '100%',
                  marginTop: '7px',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #d8e2e9'
                }}
              />
            </label>

            <div
              className="form-row full"
              style={{ display: 'flex', gap: '20px', marginTop: '10px' }}
            >
              <label className="checkbox">
                <input
                  type="checkbox"
                  checked={form.comparaisonInterLabo}
                  onChange={e =>
                    setForm({
                      ...form,
                      comparaisonInterLabo: e.target.checked
                    })
                  }
                />
                Comparaison inter-laboratoire
              </label>
              <label className="checkbox">
                <input
                  type="checkbox"
                  checked={form.declarationConformite}
                  onChange={e =>
                    setForm({
                      ...form,
                      declarationConformite: e.target.checked
                    })
                  }
                />
                Déclaration de conformité
              </label>
            </div>

            <div
              className="modal-actions full"
              style={{ marginTop: '20px' }}
            >
              <button
                type="button"
                className="secondary"
                onClick={handleCloseModal}
              >
                Annuler
              </button>
              <button
                className="primary"
                disabled={submitting}
              >
                {submitting
                  ? 'Enregistrement…'
                  : editingItem
                    ? 'Enregistrer les modifications'
                    : 'Créer la demande'}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
