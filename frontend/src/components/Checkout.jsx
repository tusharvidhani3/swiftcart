import styles from "../styles/Checkout.module.css"
import PriceDetails from "./PriceDetails"
import razorpayLogo from '../assets/icons/razorpay-logo.svg'
import { useContext, useEffect, useState } from "react"
import AddressesContext from "../contexts/AddressesContext"
import { useNavigate, useSearchParams } from "react-router"
import useMediaQuery from "../hooks/useMediaQuery"
import ManageAddresses from "./ManageAddresses"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { Loader2 } from "lucide-react"
import useRazorpay from "../hooks/useRazorpay"
import { api } from "@/api/client"

export default function Checkout() {

    const { selectedAddressId, setSelectedAddressId } = useContext(AddressesContext)
    const [isPrepaid, setPrepaid] = useState(true)
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()
    const isBuyNow = searchParams.get('source') === 'buy_now'
    const [showAddressSelector, setShowAddressSelector] = useState(false)
    const isMobile = useMediaQuery('(max-width: 767px)')
    const handlePayment = useRazorpay()

    const { data: defaultAddress, isError: isDefaultAddressUnavailable, error: defaultAddressError } = useQuery({
        queryKey: ['addresses', 'detail', 'default'],
        queryFn: () => api.get('/api/addresses/default'),
        staleTime: 1000 * 60 * 5,
        retry: false,
        enabled: !selectedAddressId,
        onSuccess: (defAddress) => setSelectedAddressId(defAddress.id)
    })

    const { data: cartSummary, isLoading: isCartSummaryLoading } = useQuery({
        queryKey: ['cart', 'summary'],
        queryFn: () => api.get(`/carts/summary`),
        staleTime: 1000 * 60 * 5
    })

    useEffect(() => {
        if (cartSummary && !cartSummary.items.length) {
            navigate('/cart');
        }
    }, [cartSummary])

    const { data: selectedAddress } = useQuery({
        queryKey: ['addresses', selectedAddressId],
        queryFn: () => api.get(`/api/addresses/${selectedAddressId}`),
        staleTime: 1000 * 60 * 5,
        enabled: !!selectedAddressId
    })

    useEffect(() => {
        if (isDefaultAddressUnavailable && defaultAddressError.status === 404)
            navigate('/addresses/select')
    }, [isDefaultAddressUnavailable])

    const queryClient = useQueryClient()

    const { mutate: placeOrder } = useMutation({
        mutationFn: ({ isPrepaid, selectedAddressId }) => api.post(`/orders/checkout`, { prepaid: isPrepaid, shippingAddressId: selectedAddressId }),
        onSuccess: (orderResponse) => {
            queryClient.invalidateQueries({ queryKey: ['cart'] })
            queryClient.setQueryData(['orders', 'list', orderResponse.id], orderResponse)
            if(isPrepaid) {
                handlePayment(orderResponse)
            }
        },
        onError: (error) => console.log(error)
    })

    const { mutate: placeBuyNowOrder } = useMutation({
        mutationFn: ({ cartItemId, selectedAddressId, isPrepaid }) => api.post(`/orders/checkout/buy-now`, { cartItemId: cartItemId, shippingAddressId: selectedAddressId, prepaid: isPrepaid }),
        onSuccess: (orderResponse) => {
            queryClient.setQueryData(['orders', 'list', orderResponse.id], orderResponse)
            if(isPrepaid) {
                handlePayment(orderResponse)
            }
        },
        onError: (error) => console.log(error)
    })

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
            <PriceDetails nextBtnClick={() => isBuyNow ? placeBuyNowOrder({ cartItemId: cartSummary.items[0].id, selectedAddressId, isPrepaid }) : placeOrder({ isPrepaid, selectedAddressId })} cart={cartSummary} isCheckoutMode={true} isCod={!isPrepaid} />
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