import { useContext, useEffect, useState } from 'react'
import styles from '../styles/ProductDetails.module.css'
import { useParams } from 'react-router'
import ToastContext from '../contexts/ToastContext'
import CartContext from '../contexts/CartContext'

export default function ProductDetails() {

    const { showToast } = useContext(ToastContext)
    const { addToCart } = useContext(CartContext)
    const { productId } = useParams()
    const [ product, setProduct ] = useState(null)

    async function fetchProduct() {
        const res = await fetch(`http://localhost:8080/api/products/${productId}`, {
            method: "GET",
            credentials: "include"
        })
        const productResponse = await res.json()
        setProduct(productResponse)
    }

    useEffect(() => {
        const init = async () => await fetchProduct()
        init()
    }, [productId])

    return product?(
        <div className={styles.productDetails}>
            <div>
                <div className={styles.productImages}></div>
                <div className={styles.productImageContainer}>
                    <img alt="product image" className={styles.productImage} src={`http://localhost:8080/${product.imageUrls[0]}`} />
                </div>
                <div className={styles.productActions}>
                    <button className={`${styles.btnAddToCart} ${styles.btnProductAction}`} onClick={() => addToCart(productId, showToast)}>Add to Cart</button>
                    <button className={`${styles.btnBuyNow} ${styles.btnProductAction}`}>Buy now</button>
                </div>
            </div>
            <div>
                <h1 className={styles.productTitle}>{product.productName}</h1>
                <h2 className={styles.productPrice}>₹ {product.price}</h2>
                <div className={styles.productDescription}>{product.description}</div>
            </div>
        </div>
    ):"loading"
}