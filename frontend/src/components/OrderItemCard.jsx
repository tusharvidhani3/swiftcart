import styles from '../styles/Orders.module.css'

export default function OrderItemCard({ product, placedAt, deliveryAt }) {

    return (
        <div className={styles.orderItemCard}>
            <img src={product.imageUrls[0]} alt="product image preview" />
            <div>
                <h3 className={styles.productTitle}>{product.productTitle}</h3>
                <div class={styles.orderedOn}>Ordered on <span className={styles.orderDate}>{}</span></div>
            </div>
        </div>
    )
}