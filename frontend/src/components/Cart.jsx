import { useContext } from 'react'
import styles from '../styles/cart.module.css'
import CartItemsContainer from './CartItemsContainer'
import PriceDetails from './PriceDetails'

export default function Cart() {

    return (
        <>
            <CartItemsContainer></CartItemsContainer>
            <PriceDetails></PriceDetails>
        </>
    )
}