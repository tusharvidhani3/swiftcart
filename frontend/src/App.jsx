import './App.css'
import Header from './components/Header'
import { Outlet, useMatches } from 'react-router'
import { UserProvider } from './contexts/UserContext'
import { ToastProvider } from './contexts/ToastContext'
import { useState } from 'react'

function App() {

  const [keyword, setKeyword] = useState("")

  const matches = useMatches();
  const mainClassKey = matches.find(m => m.handle?.mainClass)?.handle.mainClass || '';
  const mainClass = `${mainClassKey && mainClassKey}`;

  return (
    <UserProvider>
      <ToastProvider>
      <Header keyword={keyword} setKeyword={setKeyword} />
      <main className={mainClass}>
        <Outlet context={{keyword}} />
      </main>
      </ToastProvider>
    </UserProvider>
  )
}

export default App
