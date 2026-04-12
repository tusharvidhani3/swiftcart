import styles from "../styles/Home.module.css"
import ProductCard from "./ProductCard"
import ProductCardSkeleton from "./ProductCardSkeleton"

export default function ProductsContainer({ products }) {

    return (
        <div className={styles.productsContainer}>
            {products ?
            products.map(product => <ProductCard {...product} key={product.id} />)
            :
            new Array(12).fill(0).map((ele, i) => <ProductCardSkeleton key={i} />)
            }
        </div>
    )
}