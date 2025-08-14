import styles from '../styles/Cart.module.css'
import plus from '../assets/icons/plus.svg'
import minus from '../assets/icons/minus.svg'
import { useContext } from 'react'
import ToastContext from '../contexts/ToastContext'
import { useNavigate } from 'react-router'
import CartContext from '../contexts/CartContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'

export default function CartItem({ cartItemId, product, quantity }) {

    const { showToast } = useContext(ToastContext)
    const { setCart } = useContext(CartContext)
    const navigate = useNavigate()
    const {authFetch} = useAuthFetch()

    async function changeQty(changeInQty) {
        const res = await authFetch(`${apiBaseUrl}/api/cart/items/${cartItemId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ "quantity": quantity + changeInQty })
        })
        let cartResponse = null
        if (res.ok) {
            cartResponse = await res.json()
            setCart(cartResponse)
        }
        else
            showToast("Cannot add more items. Stock limit reached")
    }

    async function removeCartItem() {
        const res = await authFetch(`${apiBaseUrl}/api/cart/items/${cartItemId}`, {
            method: "DELETE",
        })
        const cartResponse = await res.json()
        setCart(cartResponse)
    }

    return (
        <div className={styles.cartItem}>
            <div className={styles.productPreview} onClick={() => navigate(`/products/${product.productId}`)}>
                <img alt="product image preview" className={styles.productImage} src={product.imageUrls[0]} />
                <div>
                    <h2 className={styles.productTitle}>{product.productName}</h2>
                    <h3 className={styles.producPrice}>₹{product.price.toLocaleString('en-IN')}</h3>
                </div>
            </div>
            <div className={styles.cartItemActions}>
                <button className={styles.btnQty} onClick={() => changeQty(1)}><img src={plus} alt="increment button" /></button>
                <span className={styles.qty}>{quantity}</span>
                <button className={`${styles.btnQty} ${quantity==1?styles.grayedOut:""}`} onClick={() => changeQty(-1)}><img src={minus} alt="decrement button" /></button>
                <button className={styles.btnRemove} onClick={removeCartItem}>Remove</button>
            </div>
        </div>
    )
}