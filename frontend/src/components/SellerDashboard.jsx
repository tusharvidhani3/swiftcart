import { useNavigate } from 'react-router'
import styles from '../styles/SellerDashboard.module.css'
import { useEffect, useState } from 'react'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { useQuery } from '@tanstack/react-query'

async function getDashboardStats(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/dashboard`, {
        method: 'GET'
    })
    return res.json()
}

export default function SellerDashboard() {

    const navigate = useNavigate()
    const authFetch = useAuthFetch()
    // const [ stats, setStats ] = useState({
    //     confirmedOrderItems: 0,
    //     shippedOrderItems: 0,
    //     deliveredOrderItems: 0,
    //     returnedOrderItems: 0,
    //     revenueToday: 0,
    //     dailyOrderStats: [],
    //     productsOutOfStock: 0
    // })

    const { data: stats, isLoading } = useQuery({
        queryKey: ['stats'],
        queryFn: () => getDashboardStats(authFetch),
        staleTime: 1000 * 60 * 5
    })

    return (
        <>
            <div className={`${styles.itemsToShip} ${styles.metricsCard}`} onClick={() => navigate('./orders')}>
                <h2 className={styles.itemsToShipCount}>{stats.confirmedOrderItems}</h2>
                <h3 className={styles.itemsToShipText}>Items yet to be shipped</h3>
            </div>
            <div className={`${styles.itemsToDeliver} ${styles.metricsCard}`} onClick={() => navigate('./orders')}>
                <h2 className={styles.itemsToDeliverCount}>{stats.shippedOrderItems}</h2>
                <h3 className={styles.itemsToDeliverText}>Items yet to be delivered</h3>
            </div>
            <div className={`${styles.outOfStock} ${styles.metricsCard}`}>
                <h2 className={styles.outOfStockCount}>{stats.productsOutOfStock}</h2>
                <h3 className={styles.outOfStockText}>Products Out of Stock</h3>
            </div>
            <div className={`${styles.lowStock} ${styles.metricsCard}`}>
                <h2 className={styles.lowStockCount}>{stats.deliveredOrderItems}</h2>
                <h3 className={styles.lowStockText}>Items Delivered</h3>
            </div>
            <div className={`${styles.totalOrders} ${styles.metricsCard}`}>
                <h2 className={styles.totalOrdersCount}>{stats.returnedOrderItems}</h2>
                <h3 className={styles.totalOrdersText}>Items Returned</h3>
            </div>
            <div className={`${styles.totalRevenue} ${styles.metricsCard}`}>
                <h2 className={styles.totalRevenueAmount}>₹{stats.revenueToday}</h2>
                <h3 className={styles.totalRevenueText}>Total Revenue</h3>
            </div>
        </>
    )
}