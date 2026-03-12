import { useEffect, useState } from "react";
import styles from '../styles/Orders.module.css'
import OrderCard from "./OrderCard";
import { useAuthFetch } from "../hooks/useAuthFetch";
import { apiBaseUrl } from "../config";
import { Loader2 } from "lucide-react";

export default function Orders() {

    const [ordersPagedModel, setOrdersPagedModel] = useState(null)
    const { authFetch } = useAuthFetch()

    async function getOrders() {
        const res = await authFetch(`${apiBaseUrl}/api/orders`, {
            method: "GET"
        })
        const pagedModel = await res.json()
        setOrdersPagedModel(pagedModel)
    }

    useEffect(() => {
        const loadOrders = async () => await getOrders()
        loadOrders()
    }, [])

    return ordersPagedModel ? (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {ordersPagedModel.orders.map(order => <OrderCard order={order} key={order.id} orders={ordersPagedModel.orders} />)}
            </div>
        </>
    ): <Loader2 className="animate-spin" />
}