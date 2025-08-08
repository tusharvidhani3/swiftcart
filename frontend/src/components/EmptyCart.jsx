import { Link } from 'react-router'
import emptyCart from '../assets/images/empty-cart.png'
import noCart from '../assets/images/d438a32e-765a-4d8b-b4a6-520b560971e8.webp'
import styles from '../styles/Cart.module.css'
import { useContext } from 'react'
import UserContext from '../contexts/UserContext'

export default function EmptyCart() {

    const { userInfo } = useContext(UserContext)

    return userInfo?(
        <div className={styles.emptyCart}>
            <img src={emptyCart} alt="Empty Cart" />
            <h2>Your cart is empty!</h2>
            <p>Add items to it now</p>
            <Link className={styles.shopNow} to="/">Shop now</Link>
        </div>
    )
    :
    (
        <div className={styles.emptyCart}>
            <img src={noCart} alt="No Cart" />
            <h2>Missing Cart items?</h2>
            <p>Login to see the items you added previously</p>
            <Link className={styles.shopNow} to="/auth/login">Login</Link>
        </div>
    )
}