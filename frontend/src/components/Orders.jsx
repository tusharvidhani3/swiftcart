import { useEffect, useState } from "react";
import OrderItemCard from "./OrderItemCard";
import styles from '../styles/Orders.module.css'
import OrderCard from "./OrderCard";
import { useAuthFetch } from "../hooks/useAuthFetch";

export default function Orders() {

    const [orders, setOrders] = useState([])
    const { authFetch } = useAuthFetch()

    async function getOrders() {
        const res = await authFetch("http://localhost:8080/api/orders", {
            method: "GET"
        })
        const ordersPage = await res.json()
        setOrders(ordersPage.content)
    }

    useEffect(() => {
        const loadOrders = async () => await getOrders()
        loadOrders()
    }, [])

    console.log(orders)
    return orders.length ? (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {orders.map(order => <OrderCard order={order} key={order.orderId} />)}
            </div>
        </>
    ):"Empty Orders"
}