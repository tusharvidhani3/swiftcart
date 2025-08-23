import App from './App.jsx'
import Home from './components/Home.jsx'
import { createBrowserRouter, Outlet } from 'react-router'
import { UserProvider } from './contexts/UserContext.jsx'
import { lazy } from 'react'

const Cart = lazy(() => import('./components/Cart.jsx'))
const ProductDetails = lazy(() => import('./components/ProductDetails.jsx'))
const Orders = lazy(() => import('./components/Orders.jsx'))
const Checkout = lazy(() => import('./components/Checkout.jsx'))
const ManageAddresses = lazy(() => import('./components/ManageAddresses.jsx'))
const Profile = lazy(() => import('./components/Profile.jsx'))
const AuthForm = lazy(() => import('./components/AuthForm.jsx'))
const AddressForm = lazy(() => import('./components/AddressForm.jsx'))
const OrderDetails = lazy(() => import('./components/OrderDetails.jsx'))
const CreateProductListing = lazy(() => import('./components/CreateProductListing.jsx'))
const AddressesProvider = lazy(() => import('./contexts/AddressesContext.jsx').then(module => ({default: module.AddressesProvider})))

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