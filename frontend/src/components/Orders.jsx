import { useState } from "react";
import OrderItemCard from "./OrderItemCard";
import styles from '../styles/Orders.module.css'

export default function Orders() {

    const [orders, setOrders] = useState(null)

    async function fetchOrderItems() {
        const res = await fetch("http://localhost:8080/api/orders", {
            method: "GET",
            credentials: "include"
        })
        const ordersPage = await res.json()
        
    }

    return (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {<OrderItemCard />}
            </div>
        </>
    )
}