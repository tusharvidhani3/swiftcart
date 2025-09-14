import { useNavigate } from 'react-router'
import styles from '../styles/SellerDashboard.module.css'
import { useEffect, useState } from 'react'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'

export default function SellerDashboard() {

    const navigate = useNavigate()
    const { authFetch } = useAuthFetch()
    const [ metrics, setMetrics ] = useState({
        itemsToShip: 0,
        itemsToDeliver: 0,
        outOfStockProducts: 0,
        lowStockProducts: 0,
        totalOrders: 0,
        totalRevenue: 0,
        shippedItems: 0,
        productsDelivered: 0
    })

    async function getMetrics() {
        const res = await authFetch(`${apiBaseUrl}/api/metrics`, {
            method: 'GET'
        })
        const metric = await res.json()
        setMetrics(metric)
    }

    useEffect(() => {
        // const init = async () => await getMetrics()
        // init()
    }, [])

    return (
        <>
        <div className={`${styles.itemsToShip} ${styles.metricsCard}`} onClick={() => navigate('./orders')}>
            <h2 className={styles.itemsToShipCount}>{metrics.itemsToShip}</h2>
            <h3 className={styles.itemsToShipText}>Items yet to be shipped</h3>
        </div>
        <div className={`${styles.itemsToDeliver} ${styles.metricsCard}`} onClick={() => navigate('./orders')}>
            <h2 className={styles.itemsToDeliverCount}>{metrics.itemsToDeliver}</h2>
            <h3 className={styles.itemsToDeliverText}>Items yet to be delivered</h3>
        </div>
        <div className={`${styles.outOfStock} ${styles.metricsCard}`}>
            <h2 className={styles.outOfStockCount}>{metrics.outOfStockProducts}</h2>
            <h3 className={styles.outOfStockText}>Products Out of Stock</h3>
        </div>
        <div className={`${styles.lowStock} ${styles.metricsCard}`}>
            <h2 className={styles.lowStockCount}>{metrics.lowStockProducts}</h2>
            <h3 className={styles.lowStockText}>Products Low in Stock</h3>
        </div>
        <div className={`${styles.totalOrders} ${styles.metricsCard}`}>
            <h2 className={styles.totalOrdersCount}>{metrics.totalOrders}</h2>
            <h3 className={styles.totalOrdersText}>Total Orders</h3>
        </div>
        <div className={`${styles.totalRevenue} ${styles.metricsCard}`}>
            <h2 className={styles.totalRevenueAmount}>₹{metrics.totalRevenue}</h2>
            <h3 className={styles.totalRevenueText}>Total Revenue</h3>
        </div>
        <div className={`${styles.productsDelivered} ${styles.metricsCard}`}>
            <h2 className={styles.productsDeliveredCount}>{metrics.productsDelivered}</h2>
            <h3 className={styles.productsDeliveredText}>Products Delivered</h3>
        </div>
        </>
    )
}