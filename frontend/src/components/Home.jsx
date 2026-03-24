import { useState, useEffect } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import { useOutletContext } from "react-router";
import SearchFilters from "./SearchFilters";
import { apiBaseUrl } from "../config";

export default function Home() {

    const [products, setProducts] = useState([])
    const [searchSummary, setSearchSummary] = useState({})
    const { keyword } = useOutletContext()
    const [searchFilters, setSearchFilters] = useState({
        category: "",
        minPrice: "",
        maxPrice: "",
        sortBy: "price",
        sortOrder: "desc",
        inStock: true
    })

    async function fetchProducts({ category, minPrice, maxPrice, sortBy, sortOrder, inStock }) {
        const baseUrl = `${apiBaseUrl}/api/products`
        const filters = []
        if (keyword) filters.push(`keyword=${keyword}`)
        if (category) filters.push(`category=${category}`)
        if (filters.length) {
            if (minPrice !== undefined && minPrice !== "") filters.push(`minPrice=${minPrice}`);
            if (maxPrice !== undefined && maxPrice !== "") filters.push(`maxPrice=${maxPrice}`);
            if (sortBy) filters.push(`sortBy=${sortBy}`);
            if (sortOrder) filters.push(`sortOrder=${sortOrder}`);
        }
        filters.push(`inStock=${inStock}`);
        const res = await fetch(`${baseUrl}?${filters.join("&")}`, {
            method: 'GET'
        })
        const pagedModel = await res.json()
        const rangeStart = pagedModel.number * pagedModel.size + pagedModel.numberOfElements ? 1 : 0
        const rangeEnd = pagedModel.number * pagedModel.size + pagedModel.numberOfElements
        const resultsCount = pagedModel.totalElements
        console.log(pagedModel)
        setProductsPagedModel(pagedModel)
        setSearchSummary({ rangeStart, rangeEnd, resultsCount, keyword, categories })
    }

    useEffect(() => {
        const init = async () => await fetchProducts(searchFilters)
        init()
    }, [keyword, categories, minPrice, maxPrice, sort, includeOutOfStock])

    return (
        <>
            {(keyword || categories.length>0) && <FilterMenu filterMenuOpen={filterMenuOpen} setFilterMenuOpen={setFilterMenuOpen} />}
            <div className={styles.mainWindow}>
                {(keyword || categories.length>0) && isMobile && <div className={styles.searchFilters}><button onClick={() => setSortDropDownOpen(!sortDropDownOpen)} className={styles.btnSort}><img src={sortIcon} /> Sort</button> <button className={styles.btnFilter} onClick={() => setFilterMenuOpen(true)}><img src={filterIcon} />Filter</button></div>}
                <SearchSummary {...searchSummary} />
                {(keyword || categories.length>0) && <SortMenu sortDropDownOpen={sortDropDownOpen} setSortDropDownOpen={setSortDropDownOpen} />}
                <ProductsContainer products={productsPagedModel?._embedded?.productResponseList} />
            </div>
        </>
    )
}