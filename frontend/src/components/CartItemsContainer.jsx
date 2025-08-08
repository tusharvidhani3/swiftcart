import CartItem from "./CartItem";
import styles from '../styles/Cart.module.css'
import { useContext } from "react";
import CartContext from "../contexts/CartContext";

export default function CartItemsContainer() {

    const {cart} = useContext(CartContext)

    return (
        <section className={styles.cartItemsContainer}>
            {cart.cartItems.map(cartItem => <CartItem {...cartItem} key={cartItem.cartItemId} />)}
        </section>
    )
}