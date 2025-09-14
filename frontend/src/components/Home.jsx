import { useState, useEffect, useContext } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import SortMenu from './SortMenu'
import FilterMenu from './FilterMenu'
import { useOutletContext, useSearchParams } from "react-router";
import sortIcon from '../assets/icons/sort.svg'
import filterIcon from '../assets/icons/filter.svg'
import { apiBaseUrl } from "../config";
import UIContext from "../contexts/UIContext";
import styles from '../styles/Home.module.css'

export default function Home() {

    const [products, setProducts] = useState(null)
    const [searchSummary, setSearchSummary] = useState({})
    const { isMobile } = useContext(UIContext)
    const [sortDropDownOpen, setSortDropDownOpen] = useState(false)
    const [filterMenuOpen, setFilterMenuOpen] = useState(false)
    const [searchParams] = useSearchParams()
    const keyword = searchParams.get('k')
    const categories = searchParams.getAll('categories')
    const minPrice = searchParams.get('min_price')
    const maxPrice = searchParams.get('max_price')
    const sort = searchParams.get('sort')
    const [sortBy, sortOrder] = sort ? sort.split('_') : []
    const includeOutOfStock = searchParams.get('include_out_of_stock')

    async function fetchProducts() {
        const baseUrl = `${apiBaseUrl}/api/products`
        const filters = []
        if (keyword) filters.push(`keyword=${keyword}`)
        if (categories.length) {
            categories.forEach(category => {
                filters.push(`categories=${category}`)
            })
        }
        if (filters.length) {
            if (minPrice || minPrice === 0) filters.push(`minPrice=${Number(minPrice)}`);
            if (maxPrice) filters.push(`maxPrice=${Number(maxPrice)}`);
            if (sortBy) filters.push(`sortBy=${sortBy}`);
            if (sortOrder === 'desc') filters.push(`sortOrder=${sortOrder}`);
            if (includeOutOfStock) filters.push(`includeOutOfStock=${includeOutOfStock}`)
        }
        const res = await fetch(`${baseUrl}?${filters.join("&")}`, {
            method: 'GET'
        })
        const productsPage = await res.json()
        const rangeStart = productsPage.number * productsPage.size + productsPage.numberOfElements ? 1 : 0
        const rangeEnd = productsPage.number * productsPage.size + productsPage.numberOfElements
        const resultsCount = productsPage.totalElements
        setProducts(productsPage.content)
        setSearchSummary({ rangeStart, rangeEnd, resultsCount, keyword, categories })
    }

    useEffect(() => {
        if (isMobile && (sortDropDownOpen || filterMenuOpen)) {
            document.body.classList.add("no-scroll");
        } else {
            document.body.classList.remove("no-scroll");
        }

        return () => document.body.classList.remove("no-scroll");
    }, [sortDropDownOpen, filterMenuOpen]);

    useEffect(() => {
        const init = async () => await fetchProducts()
        init()
    }, [keyword, categories, minPrice, maxPrice, sort, includeOutOfStock])

    return (
        <>
            {(keyword || categories.length>0) && <FilterMenu filterMenuOpen={filterMenuOpen} setFilterMenuOpen={setFilterMenuOpen} />}
            <div className={styles.mainWindow}>
                {(keyword || categories.length>0) && isMobile && <div className={styles.searchFilters}><button onClick={() => setSortDropDownOpen(!sortDropDownOpen)} className={styles.btnSort}><img src={sortIcon} /> Sort</button> <button className={styles.btnFilter} onClick={() => setFilterMenuOpen(true)}><img src={filterIcon} />Filter</button></div>}
                <SearchSummary {...searchSummary} />
                {(keyword || categories.length>0) && <SortMenu sortDropDownOpen={sortDropDownOpen} setSortDropDownOpen={setSortDropDownOpen} />}
                <ProductsContainer products={products} />
            </div>
        </>
    )
}