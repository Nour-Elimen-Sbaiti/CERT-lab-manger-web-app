import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { api } from './api'
import { Login, Register, ForgotPassword } from './pages/AuthPages'
import { AppShell } from './components/AppShell'

function App() {
  const [auth, setAuth] = useState(() => {
    try {
      const item = localStorage.getItem('cert-auth')
      if (!item) return null
      const parsed = JSON.parse(item)
      return parsed && typeof parsed === 'object' && parsed.token ? parsed : null
    } catch {
      return null
    }
  })

  const [page, setPage] = useState('dashboard')
  const [authScreen, setAuthScreen] = useState('login')
  const [showProfile, setShowProfile] = useState(false)
  const [alertCount, setAlertCount] = useState(0)

  useEffect(() => {
    if (auth?.token) {
      api('/api/alertes?jours=30', { token: auth.token })
        .then(alerts =>
          setAlertCount(Array.isArray(alerts) ? alerts.length : 0)
        )
        .catch(() => {})
    }
  }, [auth, page])

  function login(data) {
    localStorage.setItem('cert-auth', JSON.stringify(data))
    setAuth(data)
  }

  function logout() {
    localStorage.removeItem('cert-auth')
    setAuth(null)
  }

  if (!auth?.token) {
    if (authScreen === 'register')
      return <Register onGoToLogin={() => setAuthScreen('login')} />
    if (authScreen === 'forgot-password')
      return <ForgotPassword onGoToLogin={() => setAuthScreen('login')} />
    return (
      <Login
        onLogin={login}
        onGoToRegister={() => setAuthScreen('register')}
        onGoToForgotPassword={() => setAuthScreen('forgot-password')}
      />
    )
  }

  // Pages authentifiées
  const dashboard = React.lazy(() => import('./pages/Dashboard').then(m => ({ default: m.Dashboard })))
  const equipements = React.lazy(() => import('./pages/Equipments').then(m => ({ default: m.Equipments })))
  const alertes = React.lazy(() => import('./pages/Alertes').then(m => ({ default: m.Alerts })))
  const laboratoires = React.lazy(() => import('./pages/ResourceList').then(m => ({ default: m.ResourceList })))
  const personnel = React.lazy(() => import('./pages/ResourceList').then(m => ({ default: m.ResourceList })))
  const utilisateurs = React.lazy(() => import('./pages/Users').then(m => ({ default: m.Users })))
  const demandes = React.lazy(() => import('./pages/DemandesPrestation').then(m => ({ default: m.DemandesPrestation })))
  const ProfileModal = React.lazy(() => import('./pages/ProfileModal').then(m => ({ default: m.ProfileModal })))

  const PageWrapper = ({ component: Component, token, ...rest }) => (
    <React.Suspense fallback={<div className="loader">Chargement…</div>}>
      <Component token={token} {...rest} />
    </React.Suspense>
  )

  const view =
    page === 'dashboard' && <PageWrapper component={dashboard} token={auth.token} username={auth.username} setPage={setPage} />
    page === 'equipements' && <PageWrapper component={equipements} token={auth.token} />
    page === 'alertes' && <PageWrapper component={alertes} token={auth.token} />
    page === 'laboratoires' && <PageWrapper component={laboratoires} token={auth.token} kind="laboratoires" />
    page === 'personnel' && <PageWrapper component={personnel} token={auth.token} kind="personnel" />
    page === 'utilisateurs' && <PageWrapper component={utilisateurs} token={auth.token} />
    page === 'demandes' && <PageWrapper component={demandes} token={auth.token} />
  }

  return (
    <>
      {showProfile && (
        <React.Suspense fallback={<div className="loader">Chargement…</div>}>
          <ProfileModal token={auth.token} onClose={() => setShowProfile(false)} onLogout={logout} />
        </React.Suspense>
      )}
      <AppShell
        user={auth}
        page={page}
        setPage={setPage}
        onLogout={logout}
        onAvatarClick={() => setShowProfile(true)}
        alertCount={alertCount}
      >
        {view}
      </AppShell>
    </>
  )
}

createRoot(document.getElementById('root')).render(<App />)
