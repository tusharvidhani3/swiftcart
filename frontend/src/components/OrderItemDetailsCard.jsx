import { useNavigate } from 'react-router'
import styles from '../styles/OrderDetails.module.css'
import { generateStatus } from './OrderItemCard'
import { formatPaiseToRupees } from '../utils/currency'

export default function OrderItemDetailsCard({ id, product, orderItemStatus, deliveryAt, quantity, setOrders }) {

    const navigate = useNavigate()

    return (
        <div className={styles.orderItem}>
            <div className={styles.orderStatus}>{generateStatus(orderItemStatus, deliveryAt)}</div>
            <div className={styles.orderItemInfo}>
                <div className={styles.productDetails} onClick={() => navigate(`/products/${product.id}`)}>
                    <img src={product.imageUrls[0]} alt="product image preview" />
                    <span className={styles.productQty}>{product.quantity}</span>
                    <div className={styles.productInfo}>
                        <div className={styles.productTitle}>{product.name}</div>
                        <p className={styles.itemTotal}>₹{(formatPaiseToRupees(product.price) * quantity)}</p>
                        <p className={styles.status}></p>
                    </div>
                </div>
                <div className={styles.itemActions}>
                    <button className={styles.btnTrack}>Track package</button>
                    {['DELIVERED', 'RETURNED', 'REFUNDED'].includes(orderItemStatus) && <button className={styles.btnReview}>Review</button>}
                </div>
            </div>
        </div>
    )
}