import { useContext, useEffect } from 'react'
import CartItemsContainer from './CartItemsContainer'
import PriceDetails from './PriceDetails'
import EmptyCart from './EmptyCart'
import { useNavigate } from 'react-router'
import CartContext from '../contexts/CartContext'
import CheckoutContext from '../contexts/CheckoutContext'

export default function Cart() {

    const { cart } = useContext(CartContext)
    const { setBuyNow, setCheckoutCart } = useContext(CheckoutContext)
    const navigate = useNavigate()

    useEffect(() => {
        setBuyNow(false)
    }, [])

    useEffect(() => {
        setCheckoutCart(cart)
    }, [cart])
    
    return cart?.cartItems.length ? (
        <>
            <CartItemsContainer />
            <PriceDetails cart={cart} proceedToBtnTxt={"Checkout"} proceedToBtnClick={() => {navigate('/checkout')}} />
        </>) : <EmptyCart />
}