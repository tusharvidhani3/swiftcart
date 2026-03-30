import { useContext, useState } from 'react'
import { apiBaseUrl } from '../config'
import { useAuthFetch } from '../hooks/useAuthFetch'
import styles from '../styles/SellerProducts.module.css'
import ProductsContext from '../contexts/ProductsContext'
import { useNavigate } from 'react-router'
import { formatPaiseToRupees } from '../utils/currency'
import { EllipsisVertical } from 'lucide-react'

export default function SellerProductCard({ product, threeDotsMenuOpen, setThreeDotsMenuOpen }) {

    const { productId, name, imageUrls, mrp } = product
    const { authFetch } = useAuthFetch()
    const { setEditingProduct, setProducts } = useContext(ProductsContext)
    const navigate = useNavigate()
    const [stock, setStock] = useState(product.stock)
    const [price, setPrice] = useState(product.price)

    async function updateStock(newStock) {
        const res = await authFetch(`${apiBaseUrl}/api/products/${productId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                stock: newStock
            })
        })
        const productResponse = await res.json()
        setProducts(products => [...products].map(modifiedProduct => modifiedProduct.productId === productResponse.productId ? productResponse : modifiedProduct))
    }

    async function updateProduct(updatedProduct) {
        const formData = new FormData()
        formData.append("productRequest", new Blob(
            [JSON.stringify(updatedProduct)], { type: "application/json" }
        ))
        const res = await authFetch(`${apiBaseUrl}/api/products/${productId}`, {
            method: 'PUT',
            body: formData
        })
        const productResponse = await res.json()
        setProducts(products => [...products].map(modifiedProduct => modifiedProduct.productId === productResponse.productId ? productResponse : modifiedProduct))
    }

    return (
        <div className={styles.productCard}>
            <img className={styles.productImage} src={imageUrls[0]} alt="product image" />
            <div className={styles.productInfo}>
                <h2 className={styles.productTitle}>{name}</h2>
                <div className={styles.inputFields}>
                    <div className={styles.productPrice}>Price: <div className={styles.priceInputContainer}><span>₹</span><input id='price' value={formatPaiseToRupees(price).substring(1)} onChange={e => setPrice(Number(e.target.value.replace(/,/g, "")))} /></div></div>
                    <div className={styles.productMrp}>MRP: <span>{formatPaiseToRupees(mrp)}</span></div>
                    <div className={styles.productStock}>Stock: <input type="number" id='stock' value={stock} onChange={e => setStock(e.target.value)} /></div>
                    {(price !== product.price || stock !== product.stock) && <button className={styles.btnUpdate} onClick={() => {
                        if (price !== product.price) {
                            const updatedProduct = { price: price }
                            if (stock !== product.stock)
                                updatedProduct.stock = stock
                            updateProduct(updatedProduct)
                        }
                        else
                            updateStock(stock)
                    }}>{price !== product.price ? 'Update' : 'Update Stock'}</button>}
                </div>
                <div className={styles.threeDots}>
                    <EllipsisVertical className={styles.threeDotsIcon} onClick={e => {
                        e.stopPropagation()
                        setThreeDotsMenuOpen(threeDotsMenuOpen => threeDotsMenuOpen === productId ? null : productId)
                    }} />
                    {threeDotsMenuOpen === productId && <div className={styles.threeDotsMenu} onClick={() => setThreeDotsMenuOpen(null)}>
                        <div onClick={() => {
                            setEditingProduct(product)
                            navigate('./edit')
                        }}>Edit</div>
                        <div onClick={() => updateStock(0)}>Deactivate</div>
                    </div>}
                </div>
            </div>
        </div>
    )
}