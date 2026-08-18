import './App.css'
import { Suspense } from 'react';
import { UIProvider } from './contexts/UIContext';
import { Loader2 } from 'lucide-react';
import { queryClient } from './api/client';
import AppContent from './components/AppContent';
import { UserProvider } from './contexts/UserContext';
import { QueryClientProvider } from '@tanstack/react-query';

function App() {

  return (
      <UIProvider>
        <QueryClientProvider client={queryClient}>
          <UserProvider>
            <Suspense fallback={<div className='flex h-full w-full items-center justify-center'><Loader2 className='animate-spin' /></div>}>
              <AppContent />
            </Suspense>
          </UserProvider>
        </QueryClientProvider>
      </UIProvider>
  )
}

export default App
