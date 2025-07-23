import styles from '../styles/Orders.module.css'

export default function OrderItemCard({ product, deliveryAt, orderItemStatus }) {

    const deliveryDate = new Date(deliveryAt)
    let showDate = false
    if(orderItemStatus === 'PROCESSING' || orderItemStatus === 'SHIPPED' || orderItemStatus === 'DELIVERED')
        showDate = true

    let statusText
    if(orderItemStatus === 'PROCESSING' || orderItemStatus === 'SHIPPED')
        statusText = 'Arriving'
    else if(orderItemStatus === 'DELIVERED')
        statusText = 'Delivered'
    else
        statusText = orderItemStatus
    
    console.log(statusText)
    return (
        <div className={styles.orderItemCard}>
            <img src={`http://localhost:8080/${product.imageUrls[0]}`} alt="product image preview" />
            <div>
                <h3 className={styles.productTitle}>{product.productName}</h3>
                <div className={styles.orderItemStatus}>{statusText} {showDate?`${deliveryDate.getDate()} ${deliveryDate.toLocaleString('default', { month: 'long' })} ${deliveryDate.getFullYear()}`:''}</div>
            </div>
        </div>
    )
}