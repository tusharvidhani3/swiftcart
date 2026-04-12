import styles from "../styles/Checkout.module.css"
import PriceDetails from "./PriceDetails"
import razorpayLogo from '../assets/icons/razorpay-logo.svg'
import { useContext, useEffect, useState } from "react"
import AddressesContext from "../contexts/AddressesContext"
import { useNavigate, useSearchParams } from "react-router"
import { useAuthFetch } from "../hooks/useAuthFetch"
import useMediaQuery from "../hooks/useMediaQuery"
import ManageAddresses from "./ManageAddresses"
import { apiBaseUrl } from "../config"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Loader2 } from "lucide-react"

async function apiPlaceOrder(authFetch, isPrepaid, selectedAddressId) {
    const res = await authFetch(`${apiBaseUrl}/api/orders/checkout`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            prepaid: isPrepaid,
            shippingAddressId: selectedAddressId
        })
    })
    return await res.json()
}

async function apiPlaceBuyNowOrder(authFetch, cartItemId, selectedAddressId, isPrepaid) {
    const res = await authFetch(`${apiBaseUrl}/api/orders/checkout/buy-now`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            cartItemId: cartItemId,
            shippingAddressId: selectedAddressId,
            prepaid: isPrepaid
        })
    })
    return await res.json()
}

async function getCartSummary(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/carts/summary`, {
        method: 'GET'
    })
    return await res.json()
}

async function getDefaultAddress(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/addresses/default`, {
        method: 'GET'
    })
    const response = await res.json()
    console.log(response)
    if (res.ok)
        return response
    else if (response.status === 404) {
        const error = new Error()
        error.status = 404
        throw error
    }
}

export default function Checkout() {

    const { selectedAddress, setSelectedAddress } = useContext(AddressesContext)
    const [isPrepaid, setPrepaid] = useState(true)
    const navigate = useNavigate()
    const authFetch = useAuthFetch()
    const [ searchParams ] = useSearchParams()
    const isBuyNow = searchParams.get('source') === 'buy_now'
    const [showAddressSelector, setShowAddressSelector] = useState(false)
    const isMobile = useMediaQuery('(max-width: 767px)')

    const { data: defaultAddress, isError: isDefaultAddressUnavailable, error: defaultAddressError } = useQuery({
        queryKey: ['addresses', 'detail', 'default'],
        queryFn: () => getDefaultAddress(authFetch),
        staleTime: 1000 * 60 * 5,
        retry: 1,
        enabled: !selectedAddress
    })

    const { data: cartSummary, isLoading: isCartSummaryLoading } = useQuery({
        queryKey: ['cart', 'summary'],
        queryFn: () => getCartSummary(authFetch),
        staleTime: 1000 * 60 * 5
    })

    console.log(cartSummary)

    useEffect(() => {
        if(isDefaultAddressUnavailable && defaultAddressError.status === 404)
            navigate('/addresses/select')
    }, [isDefaultAddressUnavailable])

    useEffect(() => setSelectedAddress(defaultAddress), [defaultAddress])

    const queryClient = useQueryClient()

    const { mutate: placeOrder } = useMutation({
        mutationFn: ({ isPrepaid, selectedAddressId }) => apiPlaceOrder(authFetch, isPrepaid, selectedAddressId),
        onSuccess: (order) => {
            queryClient.invalidateQueries({ queryKey: ['cart'] })
            queryClient.setQueryData(['orders', 'list', order.id], order)
        }
    })

    const { mutate: placeBuyNowOrder } = useMutation({
        mutationFn: ({ cartItemId, selectedAddressId, isPrepaid }) => apiPlaceBuyNowOrder(authFetch, cartItemId, selectedAddressId, isPrepaid),
        onSuccess: (order) => {
            queryClient.setQueryData(['orders', 'list', order.id], order)
        }
    })

    console.log(cartSummary)

    return isCartSummaryLoading ? <Loader2 className="animate-spin" /> : (
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
            <PriceDetails nextBtnClick={isBuyNow ? placeBuyNowOrder(authFetch, cartSummary?.items[0]?.id, selectedAddress.id, isPrepaid) : placeOrder(authFetch, isPrepaid, selectedAddress.id)} cart={cartSummary} isCheckoutMode={true} isCod={!isPrepaid} />
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
    )
}