import { useContext, useEffect, useState } from 'react'
import styles from '../styles/SellerOrders.module.css'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import SellerOrderCard from './SellerOrderCard'
import loadingGif from '../assets/images/loading.gif'
import UIContext from '../contexts/UIContext'

export default function SellerOrders() {

    const { authFetch } = useAuthFetch()
    const [orders, setOrders] = useState(null)
    const { isMobile } = useContext(UIContext)

    async function getOrders() {
        const res = await authFetch(`${apiBaseUrl}/api/orders/all`, {
            method: 'GET'
        })
        const ordersPage = await res.json()
        setOrders(ordersPage.content)
    }

    useEffect(() => {
        const init = async () => await getOrders()
        init()
    }, [])

    return orders ? (
        <>
        <h1 className={styles.ordersTitle}>Orders</h1>
        <div className={styles.orders}>
                <h2>Ordered On</h2>
                <h2>Order Details</h2>
                <h2>Product Details</h2>
                <h2>OrderStatus</h2>
                {!isMobile && <h2>Action</h2>}
            {orders.map(order => <SellerOrderCard {...order} key={order.orderId} />)}
        </div>
        </>
    ) : <img className='loadingGif' src={loadingGif} alt="Loading..." />
}