import { Link } from 'react-router'
import emptyCart from '../assets/images/empty-cart.png'
import styles from '../styles/Cart.module.css'

export default function EmptyCart() {

    return (
        <div className={styles.emptyCart}>
            <img src={emptyCart} alt="Empty Cart" />
            <h2>Your cart is empty!</h2>
            <p>Add items to it now</p>
            <Link className={styles.shopNow} to="/">Shop now</Link>
        </div>
    )
}