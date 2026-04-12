import { useContext } from 'react'
import styles from '../styles/ProductDetails.module.css'
import { useNavigate, useParams } from 'react-router'
import CartContext from '../contexts/CartContext'
import { useAuthFetch } from '../hooks/useAuthFetch'
import ProductImageGallery from './ProductImageGallery'
import { apiBaseUrl } from '../config'
import { useApi } from '../hooks/useApi'
import { formatPaiseToRupees } from '../utils/currency'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import ProductDetailsSkeleton from './ProductDetailsSkeleton'

async function fetchProduct(apiFetch, productId) {
    const res = await apiFetch(`${apiBaseUrl}/api/products/${productId}`, {
        method: "GET"
    })
    return await res.json()
}

async function apiBuyNow(authFetch, productId) {
    const res = await authFetch(`${apiBaseUrl}/api/carts/checkout/buy-now/product/${productId}`, {
        method: 'POST'
    })
    return await res.json()
}

export default function ProductDetails() {

    const { addToCart } = useContext(CartContext)
    const { productId } = useParams()
    const authFetch = useAuthFetch()
    const navigate = useNavigate()
    const apiFetch = useApi()

    const queryClient = useQueryClient()

    const { mutate: buyNow } = useMutation({
        mutationFn: (productId) => apiBuyNow(authFetch, productId),
        onSuccess: (cart) => {
            queryClient.setQueryData(['cart'], cart)
            navigate('/checkout?source=buy_now')
        }
    })

    const { data: product, isLoading } = useQuery({
        queryKey: ['products', productId],
        queryFn: () => fetchProduct(apiFetch, productId),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <ProductDetailsSkeleton /> : (
        <div className={styles.productDetails}>
            <div>
                <ProductImageGallery imageUrls={product.imageUrls} />
                <div className={styles.productActions}>
                    <button className={`${styles.btnAddToCart} ${styles.btnProductAction}`} onClick={() => addToCart(authFetch, productId)}>Add to Cart</button>
                    <button className={`${styles.btnBuyNow} ${styles.btnProductAction}`} onClick={() => buyNow(productId)}>Buy now</button>
                </div>
            </div>
            <div>
                <h1 className={styles.productTitle}>{product.name}</h1>
                <div className={styles.prices}>
                    <span className={styles.productPrice}>{formatPaiseToRupees(product.price)}</span>
                    <span className={styles.productMrp}>{formatPaiseToRupees(product.mrp)}</span>
                </div>
                {product.description && <div className={styles.productDescription}>
                    <h2>Product Description</h2>
                    <p>{product.description}</p>
                </div>}
            </div>
        </div>
    )
}