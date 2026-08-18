import styles from '../styles/Cart.module.css'
import { CirclePlus, CircleMinus } from 'lucide-react'
import { useNavigate } from 'react-router'
import { formatPaiseToRupees } from '../utils/currency'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Button } from './ui/button'
import { toast } from 'sonner'
import { api } from '@/api/client'

export default function CartItem({ id, product, quantity }) {

    const navigate = useNavigate()

    const queryClient = useQueryClient()

    const { mutate: removeCartItem } = useMutation({
        mutationFn: (cartItemId) => api.delete(`/carts/items/${cartItemId}`),
        onSuccess: (cart) => {
            queryClient.invalidateQueries({ queryKey: ['cart', 'summary'] })
            queryClient.setQueryData(['cart'], cart)
        }
    })

    const { mutate: changeQty } = useMutation({
        mutationFn: ({ cartItemId, newQuantity }) => api.patch(`/carts/items/${cartItemId}`, { quantity: newQuantity }),
        onSuccess: (cart) => {
            queryClient.invalidateQueries({ queryKey: ['cart', 'summary'] })
            queryClient.setQueryData(['cart'], cart)
        },
        onError: (error) => {
            if (error.status === 409)
                toast.warning("Stock limit reached")
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
                <Button variant='outline' className='border' onClick={() => removeCartItem(id)}>Remove</Button>
            </div>
        </div>
    )
}