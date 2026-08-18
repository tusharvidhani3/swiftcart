import OrderCardSkeleton from "./OrderCardSkeleton";
import { Skeleton } from "./ui/skeleton";
import styles from '../styles/Orders.module.css'

export default function OrdersSkeleton() {

    return (
        <>
            <Skeleton />
            <div className={styles.ordersContainer}>
                {new Array(10).fill(null).map((_, i) => <OrderCardSkeleton key={i} />)}
            </div>
        </>
    )
}