import { useNavigate } from "react-router";
import loadRazorpayScript from "../utils/loadRazorpayScript";
import { toast } from "sonner";

export default function useRazorpay() {

    const navigate = useNavigate()

    const handlePayment = async (orderResponse) => {

        const loaded = await loadRazorpayScript()
        if (!loaded) {
            alert("Failed to load Razorpay script. Check your internet connection.");
            return;
        }

        const options = {
            key: import.meta.env.VITE_RAZORPAY_KEY_ID,
            amount: orderResponse.totalAmount,
            currency: "INR",
            name: "SwiftCart",
            description: "Test Transaction",
            order_id: orderResponse.payment.paymentOrderId,
            theme: { color: "#3399cc" },
            handler: () => {
                navigate(`/orders/${orderResponse.id}`)
            },
            modal: {
                ondismiss: () => toast.error("Payment failed")
            }
        }

        const paymentObject = new Razorpay(options)
        paymentObject.open()
    }

    return handlePayment
}