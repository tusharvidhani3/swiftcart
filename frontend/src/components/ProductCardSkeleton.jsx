import styles from '../styles/ProductCardSkeleton.module.css'
import { Skeleton } from './ui/skeleton'

export default function ProductCardSkeleton() {

    return (
        <div className={styles.productCardSkeleton}>
            <Skeleton className={styles.productImageSkeleton} />
            <Skeleton className={styles.productTitleSkeleton} />
            <div className={styles.productPriceInfoSkeleton}>
                <Skeleton className={styles.productPriceSkeleton} />
                <Skeleton className={styles.productMrpSkeleton} />
            </div>
            <Skeleton className={styles.btnAddToCartSkeleton} />
        </div>
    )
}