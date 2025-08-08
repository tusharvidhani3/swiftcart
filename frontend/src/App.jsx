import './App.css'
import { useMatches } from 'react-router'
import CustomerApp from './components/CustomerApp'
import SellerApp from './components/SellerApp';
import { useContext } from 'react';
import UserContext from './contexts/UserContext';
import { UIProvider } from './contexts/UIContext';

function App() {

  const { userInfo } = useContext(UserContext)
  console.log(userInfo)
  const matches = useMatches();
  const mainClassKey = matches.find(m => m.handle?.mainClass)?.handle.mainClass || '';
  const mainClass = `${mainClassKey && mainClassKey}`;

  return (
    <UIProvider>
      {
        userInfo?.role === 'ROLE_SELLER' ? <SellerApp mainClass={mainClass} /> : <CustomerApp mainClass={mainClass} />
      }
    </UIProvider>
  )
}

export default App
