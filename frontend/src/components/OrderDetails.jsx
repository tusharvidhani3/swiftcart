import { useParams } from 'react-router'
import styles from '../styles/OrderDetails.module.css'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { useEffect, useState } from 'react'
import OrderItemDetailsCard from './OrderItemDetailsCard'
import useMediaQuery from '../hooks/useMediaQuery'
import { apiBaseUrl } from '../config'
import razorpayLogo from '../assets/icons/razorpay-logo.svg'

export default function OrderDetails() {

    const [order, setOrder] = useState(null)
    const { orderId } = useParams()
    const { authFetch } = useAuthFetch()
    const isDesktop = useMediaQuery('(min-width: 1080px)')

    async function getOrder() {
        const res = await authFetch(`${apiBaseUrl}/api/orders/${orderId}`, {
            method: 'GET',
        })
        const orderResponse = await res.json()
        setOrder(orderResponse)
    }

    useEffect(() => {
        getOrder()
    }, [])

    let orderDate = null
    if (order) {
        orderDate = new Date(order.placedAt)
    }

    return order ? (
        isDesktop ?
            <>
                <h1 className={styles.orderDetailsTitle}>Order Details</h1>
                <div className={styles.orderData}>
                    <div><span>Order placed</span> <span className={styles.orderDate}>{orderDate.getDate()} {orderDate.toLocaleString('default', { month: 'long' })} {orderDate.getFullYear()}</span></div>
                    |
                    <div><span>Order Id</span> <span className={styles.orderId}>{order.orderId}</span></div>
                </div>

                <div className={styles.orderInfo}>
                    <section className={styles.section}>
                        <h2>Ship to</h2>
                        <div className={styles.shipTo}>
                            {order.shippingAddress.name}<br />{order.shippingAddress.addressLine1}<br />{order.shippingAddress.addressLine2}<br />{order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.pincode}
                        </div>
                    </section>

                    <section className={styles.section}>
                        <h2>Payment Method</h2>
                        <div className={styles.paymentMethod}>
                            {order.payment ? <img className={styles.razorpayLogo} src={razorpayLogo} /> : 'Cash On Delivery'}
                        </div>
                    </section>

                    <section className={styles.section}>
                        <h2>Order Summary</h2>
                        <div className={styles.orderSummary}>
                            <div><span>Items</span> <span className={styles.itemsTotal}>₹{order.totalAmount.toLocaleString('en-IN')}</span></div>
                            <div><span>Delivery</span><span className={styles.deliveryCharge}>FREE DELIVERY</span></div>
                            <div className={styles.orderSummaryTotal}><span>Order total</span> <span className={styles.orderTotal}>₹{order.totalAmount.toLocaleString('en-IN')}</span></div>
                        </div>
                    </section>
                </div>

                <section className={styles.section}>
                    <div className={styles.productsOrdered}>
                        {order.orderItems.map(orderItem => <OrderItemDetailsCard {...orderItem} key={orderItem.orderItemId} />)}
                    </div>
                </section>
            </>
            :
            <>
                <section className={styles.section}>
                    <h2>Order Details</h2>
                    <div className={styles.orderData}>
                        <div><span>Order Id</span> <span className={styles.orderId}>{order.orderId}</span></div>
                        <div><span>Order placed</span> <span className={styles.orderDate}>{orderDate.getDate()} {orderDate.toLocaleString('default', { month: 'long' })} {orderDate.getFullYear()}</span></div>
                        <div><span>Order total</span> <span className={styles.orderTotal}>₹{order.totalAmount.toLocaleString('en-IN')}</span></div>
                    </div>
                </section>

                <section className={styles.section}>
                    <h2>Products in Order</h2>
                    <div className={styles.productsOrdered}>
                        {order.orderItems.map(orderItem => <OrderItemDetailsCard {...orderItem} key={orderItem.orderItemId} />)}
                    </div>
                </section>

                <section className={styles.section}>
                    <h2>Payment Method</h2>
                    <div className={styles.paymentMethod}>
                        {order.payment ? <img className={styles.razorpayLogo} src={razorpayLogo} /> : 'Cash On Delivery'}
                    </div>
                </section>

                <section className={styles.section}>
                    <h2>Ship to</h2>
                    <div className={styles.shipTo}>
                        {order.shippingAddress.name}<br />{order.shippingAddress.addressLine1}<br />{order.shippingAddress.addressLine2}<br />{order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.pincode}
                    </div>
                </section>

                <section className={styles.section}>
                    <h2>Order Summary</h2>
                    <div className={styles.orderSummary}>
                        <div><span>Items</span> <span className={styles.itemsTotal}>₹{order.totalAmount.toLocaleString('en-IN')}</span></div>
                        <div><span>Delivery</span><span className={styles.deliveryCharge}>FREE DELIVERY</span></div>
                        <div className={styles.orderSummaryTotal}><span>Order total</span> <span className={styles.orderTotal}>₹{order.totalAmount.toLocaleString('en-IN')}</span></div>
                    </div>
                </section>
            </>
    ) : 'loading...'
}