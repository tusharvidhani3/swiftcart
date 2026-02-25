import { useState, useEffect, useContext, useMemo } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import SortMenu from './SortMenu'
import FilterMenu from './FilterMenu'
import { useSearchParams } from "react-router";
import sortIcon from '../assets/icons/sort.svg'
import filterIcon from '../assets/icons/filter.svg'
import { apiBaseUrl } from "../config";
import UIContext from "../contexts/UIContext";
import styles from '../styles/Home.module.css'
import { useApi } from "../hooks/useApi";

export default function Home() {

    const [productsPagedModel, setProductsPagedModel] = useState(null)
    const [searchSummary, setSearchSummary] = useState({})
    const { isMobile } = useContext(UIContext)
    const [sortDropDownOpen, setSortDropDownOpen] = useState(false)
    const [filterMenuOpen, setFilterMenuOpen] = useState(false)
    const [searchParams] = useSearchParams()
    const keyword = searchParams.get('k')
    const categories = useMemo(() => searchParams.getAll('categories'), [searchParams])
    const minPrice = searchParams.get('min_price')
    const maxPrice = searchParams.get('max_price')
    const sort = searchParams.get('sort')
    const [sortBy, sortOrder] = sort ? sort.split('_') : []
    const includeOutOfStock = searchParams.get('include_out_of_stock')
    const apiFetch = useApi()

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
        const res = await apiFetch(`${baseUrl}${filters.length ? '?'+filters.join("&") : ''}`, {
            method: 'GET'
        })
        const pagedModel = await res.json()
        const rangeStart = pagedModel.number * pagedModel.size + pagedModel.numberOfElements ? 1 : 0
        const rangeEnd = pagedModel.number * pagedModel.size + pagedModel.numberOfElements
        const resultsCount = pagedModel.totalElements
        setProductsPagedModel(pagedModel)
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

    console.log(productsPagedModel)

    return (
        <>
            {(keyword || categories.length>0) && <FilterMenu filterMenuOpen={filterMenuOpen} setFilterMenuOpen={setFilterMenuOpen} />}
            <div className={styles.mainWindow}>
                {(keyword || categories.length>0) && isMobile && <div className={styles.searchFilters}><button onClick={() => setSortDropDownOpen(!sortDropDownOpen)} className={styles.btnSort}><img src={sortIcon} /> Sort</button> <button className={styles.btnFilter} onClick={() => setFilterMenuOpen(true)}><img src={filterIcon} />Filter</button></div>}
                <SearchSummary {...searchSummary} />
                {(keyword || categories.length>0) && <SortMenu sortDropDownOpen={sortDropDownOpen} setSortDropDownOpen={setSortDropDownOpen} />}
                <ProductsContainer products={productsPagedModel?._embedded.productResponseList} />
            </div>
        </>
    )
}