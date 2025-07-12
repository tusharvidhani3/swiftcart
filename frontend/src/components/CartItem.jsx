import styles from '../styles/cart.module.css'
import plus from '../assets/icons/plus.svg'
import minus from '../assets/icons/minus.svg'

export default function CartItem({productImage}) {

    return (
            <div className={styles.cartItem}>
                <div className={styles.productPreview}>
                    <img alt="product image preview" className={styles.productImage} src={productImage} />
                        <div>
                            <h2 className={styles.productTitle}></h2>
                            <h3 className={styles.producPrice}></h3>
                        </div>
                </div>
                <div className={styles.cartItemActions}>
                    <button className={styles.btnQty}><img src={plus} alt="increment button" /></button>
                    <span className={styles.qty}></span>
                    <button className={styles.btnQty}><img src={minus} alt="decrement button" /></button>
                    <button className={styles.btnRemove}>Remove</button>
                </div>
            </div>
    )
}