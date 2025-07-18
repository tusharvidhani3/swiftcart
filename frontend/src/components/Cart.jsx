import { useContext, useEffect, useState } from 'react'
import CartItemsContainer from './CartItemsContainer'
import PriceDetails from './PriceDetails'
import UserContext from '../contexts/UserContext'
import EmptyCart from './EmptyCart'
import { useNavigate } from 'react-router'

export default function Cart() {

    const { userInfo } = useContext(UserContext)
    const [cart, setCart] = useState(null)
    const navigate = useNavigate()

    async function updateCart() {
        const res = await fetch("http://localhost:8080/api/cart", {
            method: "GET",
            credentials: "include"
        })
        const cartResponse = await res.json()
        if(cartResponse.cartItems.length)
            setCart(cartResponse)
        else
            setCart(null)
    }
    useEffect(() => {
        if(userInfo) {
            updateCart()
        }
    }, [userInfo])
    
    return cart ? (
        <>
            <CartItemsContainer setCart={setCart}>{cart.cartItems}</CartItemsContainer>
            <PriceDetails totalAmount={cart.totalPrice} cartItemsCount={cart.cartItems.length} proceedToBtnTxt={"Checkout"} proceedToBtnClick={() => {navigate('/checkout', {state: {...cart}})}} />
        </>) : <EmptyCart />
}