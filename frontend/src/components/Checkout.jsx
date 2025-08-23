import styles from "../styles/Checkout.module.css"
import PriceDetails from "./PriceDetails"
import razorpayLogo from '../assets/icons/razorpay-logo.svg'
import { useContext, useEffect, useState } from "react"
import AddressesContext from "../contexts/AddressesContext"
import { useNavigate } from "react-router"
import { useAuthFetch } from "../hooks/useAuthFetch"
import useMediaQuery from "../hooks/useMediaQuery"
import ManageAddresses from "./ManageAddresses"
import CheckoutContext from "../contexts/CheckoutContext"
import CartContext from "../contexts/CartContext"
import { apiBaseUrl } from "../config"

export default function Checkout() {

    const { selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const [isPrepaid, setPrepaid] = useState(true)
    const navigate = useNavigate()
    const { authFetch } = useAuthFetch()
    const { setAddresses } = useContext(AddressesContext)
    const { checkoutCart, isBuyNow } = useContext(CheckoutContext)
    const { setCart } = useContext(CartContext)
    const [showAddressSelector, setShowAddressSelector] = useState(false)
    const isMobile = useMediaQuery('(max-width: 767px)')

    async function getDefaultAddress() {
        const res = await authFetch(`${apiBaseUrl}/api/addresses/default`, {
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
        const res = await authFetch(`${apiBaseUrl}/api/orders/checkout`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                prepaid: isPrepaid,
                shippingAddressId: selectedAddress.addressId
            })
        })
        if (res.ok) {
            const orderResponse = await res.json()
            setCart(null)
            return orderResponse
        }
    }

    async function placeBuyNowOrder() {
        const res = await authFetch(`${apiBaseUrl}/api/orders/checkout/buy-now`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                cartItemId: checkoutCart.cartItems[0].cartItemId,
                shippingAddressId: selectedAddress.addressId,
                prepaid: isPrepaid
            })
        })
        if (res.ok) {
            const orderResponse = await res.json()
            return orderResponse
        }
    }

    async function getAddresses() {

        const res = await authFetch(`${apiBaseUrl}/api/addresses`, {
            method: "GET"
        })
        const addressList = await res.json()
        setAddresses(addressList)
    }

    useEffect(() => {
        if (showAddressSelector) {
            const init = async () => { await getAddresses() }
            init()
        }
    }, [showAddressSelector])

    return checkoutCart?(
        <>
            <section className={styles.addressPreview}>
                {!showAddressSelector ?
                    <div className={styles.deliverTo}>
                        <div>
                            <span>Deliver to:</span> <span id={styles.name}>{selectedAddress?.name}</span> <span id={styles.addressType}>{selectedAddress?.addressType}</span>
                            <div className={styles.address}><span id={styles.addressLine1}>{selectedAddress?.addressLine1}</span>, <span id={styles.addressLine2}>{selectedAddress?.addressLine2}</span>, <span id={styles.city}>{selectedAddress?.city}</span>, <span id={styles.state}>{selectedAddress?.state}</span> - <span id={styles.pincode}>{selectedAddress?.pincode}</span></div>
                            <div id={styles.mobileNumber}>Phone: {selectedAddress?.mobileNumber}</div>
                        </div>
                        <button className={styles.btnChangeAddress} onClick={isMobile ? () => navigate('/addresses/select') : () => setShowAddressSelector(true)}>Change</button>
                    </div>
                    :
                    <h2>Select a delivery address</h2>
                }
                {showAddressSelector && <div className={styles.addressSelectorMenu}>
                    <ManageAddresses isSelectMode={true} setShowAddressSelector={setShowAddressSelector} />
                </div>}
            </section>
            <PriceDetails nextBtnClick={isBuyNow ? placeBuyNowOrder : placeOrder} cart={checkoutCart} isCheckoutMode={true} isCod={!isPrepaid} />
            <section className={styles.paymentMethod}>
                <h2>Payment Method</h2>
                <form id={styles.paymentForm}>
                    <label className={styles.paymentOption}>
                        <input type="radio" value="prepaid" checked={isPrepaid} onChange={() => setPrepaid(true)} /> Pay using <img src={razorpayLogo} alt="Razorpay logo" />
                    </label>
                    <br />
                    <label className={styles.paymentOption}>
                        <input type="radio" value="cod" checked={!isPrepaid} onChange={() => setPrepaid(false)} /> Cash on Delivery
                    </label>
                    <br />
                </form>
            </section>
        </>
    ):'No cart'
}