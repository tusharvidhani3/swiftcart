import { useContext } from 'react'
import styles from '../styles/Home.module.css'
import { useNavigate } from 'react-router'
import CartContext from '../contexts/CartContext'
import { formatPaiseToRupees } from '../utils/currency'
import { useAuthFetch } from '../hooks/useAuthFetch'

export default function ProductCard({ id, name, price, mrp, imageUrls }) {

    const navigate = useNavigate()
    const { addToCart } = useContext(CartContext)
    const authFetch = useAuthFetch()

    return (
        <div className={styles.productCard} onClick={() => navigate(`/products/${id}`)}>
            <img alt="product image" className={styles.productImage} src={`${imageUrls[0]}`} />
            <h2 className={styles.productTitle}>{name}</h2>
            <div>
                <span className={styles.productPrice}>{formatPaiseToRupees(price)}</span>
                <span className={styles.productMrp}>{formatPaiseToRupees(mrp)}</span>
                <span className={styles.discount}>{Math.round((mrp - price)*100/mrp)}% Off</span>
            </div>
            <button className={styles.btnAddToCart} onClick={e => {
                e.stopPropagation()
                addToCart(id)
                }}>Add to cart</button>
        </div>
    )
}