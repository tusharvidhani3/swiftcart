import { useState, useEffect } from "react";
import ProductsContainer from "./ProductsContainer";
import SearchSummary from "./SearchSummary";
import { useLocation } from "react-router";

export default function Home() {

    const location = useLocation()
    const searchParams = location.state || {}
    const [products, setProducts] = useState([])
    const [searchSummary, setSearchSummary] = useState({})

    async function fetchProducts({ keyword, category, minPrice, maxPrice, sortBy, sortOrder, inStock }) {
        const baseUrl = "http://localhost:8080/api/products"
        const filters = []
        if (keyword) filters.push(`keyword=${keyword}`)
        if (category) filters.push(`category=${category}`)
        if (filters.length) {
            if (minPrice !== undefined && minPrice !== "") filters.push(`minPrice=${minPrice}`);
            if (maxPrice !== undefined && maxPrice !== "") filters.push(`maxPrice=${maxPrice}`);
            if (sortBy) filters.push(`sortBy=${sortBy}`);
            if (sortOrder) filters.push(`sortOrder=${sortOrder}`);
            if (inStock !== undefined && inStock !== "") filters.push(`inStock=${inStock}`);
        }
        const res = await fetch(filters.length ? `${baseUrl}?${filters.join("&")}` : baseUrl, {
            method: 'GET'
        })
        const productsPage = await res.json()
        const rangeStart = productsPage.number * productsPage.size + productsPage.numberOfElements ? 1 : 0
        const rangeEnd = productsPage.number * productsPage.size + productsPage.numberOfElements
        const resultsCount = productsPage.totalElements
        console.log(productsPage)
        setProducts(productsPage.content)
        if (keyword)
            setSearchSummary({ rangeStart, rangeEnd, resultsCount, keyword, category })
    }

    useEffect(() => {
        const init = async () => await fetchProducts(searchParams)
        init()
    }, [])

    return (
        <>
            <SearchSummary {...searchSummary} />
            <ProductsContainer products={products}></ProductsContainer>
        </>
    )
}