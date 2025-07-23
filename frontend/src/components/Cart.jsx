import { useContext } from 'react'
import CartItemsContainer from './CartItemsContainer'
import PriceDetails from './PriceDetails'
import EmptyCart from './EmptyCart'
import { useNavigate } from 'react-router'
import CartContext from '../contexts/CartContext'

export default function Cart() {

    const { cart } = useContext(CartContext)
    const navigate = useNavigate()
    
    return cart ? (
        <>
            <CartItemsContainer />
            <PriceDetails proceedToBtnTxt={"Checkout"} proceedToBtnClick={() => {navigate('/checkout')}} />
        </>) : <EmptyCart />
}