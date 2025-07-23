import styles from '../styles/OrderSummary.module.css'

export default function OrderSummary() {

    return (
        <>
            <section className={styles.section}>
                <h2>Order Details</h2>
                <div className={styles.orderDetails}>
                    <div><span>Order Id</span> <span className={styles.orderId}></span></div>
                    <div><span>Order date</span> <span className={styles.orderDate}></span></div>
                    <div><span>Order total</span> <span className={styles.orderTotal}></span></div>
                </div>
            </section>

            <section className={styles.section}>
                <h2>Products in Order</h2>
                <div className={styles.productsOrdered}>
                        <div className={styles.orderItem}>
                            <div className={styles.orderStatus}></div>
                            <div className={styles.productDetails}>
                                <img alt="product image preview" />
                                <span className={styles.productQty}></span>
                                <div>
                                    <div><span className={styles.productTitle}></span></div>
                                    <p className={styles.itemTotal}></p>
                                </div>
                            </div>
                            <div className={styles.itemActions}>
                                <button className={styles.btnReturn}>Return / Replace</button>
                                <button className={styles.btnReview}>Review</button>
                            </div>
                        </div>
                </div>
            </section>

            <section className={styles.section}>
                <h2>Payment Information</h2>
                <div className={styles.paymentInfo}>
                    <h3>Payment Method(s)</h3>
                    <p className={styles.paymentMethod}>Rupay Debit Card ending wih 8580</p>
                </div>
            </section>

            <section className={styles.section}>
                <h2>Ship to</h2>
                <div className={styles.shipTo}>
                    <p className={styles.name}></p>
                    <p className={styles.address}></p>
                </div>
            </section>

            <section className={styles.section}>
                <h2>Price Details</h2>
                <div className={styles.priceDetails}>
                    <div><span>Items</span> <span className={styles.itemsTotal}></span></div>
                    <div><span>Delivery</span><span className={styles.deliveryCharge}></span></div>
                    <div className={styles.orderSummaryTotal}><span>Order total</span> <span className={styles.orderTotal}></span></div>
                </div>
            </section>
        </>
    )
}