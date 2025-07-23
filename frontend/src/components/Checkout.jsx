import styles from "../styles/Checkout.module.css"
import PriceDetails from "./PriceDetails"
import bhimLogo from '../assets/icons/bhim-logo.svg'
import phonepeLogo from '../assets/icons/phonepe-logo.svg'
import gpayLogo from '../assets/icons/gpay-logo.svg'
import paytmLogo from '../assets/icons/paytm-logo.svg'
import { useContext, useEffect, useState } from "react"
import AddressesContext from "../contexts/AddressesContext"
import { useNavigate } from "react-router"
import { useAuthFetch } from "../hooks/useAuthFetch"

export default function Checkout() {

    const { selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const [paymentMethod, setPaymentMethod] = useState('UPI')
    const navigate = useNavigate()
    const { authFetch } = useAuthFetch()

    async function getDefaultAddress() {
        const res = await authFetch('http://localhost:8080/api/addresses/default', {
            method: 'GET'
        })
        const defaultAddress = await res.json()
        setSelectedAddress(defaultAddress)
    }

    useEffect(() => {
        if (!selectedAddress) {
            const init = async () => await getDefaultAddress()
            init()
        }
    }, [])

    async function placeOrder() {
        const res = await authFetch('http://localhost:8080/api/orders/checkout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                paymentMethod: paymentMethod,
                shippingAddressId: selectedAddress.addressId
            })
        })
        const orderResponse = await res.json()
        navigate(`/orders/summary/${orderResponse.orderId}`)
    }

    console.log(selectedAddress)
    return (
        <>
            <section className={styles.addressPreview}>
                <div>
                    <span>Deliver to:</span> <span id={styles.name}>{selectedAddress?.name}</span> <span id={styles.addressType}>{selectedAddress?.addressType}</span>
                    <div className={styles.address}><span id={styles.addressLine1}>{selectedAddress?.addressLine1}</span>, <span id={styles.addressLine2}>{selectedAddress?.addressLine2}</span>, <span id={styles.city}>{selectedAddress?.city}</span>, <span id={styles.state}>{selectedAddress?.state}</span> - <span id={styles.pincode}>{selectedAddress?.pincode}</span></div>
                    <div id={styles.mobileNumber}>{selectedAddress?.mobileNumber}</div>
                </div>
                <button className={styles.btnChangeAddress} onClick={() => navigate('/addresses/select')}>Change</button>
            </section>
            <PriceDetails proceedToBtnTxt='Pay' proceedToBtnClick={placeOrder} />
            <section className={styles.paymentMethod}>
                <h2>Payment Method</h2>
                <form id={styles.paymentForm}>
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="UPI" checked={paymentMethod === 'UPI'} onChange={e => setPaymentMethod(e.target.value)} /> <img src={bhimLogo} alt="bhim upi" /> <img src={phonepeLogo} alt="phonepe upi" /> <img src={gpayLogo} alt="google pay upi" /> <img src={paytmLogo} alt="paytm upi" /> UPI
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="CARD" checked={paymentMethod === 'CARD'} onChange={e => setPaymentMethod(e.target.value)} /> Debit / Credit Card
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="NET_BANKING" checked={paymentMethod === 'NET_BANKING'} onChange={e => setPaymentMethod(e.target.value)} /> Net Banking
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" name="paymentMethod" value="CASH_ON_DELIVERY" checked={paymentMethod === 'CASH_ON_DELIVERY'} onChange={e => setPaymentMethod(e.target.value)} /> Cash on Delivery
                    </label>
                    <br />
                </form>
            </section>
        </>
    )
}