import { useEffect, useState } from 'react'
import styles from '../styles/Orders.module.css'

export default function OrderItemCard({ product, deliveryAt, orderItemStatus }) {

    const [status, setStatus] = useState(orderItemStatus)

    useEffect(() => {
        setStatus(generateStatus(orderItemStatus, deliveryAt))
    }, [orderItemStatus])

    return (
        <div className={styles.orderItemCard}>
            <div className={styles.orderItemStatus}>{status}</div>
            <div className={styles.productInfo}>
                <img src={product.imageUrls[0]} alt="product image preview" />
                <h3 className={styles.productTitle}>{product.productName}</h3>
            </div>
        </div>
    )
}

export function generateStatus(orderItemStatus, deliveryAt) {
    const deliveryDate = new Date(deliveryAt)
    let showDate = false
    if (orderItemStatus === 'CONFIRMED' || orderItemStatus === 'SHIPPED' || orderItemStatus === 'DELIVERED')
        showDate = true

    let statusText
    if (orderItemStatus === 'CONFIRMED' || orderItemStatus === 'SHIPPED')
        statusText = 'Arriving'
    else if(orderItemStatus === 'OUT_FOR_DELIVERY')
        statusText = 'Arriving today'
    else if (orderItemStatus === 'DELIVERED')
        statusText = 'Delivered'
    else
        statusText = orderItemStatus

    return `${statusText} ${showDate ? `${deliveryDate.getDate()} ${deliveryDate.toLocaleString('default', { month: 'long' })} ${deliveryDate.getFullYear()}` : ''}`
}