import './App.css'
import Header from './components/Header'
import { Outlet } from 'react-router'
import { UserProvider } from './contexts/UserContext'
import { ToastProvider } from './contexts/ToastContext'

function App() {

  return (
    <UserProvider>
      <ToastProvider>
      <Header />
      <main>
        <Outlet />
      </main>
      </ToastProvider>
    </UserProvider>
  )
}

export default App
