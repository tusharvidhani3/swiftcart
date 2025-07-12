import styles from '../styles/cart.module.css'

export default function PriceDetails() {

    return (
        <section className={styles.priceDetails}>
            <h2 className={styles.priceSummaryTitle}>Price Details</h2>
            <div className={styles.priceSummary}>
                <span>Price (<span className={styles.cartItemsCount}></span> items)</span> <span className={styles.price}></span>
            </div>
            <div className={styles.priceSummary}>
                <span>Delivery Charges</span> <span className={styles.deliveryCharge}>FREE DELIVERY</span>
            </div>
            <div className={`${styles.priceSummary} ${styles.orderTotal}`}>
                <span>Total Amount</span> <span className={styles.totalAmount}></span>
            </div>

            <div className={styles.cartCheckout}>
                <h2 className={styles.priceToPay}></h2>
                <button className={styles.btnCheckout}>Proceed to Checkout</button>
            </div>
        </section>
    )
}