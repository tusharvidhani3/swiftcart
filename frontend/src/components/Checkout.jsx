import styles from "../styles/Checkout.module.css"
import PriceDetails from "./PriceDetails"

export default function Checkout() {

    return (
        <>
            <section className={styles.addressPreview}>
                <div>
                    <span>Deliver to:</span> <span id={styles.name}></span> <span id={styles.addressType}></span>
                    <div className={styles.address}><span id={styles.addressLine1}></span>, <span id={styles.addressLine2}></span>, <span id={styles.city}></span>, <span id={styles.state}></span> - <span id={styles.pincode}></span></div>
                    <div id={styles.mobileNumber}></div>
                </div>
                <button className={styles.btnChangeAddress}>Change</button>
            </section>
            <PriceDetails  />
            <section className={styles.paymentMethod}>
                <h2>Payment Method</h2>
                <form id={styles.paymentForm}>
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="UPI" checked /> <img src="assets/icons/bhim-logo.svg" alt="bhim upi" /> <img src="assets/icons/phonepe-logo.svg" alt="phonepe upi" /> <img src="assets/icons/gpay-logo.svg" alt="google pay upi" /> <img src="assets/icons/paytm-logo.svg" alt="paytm upi" /> UPI
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="CARD" /> Debit / Credit Card
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="NET_BANKING" /> Net Banking
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="CASH_ON_DELIVERY" /> Cash on Delivery
                    </label>
                    <br />
                </form>
            </section>
        </>
    )
}