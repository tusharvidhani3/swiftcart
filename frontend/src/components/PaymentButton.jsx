import loadRazorpayScript from '../utils/loadRazorpayScript'
import styles from '../styles/PriceDetails.module.css'
import { useContext } from 'react'
import ToastContext from '../contexts/ToastContext'
import { useNavigate } from 'react-router'

export default function PaymentButton({ createOrder, amount }) {

    const { setToast } = useContext(ToastContext)
    const navigate = useNavigate()

    const handlePayment = async () => {

        const orderResponse = await createOrder()

        const loaded = await loadRazorpayScript()
        if (!loaded) {
            alert("Failed to load Razorpay script. Check your internet connection.");
            return;
        }

        const options = {
            key: import.meta.env.VITE_RAZORPAY_KEY_ID,
            amount: amount,
            currency: "INR",
            name: "SwiftCart",
            description: "Test Transaction",
            order_id: orderResponse.payment.paymentOrderId, 
            theme: { color: "#3399cc" },
            handler: () => {
                navigate(`/orders/${orderResponse.orderId}`)
            },
            modal: {
                ondismiss: () => setToast("Payment failed")
            }
        }

        const paymentObject = new Razorpay(options)
        paymentObject.open()
    }

    return <button className={styles.btnCheckout} onClick={handlePayment}>Pay Online</button>
}