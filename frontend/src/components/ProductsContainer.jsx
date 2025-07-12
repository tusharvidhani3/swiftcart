import { useContext, useEffect, useState } from "react"
import styles from "../styles/index.module.css"
import ProductCard from "./ProductCard"

export default function ProductsContainer({ products }) {

    return (
        <div className={styles.productsContainer}>
            {products.map(product => <ProductCard {...product} key={product.productId} />)}
        </div>
    )
}