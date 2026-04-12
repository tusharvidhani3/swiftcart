import styles from '../styles/Cart.module.css'
import { CirclePlus, CircleMinus } from 'lucide-react'
import { useContext } from 'react'
import ToastContext from '../contexts/ToastContext'
import { useNavigate } from 'react-router'
import { useAuthFetch } from '../hooks/useAuthFetch'
import { apiBaseUrl } from '../config'
import { formatPaiseToRupees } from '../utils/currency'
import { useMutation, useQueryClient } from '@tanstack/react-query'

async function apiRemoveCartItem(authFetch, cartItemId) {
    const res = await authFetch(`${apiBaseUrl}/api/carts/items/${cartItemId}`, {
        method: "DELETE",
    })
    return await res.json()
}

async function apiChangeQty(authFetch, cartItemId, newQuantity ) {
    const res = await authFetch(`${apiBaseUrl}/api/carts/items/${cartItemId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ "quantity": newQuantity })
    })
    if (res.status === 409) {
        const error = new Error()
        error.status = 409
        throw error
    }
    return await res.json()
}

export default function CartItem({ id, product, quantity }) {

    const { showToast } = useContext(ToastContext)
    const navigate = useNavigate()
    const authFetch = useAuthFetch()

    const queryClient = useQueryClient()

    const { mutate: removeCartItem } = useMutation({
        mutationFn: (cartItemId) => apiRemoveCartItem(authFetch, cartItemId),
        onSuccess: (cart) => queryClient.setQueryData(['cart'], cart)
    })

    const { mutate: changeQty } = useMutation({
        mutationFn: ({ cartItemId, newQuantity }) => apiChangeQty(authFetch, cartItemId, newQuantity),
        onSuccess: (cart) => queryClient.setQueryData(['cart'], cart),
        onError: (error) => {
            if (error.status)
                showToast("Cannot add more items. Stock limit reached")
        }
    })

    return (
        <div className={styles.cartItem}>
            <div className={styles.productPreview} onClick={() => navigate(`/products/${product.id}`)}>
                <img alt="product image preview" className={styles.productImage} src={product.imageUrls[0]} />
                <div>
                    <h2 className={styles.productTitle}>{product.name}</h2>
                    <h3 className={styles.productPrice}>{formatPaiseToRupees(product.price)}</h3>
                </div>
            </div>
            <div className={styles.cartItemActions}>
                <button className={styles.btnQty} onClick={() => changeQty({cartItemId: id, newQuantity: quantity + 1})}><CirclePlus /></button>
                <span className={styles.qty}>{quantity}</span>
                <button className={`${styles.btnQty} ${quantity == 1 ? styles.grayedOut : ""}`} onClick={() => changeQty({cartItemId: id, newQuantity: quantity - 1})}><CircleMinus /></button>
                <button className={styles.btnRemove} onClick={() => removeCartItem(id)}>Remove</button>
            </div>
        </div>
    )
}