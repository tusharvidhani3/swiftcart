import App from './App.jsx'
import Home from './components/Home.jsx'
import Cart from './components/Cart.jsx'
import { createBrowserRouter } from 'react-router'
import ProductDetails from './components/ProductDetails.jsx'
import Orders from './components/Orders.jsx'
import Checkout from './components/Checkout.jsx'
import ManageAddresses from './components/ManageAddresses.jsx'
import Profile from './components/Profile.jsx'
import AuthForm from './components/AuthForm.jsx'
import OrderSummary from './components/OrderSummary.jsx'
import CreateProductListing from './components/CreateProductListing.jsx'
import AddAddress from './components/AddAddress.jsx'

const router = createBrowserRouter([
    {
        path: '/',
        element: <App />,
        children: [
            {
                index: true,
                element: <Home />
            },

            {
                path: 'products',
                children:[
                    {
                        path: ':productId',
                        element: <ProductDetails />,
                        handle: { mainClass: 'productDetails' }
                    },

                    {
                        path: 'add',
                        element: <CreateProductListing />,
                        handle: { mainClass: 'createProductListing' }
                    }
                ]
            },

            {
                path: 'profile',
                element: <Profile />,
                handle: { mainClass: 'profile' }
            },

            {
                path: 'orders',
                element: <Orders />,
                handle: { mainClass: 'orders' },
                children: [
                    {
                        path: 'summary/:orderId',
                        element: <OrderSummary />,
                        handle: { mainClass: 'orderSummary' }
                    }
                ]
            },

            {
                path: 'cart',
                element: <Cart />,
                handle: { mainClass: 'cart' }
            },

            {
                path: 'checkout',
                element: <Checkout />,
                handle: { mainClass: 'checkout' }
            },

            {
                path: 'addresses',
                element: <ManageAddresses />,
                handle: { mainClass: 'manageAddresses' },
                children: [
                    {
                        path: 'add',
                        element: <AddAddress />,
                        handle: { mainClass: 'addAddress' }
                    }
                ]
            },

            {
                path: 'auth',
                children: [
                    {
                        path: 'login',
                        element: <AuthForm mode="login" />,
                        handle: { mainClass: 'authForm' }
                    },

                    {
                        path: 'register',
                        element: <AuthForm mode="register" />,
                        handle: { mainClass: 'authForm' }
                    }
                ]
            },
        ]
    }
])

export default router