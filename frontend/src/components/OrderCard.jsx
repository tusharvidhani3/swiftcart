import OrderItemCard from "./OrderItemCard"
import styles from '../styles/Orders.module.css'
import { useNavigate } from "react-router"
import { formatPaiseToRupees } from "../utils/currency"
import { Loader2 } from "lucide-react"

export default function OrderCard({ order }) {

    const navigate = useNavigate()
    const { id, totalAmount, placedAt, items } = order
    const orderDate = new Date(placedAt)

    return items ? (
        <div className={styles.orderCard} onClick={() => navigate(`./${id}`)}>
            <div className={styles.orderDetails}>
                <div className={`${styles.orderPlaced} ${styles.orderField}`}><span>Order Placed</span> <span className={styles.placedOn}>{orderDate.getDate()} {orderDate.toLocaleString('default', { month: 'long' })} {orderDate.getFullYear()}</span></div>
                <div className={`${styles.orderTotal} ${styles.orderField}`}><span>Total</span> <span className={styles.totalAmount}>{formatPaiseToRupees(totalAmount)}</span></div>
                <div className={`${styles.orderId} ${styles.orderField}`}><span>Order #</span> <span className={styles.orderId}>{id}</span></div>
            </div>
            <div className={styles.orderItemsContainer}>
                {items.map(orderItem => <OrderItemCard {...orderItem} key={orderItem.id} />)}
            </div>
        </div>
    ) : <Loader2 className="animate-spin" />
}