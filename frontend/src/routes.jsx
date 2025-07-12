import App from './App.jsx'
import Home from './components/Home.jsx'
import Cart from './components/Cart.jsx'
import { createBrowserRouter } from 'react-router'

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                index: true,
                element: <Home />
            },

            // {
            //     path: '/profile',
            //     element: <Profile />
            // },

            // {
            //     path: '/orders',
            //     element: <Orders />
            // },

            {
                path: '/cart',
                element: <Cart />
            }

            // {
            //     path: '/addresses',
            //     element: <Addresses />
            // },

            // {
            //     path: '/login',
            //     element: <Login />
            // }
        ]
    }
])

export default router