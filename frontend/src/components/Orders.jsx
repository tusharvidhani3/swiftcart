import styles from '../styles/Orders.module.css'
import OrderCard from "./OrderCard";
import { useQuery } from "@tanstack/react-query";
import OrdersSkeleton from './OrdersSkeleton';
import { api } from '@/api/client';

export default function Orders() {

    const { data: ordersPagedModel, isLoading, isError, error } = useQuery({
        queryKey: ['orders'],
        queryFn: () => api.get('/orders'),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <OrdersSkeleton /> : (
        <>
            <h2 className={styles.yourOrders}>Your Orders</h2>
            <div className={styles.ordersContainer}>
                {ordersPagedModel._embedded?.ordersResponseList.map(order => <OrderCard order={order} key={order.id} orders={ordersPagedModel.orders} />)}
            </div>
        </>
    )
}