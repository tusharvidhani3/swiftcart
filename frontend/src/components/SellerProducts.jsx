import { useNavigate } from 'react-router'
import plusIcon from '../assets/icons/plus.svg'
import { useAuthFetch } from '../hooks/useAuthFetch'
import styles from '../styles/SellerProducts.module.css'
import { apiBaseUrl } from '../config'
import { useContext, useEffect, useState } from 'react'
import SellerProductCard from './SellerProductCard'
import searchIcon from '../assets/icons/search.svg'
import ProductsContext from '../contexts/ProductsContext'
import { Loader2 } from 'lucide-react'

export default function SellerProducts() {

    const [keyword, setKeyword] = useState("")
    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()
    const { productsPagedModel, setProductsPagedModel } = useContext(ProductsContext)
    const [threeDotsMenuOpen, setThreeDotsMenuOpen] = useState(null)

    async function getProducts() {
        const res = await authFetch(`${apiBaseUrl}/api/products?includeOutOfStock=true${keyword?'?keyword='+keyword:''}`, {
            method: 'GET',
        })
        const pagedModel = await res.json()
        setProductsPagedModel(pagedModel)
    }

    useEffect(() => {
        const init = async () => await getProducts()
        init()
    }, [])

    return productsPagedModel ? (
        <>
        <h1 className={styles.h1}>Listed Products</h1>
            <section className={styles.secAction}>
                <button className={styles.btnAddProduct} onClick={() => navigate('/seller/products/add')}><img src={plusIcon} alt='plus' /> Add Product</button>
            </section>

            <form className={styles.searchbox} onSubmit={e => {
                e.preventDefault()
                getProducts()
            }}>
                <input type="search" name="keyword" placeholder="Search for products" value={keyword} onChange={e => setKeyword(e.target.value)} />
                <button><img src={searchIcon} alt="search button" /></button>
            </form>

            <section className={styles.productsContainer} onClick={() => threeDotsMenuOpen?setThreeDotsMenuOpen(null) : undefined}>
                {productsPagedModel.products.map(product => <SellerProductCard key={product.productId} product={product} threeDotsMenuOpen={threeDotsMenuOpen} setThreeDotsMenuOpen={setThreeDotsMenuOpen} />)}
            </section>
        </>
    ) : <Loader2 className='animate-spin' />
}