import App from './App.jsx'
import Home from './components/Home.jsx'
import Cart from './components/Cart.jsx'
import { createBrowserRouter, Outlet } from 'react-router'
import ProductDetails from './components/ProductDetails.jsx'
import Orders from './components/Orders.jsx'
import Checkout from './components/Checkout.jsx'
import ManageAddresses from './components/ManageAddresses.jsx'
import Profile from './components/Profile.jsx'
import AuthForm from './components/AuthForm.jsx'
import CreateProductListing from './components/CreateProductListing.jsx'
import AddressForm from './components/AddressForm.jsx'
import { AddressesProvider } from './contexts/AddressesContext.jsx'
import OrderDetails from './components/OrderDetails.jsx'
import { UserProvider } from './contexts/UserContext.jsx'

const router = createBrowserRouter([
    {
        path: '/',
        element: (<UserProvider>
            <App />
        </UserProvider>),
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
                children: [
                    {
                        path: '',
                        element: <Orders />,
                        handle: { mainClass: 'orders' }
                    },

                    {
                        path: ':orderId',
                        element: <OrderDetails />,
                        handle: { mainClass: 'orderDetails' }
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
                element: (<AddressesProvider>
                    <Checkout />
                </AddressesProvider>),
                handle: { mainClass: 'checkout' }
            },

            {
                path: 'addresses',
                element: (<AddressesProvider>
                    <Outlet />
                </AddressesProvider>),
                children: [
                    {
                        path: '',
                        element: <ManageAddresses />,
                        handle: { mainClass: 'manageAddresses' }
                    },

                    {
                        path: 'select',
                        element: <ManageAddresses isSelectMode={true} />,
                        handle: { mainClass: 'manageAddresses' }
                    },

                    {
                        path: 'add',
                        element: <AddressForm />,
                        handle: { mainClass: 'addAddress' }
                    },

                    {
                        path: 'edit',
                        element: <AddressForm isEditMode={true} />,
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

            // {
            //     path: 'seller',
            //     element:
            // }
        ]
    }
])

export default router