import { useContext, useEffect, useState } from 'react'
import styles from '../styles/ProductDetails.module.css'
import { useNavigate, useParams } from 'react-router'
import ToastContext from '../contexts/ToastContext'
import CartContext from '../contexts/CartContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import CheckoutContext from '../contexts/CheckoutContext'
import ProductImageGallery from './ProductImageGallery'

export default function ProductDetails() {

    const { showToast } = useContext(ToastContext)
    const { addToCart } = useContext(CartContext)
    const { productId } = useParams()
    const [ product, setProduct ] = useState(null)
    const { authFetch } = useAuthFetch()
    const { setCheckoutCart, setBuyNow } = useContext(CheckoutContext)
    const navigate = useNavigate()
    console.log(product?.imageUrls)

    async function fetchProduct() {
        const res = await fetch(`http://localhost:8080/api/products/${productId}`, {
            method: "GET",
            credentials: "include"
        })
        const productResponse = await res.json()
        setProduct(productResponse)
    }

    async function buyNow() {
        const res = await authFetch(`http://localhost:8080/api/cart/checkout/buy-now/product/${productId}`, {
            method: 'POST'
        })
        const cartResponse = await res.json()
        setCheckoutCart(cartResponse)
        setBuyNow(true)
        navigate('/checkout')
    }

    useEffect(() => {
        const init = async () => await fetchProduct()
        init()
    }, [productId])

    return product?(
        <div className={styles.productDetails}>
            <div>
                <ProductImageGallery imageUrls={product.imageUrls} />
                <div className={styles.productActions}>
                    <button className={`${styles.btnAddToCart} ${styles.btnProductAction}`} onClick={() => addToCart(productId, showToast)}>Add to Cart</button>
                    <button className={`${styles.btnBuyNow} ${styles.btnProductAction}`} onClick={buyNow}>Buy now</button>
                </div>
            </div>
            <div>
                <h1 className={styles.productTitle}>{product.productName}</h1>
                <div className={styles.prices}>
                    <span className={styles.productPrice}>₹{product.price.toLocaleString('en-IN')}</span>
                    <span className={styles.productMrp}>₹{product.mrp.toLocaleString('en-IN')}</span>
                </div>
                {product.description && <div className={styles.productDescription}>
                    <h2>Product Description</h2>
                    <p>{product.description}</p>
                </div>}
            </div>
        </div>
    ):"loading"
}