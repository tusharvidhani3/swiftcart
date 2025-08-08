import { useNavigate } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'
import styles from '../styles/OrderDetails.module.css'
import { generateStatus } from './OrderItemCard'

export default function OrderItemDetailsCard({ orderItemId, product, orderItemStatus, deliveryAt, quantity, orders, setOrders }) {

    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()

    async function cancelOrderItem() {
        
        const res = await authFetch(`http://localhost:8080/api/orders/items/${orderItemId}/cancel`, {
            method: 'PATCH',
            credentials: 'include'
        })

        if(res.ok) {
            const cancelledOrderItem = await res.json()
            setOrders(orders => orders.map(order => {
                if(order.orderId === cancelledOrderItem.orderId) {
                    return cancelOrderItem
                }
            }))
        }
    }

    return (
        <div className={styles.orderItem}>
            <div className={styles.orderStatus}>{generateStatus(orderItemStatus, deliveryAt)}</div>
            <div className={styles.orderItemInfo}>
            <div className={styles.productDetails} onClick={() => navigate(`/products/${product.productId}`)}>
                <img src={product.imageUrls[0]} alt="product image preview" />
                <span className={styles.productQty}>{product.quantity}</span>
                <div className={styles.productInfo}>
                    <div><span className={styles.productTitle}>{product.productName}</span></div>
                    <p className={styles.itemTotal}>₹{(product.price * quantity).toLocaleString('en-IN')}</p>
                    <p className={styles.status}></p>
                </div>
            </div>
            <div className={styles.itemActions}>
                {['PENDING', 'PROCESSING', 'SHIPPED'].includes(orderItemStatus) && <button className={styles.btnCancel} onClick={cancelOrderItem}>Cancel</button>}
                {orderItemStatus==='DELIVERED' ? <button className={styles.btnReturn}>Return / Replace</button> : <button className={styles.btnTrack}>Track package</button>}
                {['DELIVERED', 'RETURNED', 'REFUNDED'].includes(orderItemStatus) && <button className={styles.btnReview}>Review</button>}
            </div>
            </div>
        </div>
    )
}