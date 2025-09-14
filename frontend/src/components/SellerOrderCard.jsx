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
    const [ threeDotsMenuOpen, setThreeDotsMenuOpen ] = useState(false)

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
        console.log(orderItemResponse)
    }

    return (
        <>
            <div className={styles.orderedOn}>
                <p className={styles.orderDate}>{orderDate.toLocaleDateString('en-IN', { timeZone: "Asia/Kolkata" })}</p>
                <p className={styles.orderTime}>{orderDate.toLocaleTimeString('en-IN', { timeZone: "Asia/Kolkata" })} IST</p>
            </div>

            <div className={styles.orderDetails}>
                <Link to={`/seller/orders/${orderId}`}>{orderId}</Link>
                <div>Payment Method: {payment ? <img className={styles.razorpayLogo} src={razorpayLogo} alt='razorpay' /> : <span className={`${styles.badge} ${styles.cod}`}>Cash On Delivery</span>}</div>
            </div>

            {orderItems.map(orderItem =>
                <div key={orderItem.orderItemId}>
                    <div className={styles.productDetails}>
                        <img className={styles.productImage} src={orderItem.product.imageUrls[0]} alt="product image" />
                        <div>
                            <Link to={`/products/${orderItem.product.productId}`}>{orderItem.product.productName}</Link>
                            <div>Quantity: {orderItem.quantity}</div>
                            <div>Item subtotal: ₹{orderItem.product.price}</div>
                            <div>Order Item ID: {orderItem.orderItemId}</div>
                        </div>
                    </div>

                    <div className={styles.orderStatus}>
                        <span className={`${styles.badge} ${styles.status}`}>{orderItem.orderItemStatus}</span>
                    </div>

                    {isMobile && <button className={styles.btnThreeDots} onClick={() => setThreeDotsMenuOpen(true)}><img src={threeDotsIcon} alt="three dots" /></button>}
                    <div className={styles.actions}>
                        {orderItem.orderItemSatus === 'DELIVERED' ? <button className={styles.btnAction} onClick={() => refund(orderItem.orderItemId)}>Refund</button> : !['CANCELLED', 'CANCELLED_BY_SELLER'].includes(orderItem.orderItemStatus) && <button className={styles.btnAction} onClick={() => cancel(orderItem.orderItemId)}>Cancel</button>}
                        {orderItem.orderItemStatus === 'CONFIRMED' ? <button className={styles.btnAction} onClick={() => shipOrderItem(orderItem.orderItemId)}>Mark as Shipped</button> : orderItem.orderItemSatus === 'SHIPPED' && <button className={styles.btnAction}>Mark as Delivered</button>}
                    </div >
                </div>
            )
            }
        </>
    )
}