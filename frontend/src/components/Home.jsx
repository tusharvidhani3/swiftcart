import { useState, useEffect, useContext, useMemo } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import SortMenu from './SortMenu'
import FilterMenu from './FilterMenu'
import { useSearchParams } from "react-router";
import UIContext from "../contexts/UIContext";
import styles from '../styles/Home.module.css'
import { ArrowDownWideNarrow, SlidersHorizontal } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import { apiBaseUrl } from "@/config";

async function fetchProducts({ keyword, categories, minPrice, maxPrice, sortBy, sortOrder, includeOutOfStock }) {
    const res = await axios.get(`${apiBaseUrl}/api/products`, {
        params: {
            keyword: keyword || undefined,
            categories: categories?.length ? categories : undefined,
            minPrice: (minPrice || minPrice === 0) ? Number(minPrice) : undefined,
            maxPrice: maxPrice ? Number(maxPrice) : undefined,
            sortBy: sortBy,
            sortOrder: sortOrder === 'desc' ? sortOrder : undefined,
            includeOutOfStock: includeOutOfStock || undefined
        }
    })
    return res.data
}

export default function Home() {

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

    const { data: productsPagedModel, isLoading } = useQuery({
        queryKey: ['products', 'search', { keyword, categories: categories.join(','), minPrice, maxPrice, sortBy, sortOrder, includeOutOfStock }],
        queryFn: () => fetchProducts({ keyword, categories, minPrice, maxPrice, sortBy, sortOrder, includeOutOfStock }),
        staleTime: 1000 * 60 * 5
    })

    useEffect(() => {
        if (productsPagedModel) {
            const rangeStart = productsPagedModel.page.number * productsPagedModel.page.size + productsPagedModel.page.numberOfElements ? 1 : 0
            const rangeEnd = productsPagedModel.page.number * productsPagedModel.page.size + productsPagedModel.page.numberOfElements
            const resultsCount = productsPagedModel.page.totalElements
            setSearchSummary({ rangeStart, rangeEnd, resultsCount, keyword, categories })
        }
    }, [productsPagedModel])

    useEffect(() => {
        if (isMobile && (sortDropDownOpen || filterMenuOpen)) {
            document.body.classList.add("no-scroll");
        } else {
            document.body.classList.remove("no-scroll");
        }

        return () => document.body.classList.remove("no-scroll");
    }, [sortDropDownOpen, filterMenuOpen]);

    return (
        <>
            {(keyword || categories.length > 0) && <FilterMenu filterMenuOpen={filterMenuOpen} setFilterMenuOpen={setFilterMenuOpen} />}
            <div className={styles.mainWindow}>
                {(keyword || categories.length > 0) && isMobile && <div className={styles.searchFilters}><button onClick={() => setSortDropDownOpen(!sortDropDownOpen)} className={styles.btnSort}><ArrowDownWideNarrow /> Sort</button> <button className={styles.btnFilter} onClick={() => setFilterMenuOpen(true)}><SlidersHorizontal />Filter</button></div>}
                <SearchSummary {...searchSummary} />
                {(keyword || categories.length > 0) && <SortMenu sortDropDownOpen={sortDropDownOpen} setSortDropDownOpen={setSortDropDownOpen} />}
                { productsPagedModel?.page?.totalElements > 0 ? <ProductsContainer products={productsPagedModel._embedded.productResponseList} /> : <div className="flex justify-center items-center">No products found</div> }
            </div>
        </>
    )
}