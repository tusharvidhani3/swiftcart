import { useState, useEffect } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import { useOutletContext } from "react-router";
import SearchFilters from "./SearchFilters";

export default function Home() {

    const [products, setProducts] = useState([])
    const [searchSummary, setSearchSummary] = useState({})
    const { keyword } = useOutletContext()
    const [searchFilters, setSearchFilters] = useState({
        category: "",
        minPrice: "",
        maxPrice: "",
        sortBy: "price",
        sortOrder: "",
        inStock: true
    })

    async function fetchProducts({ category, minPrice, maxPrice, sortBy, sortOrder, inStock }) {
        const baseUrl = "http://localhost:8080/api/products"
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
        const productsPage = await res.json()
        const rangeStart = productsPage.number * productsPage.size + productsPage.numberOfElements ? 1 : 0
        const rangeEnd = productsPage.number * productsPage.size + productsPage.numberOfElements
        const resultsCount = productsPage.totalElements
        setProducts(productsPage.content)
        setSearchSummary({ rangeStart, rangeEnd, resultsCount, keyword, category })
    }

    useEffect(() => {
        const init = async () => await fetchProducts(searchFilters)
        init()
    }, [keyword, searchFilters])

    return (
        <>
            <SearchSummary {...searchSummary} />
            <SearchFilters searchFilters={searchFilters} setSearchFilters={setSearchFilters} />
            <ProductsContainer products={products} />
        </>
    )
}