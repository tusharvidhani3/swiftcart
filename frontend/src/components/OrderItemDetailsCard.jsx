import { useNavigate } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'
import styles from '../styles/OrderDetails.module.css'
import { generateStatus } from './OrderItemCard'
import { apiBaseUrl } from '../config'

export default function OrderItemDetailsCard({ id, product, orderItemStatus, deliveryAt, quantity, setOrders }) {

    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()

    async function cancelOrderItem() {
        
        const res = await authFetch(`${apiBaseUrl}/api/orders/items/${id}/cancel`, {
            method: 'PATCH',
            credentials: 'include'
        })

        if(res.ok) {
            const cancelledOrderItem = await res.json()
            setOrders(orders => orders.map(order => {
                if(order.id === cancelledOrderItem.order.id) {
                    const modifiedOrder = {...order}
                    modifiedOrder.items.map(orderItem => {
                        if(orderItem.id === cancelledOrderItem.id) {
                            return cancelledOrderItem
                        }
                        return orderItem
                    })
                    return modifiedOrder
                }
                return order
            }))
        }
    }

    return (
        <div className={styles.orderItem}>
            <div className={styles.orderStatus}>{generateStatus(orderItemStatus, deliveryAt)}</div>
            <div className={styles.orderItemInfo}>
                <div className={styles.productDetails} onClick={() => navigate(`/products/${product.id}`)}>
                    <img src={product.imageUrls[0]} alt="product image preview" />
                    <span className={styles.productQty}>{product.quantity}</span>
                    <div className={styles.productInfo}>
                        <div className={styles.productTitle}>{product.name}</div>
                        <p className={styles.itemTotal}>₹{(product.price * quantity).toLocaleString('en-IN')}</p>
                        <p className={styles.status}></p>
                    </div>
                </div>
                <div className={styles.itemActions}>
                    {['CONFIRMED', 'SHIPPED'].includes(orderItemStatus) && <button className={styles.btnCancel} onClick={cancelOrderItem}>Cancel</button>}
                    {orderItemStatus === 'DELIVERED' ? <button className={styles.btnReturn}>Return / Replace</button> : <button className={styles.btnTrack}>Track package</button>}
                    {['DELIVERED', 'RETURNED', 'REFUNDED'].includes(orderItemStatus) && <button className={styles.btnReview}>Review</button>}
                </div>
            </div>
        </div>
    )
}