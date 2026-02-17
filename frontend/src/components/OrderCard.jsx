import OrderItemCard from "./OrderItemCard"
import styles from '../styles/Orders.module.css'
import { useNavigate } from "react-router"

export default function OrderCard({ order }) {

    const navigate = useNavigate()
    const { id, shippingAddress, totalAmount, placedAt, orderItems, payment } = order
    const orderDate = new Date(placedAt)

    return orderItems ? (
        <div className={styles.orderCard} onClick={() => navigate(`./${id}`)}>
            <div className={styles.orderDetails}>
                <div className={`${styles.orderPlaced} ${styles.orderField}`}><span>Order Placed</span> <span className={styles.placedOn}>{orderDate.getDate()} {orderDate.toLocaleString('default', { month: 'long' })} {orderDate.getFullYear()}</span></div>
                <div className={`${styles.orderTotal} ${styles.orderField}`}><span>Total</span> <span className={styles.totalAmount}>₹{totalAmount.toLocaleString('en-IN')}</span></div>
                <div className={`${styles.orderId} ${styles.orderField}`}><span>Order #</span> <span className={styles.orderId}>{id}</span></div>
            </div>
            <div className={styles.orderItemsContainer}>
                {orderItems.map(orderItem => <OrderItemCard {...orderItem} key={orderItem.id} />)}
            </div>
        </div>
    ):"loading"
}