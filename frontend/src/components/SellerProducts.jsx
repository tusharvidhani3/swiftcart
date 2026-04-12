import { useNavigate } from 'react-router'
import { CirclePlus, Search, Loader2 } from 'lucide-react'
import { useAuthFetch } from '../hooks/useAuthFetch'
import styles from '../styles/SellerProducts.module.css'
import { apiBaseUrl } from '../config'
import { useState } from 'react'
import SellerProductCard from './SellerProductCard'
import { useQuery } from '@tanstack/react-query'

async function getProducts(authFetch, keyword) {
    const res = await authFetch(`${apiBaseUrl}/api/products/seller?${keyword ? '?keyword=' + keyword : ''}`, {
        method: 'GET',
    })
    return await res.json()
}

export default function SellerProducts() {

    const [keyword, setKeyword] = useState("")
    const authFetch = useAuthFetch()
    const navigate = useNavigate()
    const [threeDotsMenuOpen, setThreeDotsMenuOpen] = useState(null)

    const { data: productsPagedModel, isLoading } = useQuery({
        queryKey: ['products',],
        queryFn: () => getProducts(authFetch, keyword),
        staleTime: 1000 * 60 * 5
    })

    return isLoading ? <SellerProductsSkeleton /> : (
        <>
            <h1 className={styles.h1}>Listed Products</h1>
            <section className={styles.secAction}>
                <button className={styles.btnAddProduct} onClick={() => navigate('/seller/products/add')}><CirclePlus /> Add Product</button>
            </section>

            <form className={styles.searchbox} onSubmit={e => {
                e.preventDefault()
                getProducts()
            }}>
                <input type="search" name="keyword" placeholder="Search for products" value={keyword} onChange={e => setKeyword(e.target.value)} />
                <button><Search /></button>
            </form>

            <section className={styles.productsContainer} onClick={() => threeDotsMenuOpen ? setThreeDotsMenuOpen(null) : undefined}>
                {productsPagedModel._embedded.productResponseList?.map(product => <SellerProductCard key={product.id} product={product} threeDotsMenuOpen={threeDotsMenuOpen} setThreeDotsMenuOpen={setThreeDotsMenuOpen} />)}
            </section>
        </>
    )
}