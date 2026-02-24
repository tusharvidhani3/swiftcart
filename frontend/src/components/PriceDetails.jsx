import { useNavigate } from 'react-router'
import styles from '../styles/PriceDetails.module.css'
import PaymentButton from './PaymentButton'
import { formatPaiseToRupees } from '../utils/currency'

export default function PriceDetails({ nextBtnClick, cart, isCheckoutMode, isCod }) {

    const navigate = useNavigate()
    const totalAmount=cart?.totalPrice, cartItemsCount=cart?.items.length

    return (
        <section className={`${styles.priceDetails} ${isCheckoutMode?styles.checkoutMode:''}`}>
            <h2 className={styles.priceSummaryTitle}>Price Details</h2>
            <div className={styles.priceSummary}>
                <span>Price (<span className={styles.cartItemsCount}>{cartItemsCount}</span> items)</span> <span className={styles.price}>{formatPaiseToRupees(totalAmount)}</span>
            </div>
            <div className={styles.priceSummary}>
                <span>Delivery Charges</span> <span className={styles.deliveryCharge}>FREE DELIVERY</span>
            </div>
            <div className={`${styles.priceSummary} ${styles.orderTotal}`}>
                <span>Total Amount</span> <span className={styles.totalAmount}>{formatPaiseToRupees(totalAmount)}</span>
            </div>

            <div className={styles.checkout}>
                <h2 className={styles.priceToPay}>{formatPaiseToRupees(totalAmount)}</h2>
                {isCheckoutMode ? (isCod ? <button className={styles.btnCheckout} onClick={async () => {
                    const orderResponse = await nextBtnClick()
                    navigate(`/orders/${orderResponse.id}`)
                    }}>Place Order</button> : <PaymentButton createOrder={nextBtnClick} amount={totalAmount*100} />) : <button className={styles.btnCheckout} onClick={nextBtnClick}>Proceed to Checkout</button>}
            </div>
        </section>
    )
}