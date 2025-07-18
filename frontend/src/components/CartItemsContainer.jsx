import CartItem from "./CartItem";
import styles from '../styles/Cart.module.css'

export default function CartItemsContainer({ children, setCart }) {

    return (
        <section className={styles.cartItemsContainer}>
            {children.map(cartItem => <CartItem {...cartItem} setCart={setCart} key={cartItem.cartItemId} />)}
        </section>
    )
}