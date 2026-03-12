import styles from '../styles/SellerOrders.module.css'
import razorpayLogo from '../assets/icons/razorpay-logo.svg'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { Link } from 'react-router'
import { Fragment, useContext, useState } from 'react'
import UIContext from '../contexts/UIContext'
import threeDotsIcon from '../assets/icons/three-dots.svg'

export default function SellerOrderCard({ orderId, placedAt, shippingAddress, totalAmount, orderItems, payment }) {

    const orderDate = new Date(placedAt)
    const { authFetch } = useAuthFetch()
    const { isMobile } = useContext(UIContext)
    const [threeDotsMenuOpen, setThreeDotsMenuOpen] = useState(false)

    async function cancel(orderItemId) {
        const res = await authFetch(`${apiBaseUrl}/api/orders/items/${orderItemId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                orderStatus: 'CANCELLED_BY_SELLER'
            })
        })
        const orderItemResponse = await res.json()
    }

    return (
        <div className={styles.orderCard}>
            <div className={styles.orderInfo}>
                <div className={styles.orderedOn}>
                    {isMobile && <h2>Ordered On</h2>}
                    <div>
                        <p className={styles.orderDate}>{orderDate.toLocaleDateString('en-IN', { timeZone: "Asia/Kolkata" })}</p>
                        <p className={styles.orderTime}>{orderDate.toLocaleTimeString('en-IN', { timeZone: "Asia/Kolkata" })} IST</p>
                    </div>
                </div>

                <div className={styles.orderDetails}>
                    {isMobile ? <div><h2>Order ID</h2><span>#{orderId}</span></div> : <Link to={`/seller/orders/${orderId}`} className={styles.orderId}>Order ID: {orderId}</Link>}
                    <div>{isMobile ? <h2>Payment Method</h2> : <>Payment Method:</>} {payment ? <img className={styles.razorpayLogo} src={razorpayLogo} alt='razorpay' /> : <span className={`${styles.cod}`}>Cash On Delivery</span>}</div>
                </div>
            </div>

            <div className={styles.orderItemContainer}>
                {orderItems.map(orderItem =>
                    <div className={styles.orderItem} key={orderItem.orderItemId}>
                        <div className={styles.productDetails}>
                            <img className={styles.productImage} src={orderItem.product.imageUrls[0]} alt="product image" />
                            <div>
                                <Link to={`/products/${orderItem.product.productId}`} className={styles.productTitle}>{orderItem.product.productName}</Link>
                                <div>Quantity: {orderItem.quantity}</div>
                                <div>Item subtotal: ₹{orderItem.product.price}</div>
                                <div>Order Item ID: {orderItem.orderItemId}</div>
                            </div>
                        </div>

                        <div className={styles.orderStatus}>
                            <span className={`${styles.badge} ${styles.status} ${styles[orderItem.orderItemStatus.toLowerCase()]}`}>{orderItem.orderItemStatus.replaceAll('_', ' ')}</span>
                        </div>

                        {!isMobile && <div className={styles.actions}>
                            {orderItem.orderItemSatus === 'DELIVERED' ? <button className={styles.btnAction} onClick={() => refund(orderItem.orderItemId)}>Refund</button> : !['CANCELLED', 'CANCELLED_BY_SELLER'].includes(orderItem.orderItemStatus) && <button className={styles.btnAction} onClick={() => cancel(orderItem.orderItemId)}>Cancel</button>}
                            {orderItem.orderItemStatus === 'CONFIRMED' ? <button className={styles.btnAction} onClick={() => shipOrderItem(orderItem.orderItemId)}>Mark as Shipped</button> : orderItem.orderItemSatus === 'SHIPPED' && <button className={styles.btnAction}>Mark as Delivered</button>}
                        </div>
                        }
                    </div>
                )
                }
            </div>
        </div>
    )
}