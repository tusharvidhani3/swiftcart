import { useNavigate } from 'react-router'
import { CirclePlus, Search, Loader2 } from 'lucide-react'
import styles from '../styles/SellerProducts.module.css'
import { useState } from 'react'
import SellerProductCard from './SellerProductCard'
import { useQuery } from '@tanstack/react-query'
import { api } from '@/api/client'

export default function SellerProducts() {

    const [keyword, setKeyword] = useState("")
    const navigate = useNavigate()
    const [threeDotsMenuOpen, setThreeDotsMenuOpen] = useState(null)

    const { data: productsPagedModel, isLoading } = useQuery({
        queryKey: ['products',],
        queryFn: () => api.get(`/products/seller?${keyword ? '?keyword=' + keyword : ''}`),
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