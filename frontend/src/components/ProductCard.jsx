import { useContext } from 'react'
import styles from '../styles/Home.module.css'
import ToastContext from '../contexts/ToastContext'
import { useNavigate } from 'react-router'
import CartContext from '../contexts/CartContext'

export default function ProductCard({ productId, productName, price, mrp, imageUrls }) {

    const { showToast } = useContext(ToastContext)
    const navigate = useNavigate()
    const { addToCart } = useContext(CartContext)

    return (
        <div className={styles.productCard} onClick={() => navigate(`/products/${productId}`)}>
            <img alt="product image" className={styles.productImage} src={`${imageUrls[0]}`} />
            <h2 className={styles.productTitle}>{productName}</h2>
            <div>
                <span className={styles.productPrice}>₹{price.toLocaleString('en-IN')}</span>
                <span className={styles.productMrp}>₹{mrp.toLocaleString('en-IN')}</span>
                <span className={styles.discount}>{Math.round((mrp - price)*100/mrp)}% Off</span>
            </div>
            <button className={styles.btnAddToCart} onClick={e => {
                e.stopPropagation()
                addToCart(productId, showToast)
                }}>Add to cart</button>
        </div>
    )
}