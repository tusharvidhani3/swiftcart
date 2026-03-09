import { useContext, useEffect, useState } from 'react'
import styles from '../styles/SellerOrders.module.css'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import SellerOrderCard from './SellerOrderCard'
import UIContext from '../contexts/UIContext'
import { Loader2 } from 'lucide-react'

export default function SellerOrders() {

    const { authFetch } = useAuthFetch()
    const [ordersPagedModel, setOrdersPagedModel] = useState(null)
    const { isMobile } = useContext(UIContext)

    async function getOrders() {
        const res = await authFetch(`${apiBaseUrl}/api/orders/all`, {
            method: 'GET'
        })
        const pagedModel = await res.json()
        setOrdersPagedModel(pagedModel)
    }

    useEffect(() => {
        const init = async () => await getOrders()
        init()
    }, [])

    return ordersPagedModel ? (
        <>
            <h1 className={styles.ordersTitle}>Orders</h1>
            <div className={styles.orders}>
                {!isMobile && <div className={styles.orderHeader}>
                    <div className={styles.orderInfoTitle}>
                        <h2>Ordered On</h2>
                        <h2>Order Details</h2>
                    </div>
                    <div className={styles.orderItemTitle}>
                        <h2>Product Details</h2>
                        <h2>OrderStatus</h2>
                        <h2>Action</h2>
                    </div>
                </div>}
                {ordersPagedModel.orders.map(order => <SellerOrderCard {...order} key={order.orderId} />)}
            </div>
        </>
    ) : <Loader2 className='animate-spin' />
}