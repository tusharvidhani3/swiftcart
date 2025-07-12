import { useContext } from 'react'
import styles from '../styles/index.module.css'
import ToastContext from '../contexts/ToastContext'

export default function ProductCard({ productId, productName, price, mrp, imageUrls, setTokenExpired }) {

    const { showToast } = useContext(ToastContext)

    async function addToCart() {
        const res = await fetch("http://localhost:8080/api/cart/items", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ productId, quantity: 1 })
        })
        if (res.ok) {
            showToast("Product added to cart")
        }
        else if (res.status === 403) {
            setTokenExpired(true)
        }
    }

    const imagePath = imageUrls[0]
    console.log(imageUrls[0])

    return (
        <div className={styles.productCard}>
            <img alt="product image" className={styles.productImage} src={`http://localhost:8080/${imageUrls[0]}`} />
            <h1 className={styles.productTitle}>{productName}</h1>
            <div>
                <span className={styles.productPrice}>₹{price}</span>
                <span className={styles.productMrp}>₹{mrp}</span>
            </div>
            <button className={styles.btnAddToCart} onClick={e => addToCart()}>Add to cart</button>
        </div>
    )
}