import styles from '../styles/ProductCardShimmer.module.css'

export default function ProductCardShimmer() {

    return (
        <div className={styles.productCardShimmer}>
            <div className={styles.productImageShimmer} />
            <div className={styles.productTitleShimmer} />
            <div className={styles.productPriceInfoShimmer}>
                <span className={styles.productPriceShimmer} />
                <span className={styles.productMrpShimmer} />
            </div>
            <div className={styles.btnAddToCartShimmer} />
        </div>
    )
}