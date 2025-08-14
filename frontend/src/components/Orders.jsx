import { useEffect, useState } from "react";
import styles from '../styles/Orders.module.css'
import OrderCard from "./OrderCard";
import { useAuthFetch } from "../hooks/useAuthFetch";
import { apiBaseUrl } from "../config";

export default function Orders() {

    const [orders, setOrders] = useState([])
    const { authFetch } = useAuthFetch()

    async function getOrders() {
        const res = await authFetch(`${apiBaseUrl}/api/orders`, {
            method: "GET"
        })
        const ordersPage = await res.json()
        setOrders(ordersPage.content)
    }

    useEffect(() => {
        const loadOrders = async () => await getOrders()
        loadOrders()
    }, [])

    return orders.length ? (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {orders.map(order => <OrderCard order={order} key={order.orderId} orders={orders} />)}
            </div>
        </>
    ):"Empty Orders"
}