import styles from '../styles/Cart.module.css'
import plus from '../assets/icons/plus.svg'
import minus from '../assets/icons/minus.svg'
import { useContext } from 'react'
import ToastContext from '../contexts/ToastContext'
import { useNavigate } from 'react-router'

export default function CartItem({ cartItemId, product, quantity, setCart }) {

    const { showToast } = useContext(ToastContext)
    const navigate = useNavigate()

    async function changeQty(changeInQty) {
        const res = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
            method: "PUT",
            credentials: "include",
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
        const res = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
            method: "DELETE",
            credentials: "include"
        })
        const cartResponse = await res.json()
        console.log(cartResponse)
        setCart(cartResponse)
    }

    return (
        <div className={styles.cartItem}>
            <div className={styles.productPreview} onClick={() => navigate(`/products/${product.productId}`)}>
                <img alt="product image preview" className={styles.productImage} src={`http://localhost:8080/${product.imageUrls[0]}`} />
                <div>
                    <h2 className={styles.productTitle}>{product.productName}</h2>
                    <h3 className={styles.producPrice}>₹ {product.price}</h3>
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