import styles from '../styles/PriceDetails.module.css'

export default function PriceDetails({ proceedToBtnTxt, proceedToBtnClick, cart, isCheckoutMode }) {

    const totalAmount=cart?.totalPrice, cartItemsCount=cart?.cartItems.length

    return (
        <section className={`${styles.priceDetails} ${isCheckoutMode?styles.checkoutMode:''}`}>
            <h2 className={styles.priceSummaryTitle}>Price Details</h2>
            <div className={styles.priceSummary}>
                <span>Price (<span className={styles.cartItemsCount}>{cartItemsCount}</span> items)</span> <span className={styles.price}>₹{totalAmount.toLocaleString('en-IN')}</span>
            </div>
            <div className={styles.priceSummary}>
                <span>Delivery Charges</span> <span className={styles.deliveryCharge}>FREE DELIVERY</span>
            </div>
            <div className={`${styles.priceSummary} ${styles.orderTotal}`}>
                <span>Total Amount</span> <span className={styles.totalAmount}>₹{totalAmount.toLocaleString('en-IN')}</span>
            </div>

            <div className={styles.checkout}>
                <h2 className={styles.priceToPay}>₹{totalAmount.toLocaleString('en-IN')}</h2>
                <button className={styles.btnCheckout} onClick={proceedToBtnClick}>Proceed to {proceedToBtnTxt}</button>
            </div>
        </section>
    )
}