import { useContext } from 'react'
import styles from '../styles/ProductDetails.module.css'
import { useNavigate, useParams } from 'react-router'
import CartContext from '../contexts/CartContext'
import ProductImageGallery from './ProductImageGallery'
import { formatPaiseToRupees } from '../utils/currency'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import ProductDetailsSkeleton from './ProductDetailsSkeleton'
import { api } from '@/api/client'

export default function ProductDetails() {

    const { addToCart } = useContext(CartContext)
    const { productId } = useParams()
    const navigate = useNavigate()

    const queryClient = useQueryClient()

    const { mutate: buyNow } = useMutation({
        mutationFn: (productId) => api.post(`/carts/checkout/buy-now/product/${productId}`),
        onSuccess: (cart) => {
            queryClient.setQueryData(['cart'], cart)
            navigate('/checkout?source=buy_now')
        }
    })

    const { data: product, isLoading } = useQuery({
        queryKey: ['products', productId],
        queryFn: () => api.get(`/products/${productId}`),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <ProductDetailsSkeleton /> : (
        <div className={styles.productDetails}>
            <div>
                <ProductImageGallery imageUrls={product.imageUrls} />
                {!product.isOutOfStock && <div className={styles.productActions}>
                    <button className={`${styles.btnAddToCart} ${styles.btnProductAction}`} onClick={() => addToCart(productId)}>Add to Cart</button>
                    <button className={`${styles.btnBuyNow} ${styles.btnProductAction}`} onClick={() => buyNow(productId)}>Buy now</button>
                </div>}
            </div>
            <div>
                <h1 className={styles.productTitle}>{product.name}</h1>
                <div className={styles.prices}>
                    <span className={styles.productPrice}>{product.isOutOfStock ? 'Out of stock' : formatPaiseToRupees(product.price)}</span>
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