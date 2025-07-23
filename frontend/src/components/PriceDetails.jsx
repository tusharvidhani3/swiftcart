import { useContext } from 'react'
import styles from '../styles/PriceDetails.module.css'
import CartContext from '../contexts/CartContext'

export default function PriceDetails({ proceedToBtnTxt, proceedToBtnClick }) {

    const {cart} = useContext(CartContext)
    const totalAmount=cart.totalPrice, cartItemsCount=cart.cartItems.length

    return (
        <section className={styles.priceDetails}>
            <h2 className={styles.priceSummaryTitle}>Price Details</h2>
            <div className={styles.priceSummary}>
                <span>Price (<span className={styles.cartItemsCount}>{cartItemsCount}</span> items)</span> <span className={styles.price}>₹ {totalAmount}</span>
            </div>
            <div className={styles.priceSummary}>
                <span>Delivery Charges</span> <span className={styles.deliveryCharge}>FREE DELIVERY</span>
            </div>
            <div className={`${styles.priceSummary} ${styles.orderTotal}`}>
                <span>Total Amount</span> <span className={styles.totalAmount}>₹ {totalAmount}</span>
            </div>

            <div className={styles.checkout}>
                <h2 className={styles.priceToPay}>₹ {totalAmount}</h2>
                <button className={styles.btnCheckout} onClick={proceedToBtnClick}>Proceed to {proceedToBtnTxt}</button>
            </div>
        </section>
    )
}