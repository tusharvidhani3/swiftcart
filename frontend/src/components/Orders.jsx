import styles from '../styles/Orders.module.css'
import OrderCard from "./OrderCard";
import { useAuthFetch } from "../hooks/useAuthFetch";
import { apiBaseUrl } from "../config";
import { useQuery } from "@tanstack/react-query";
import OrdersSkeleton from './OrdersSkeleton';

async function getOrders(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/orders`, {
        method: "GET"
    })
    return await res.json()
}

export default function Orders() {

    const authFetch = useAuthFetch()

    const { data: ordersPagedModel, isLoading, isError, error } = useQuery({
        queryKey: ['orders'],
        queryFn: () => getOrders(authFetch),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <OrdersSkeleton /> : (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {ordersPagedModel.orders?.map(order => <OrderCard order={order} key={order.id} orders={ordersPagedModel.orders} />)}
            </div>
        </>
    )
}