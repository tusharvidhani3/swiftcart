import { useContext, useState } from 'react'
import styles from '../styles/SellerOrders.module.css'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import SellerOrderCard from './SellerOrderCard'
import UIContext from '../contexts/UIContext'
import { useQuery } from '@tanstack/react-query'

async function getOrders(authFetch, page) {
    const res = await authFetch(`${apiBaseUrl}/api/orders/all?page=${page}`, {
        method: 'GET'
    })
    return await res.json()
}

export default function SellerOrders() {

    const authFetch = useAuthFetch()
    const { isMobile } = useContext(UIContext)
    const [page, setPage] = useState(0)

    const { data: ordersPagedModel, isLoading } = useQuery({
        queryKey: ['orders', 'list', page],
        queryFn: () => getOrders(authFetch, page),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <SellerOrdersSkeleton /> : (
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
                {ordersPagedModel.orders?.map(order => <SellerOrderCard {...order} key={order.orderId} />)}
            </div>
        </>
    )
}