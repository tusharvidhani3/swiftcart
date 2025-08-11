import styles from "../styles/Home.module.css"
import ProductCard from "./ProductCard"
import ProductCardShimmer from "./ProductCardShimmer"

export default function ProductsContainer({ products }) {

    return (
        <div className={styles.productsContainer}>
            {products.length ?
            products.map(product => <ProductCard {...product} key={product.productId} />)
            :
            new Array(12).fill(0).map((ele, i) => <ProductCardShimmer key={i} />)
            }
        </div>
    )
}