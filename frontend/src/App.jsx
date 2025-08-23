import './App.css'
import { useMatches } from 'react-router'
import CustomerApp from './components/CustomerApp'
import SellerApp from './components/SellerApp';
import { Suspense, useContext } from 'react';
import UserContext from './contexts/UserContext';
import { UIProvider } from './contexts/UIContext';
import loadingGif from './assets/images/loading.gif'

function App() {

  const { userInfo } = useContext(UserContext)
  const matches = useMatches();
  const mainClassKey = matches.find(m => m.handle?.mainClass)?.handle.mainClass || '';
  const mainClass = `${mainClassKey && mainClassKey}`;

  return (
    <Suspense fallback={<img src={loadingGif} alt='Loading...' />}>
      <UIProvider>
        {
          userInfo?.role === 'ROLE_SELLER' ? <SellerApp mainClass={mainClass} /> : <CustomerApp mainClass={mainClass} />
        }
      </UIProvider>
    </Suspense>
  )
}

export default App
