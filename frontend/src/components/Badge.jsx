export function Badge({ state }) {
  const stateLabels = {
    NOUVEAU: 'Nouveau',
    DISPONIBLE: 'Disponible',
    EN_UTILISATION: 'En utilisation',
    EN_ETALONNAGE: 'Étalonnage',
    EN_MAINTENANCE: 'Maintenance',
    EN_PANNE: 'En panne',
    HORS_SERVICE: 'Hors service',
    REFORME: 'Réformé'
  }
  return (
    <span className={`badge ${state?.toLowerCase().replaceAll('_', '-')}`}>
      {stateLabels[state] || state}
    </span>
  )
}
