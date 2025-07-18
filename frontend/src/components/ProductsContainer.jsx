import styles from "../styles/Home.module.css"
import ProductCard from "./ProductCard"

export default function ProductsContainer({ products }) {

    return (
        <div className={styles.productsContainer}>
            {products.map(product => <ProductCard {...product} key={product.productId} />)}
        </div>
    )
}