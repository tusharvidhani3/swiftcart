import styles from '../styles/Home.module.css'
import backIcon from '../assets/icons/left-arrow.svg'
import { useContext, useEffect, useState } from 'react'
import UIContext from '../contexts/UIContext'
import { useSearchParams } from 'react-router'

export default function FilterMenu({ filterMenuOpen, setFilterMenuOpen }) {

    const [selectedAttribute, setSelectedAttribute] = useState(0)
    const { isMobile } = useContext(UIContext)
    const [searchParams, setSearchParams] = useSearchParams()

    const [filters, setFilters] = useState({
        price: {
            minPrice: searchParams.get('min_price') || '',
            maxPrice: searchParams.get('max_price') || ''
        },
        categories: new Set(),
        availability: {
            includeOutOfStock: searchParams.get('include_out_of_stock') || false
        }
    })

    function applyFilters() {
        setSearchParams(prev => {
            const params = new URLSearchParams(prev);
            if (filters.price.minPrice)
                params.set("min_price", filters.price.minPrice);
            else
                params.delete("min_price")
            if (filters.price.maxPrice)
                params.set("max_price", filters.price.maxPrice);
            else
                params.delete("max_price")
            if(filters.categories.size) {
                filters.categories.forEach(category => params.append("categories", category))
            }
            else {
                params.delete("categories")
            }
            if (filters.availability.includeOutOfStock)
                params.set("include_out_of_stock", true)
            else
                params.delete("include_out_of_stock")
            return params;
        })
    }

    useEffect(() => {
        if(!isMobile)
        applyFilters()
    }, [filters])

    return (
        (isMobile && filterMenuOpen || !isMobile) &&
        <>
            {isMobile && filterMenuOpen && <div className='backdrop-overlay'></div>}
            <div className={styles.filterMenu}>
                <div className={styles.filterMenuHeader}>
                    {isMobile && <button className={styles.btnBack} onClick={() => setFilterMenuOpen(false)}><img src={backIcon} alt="back" /></button>}
                    <h2>Filters</h2>
                </div>
                <div className={styles.filtersList}>
                    {isMobile && <div className={styles.filterAttributes}>
                        <div className={`${styles.filterAttribute} ${selectedAttribute === 0 ? styles.selected : ''}`} onClick={() => setSelectedAttribute(0)}>Price</div>
                        <div className={`${styles.filterAttribute} ${selectedAttribute === 1 ? styles.selected : ''}`} onClick={() => setSelectedAttribute(1)}>Category</div>
                        <div className={`${styles.filterAttribute} ${selectedAttribute === 2 ? styles.selected : ''}`} onClick={() => setSelectedAttribute(2)}>Availability</div>
                    </div>}

                    <div className={styles.filters}>
                        {!isMobile && <h3>Price</h3>}
                        {(isMobile && selectedAttribute === 0 || !isMobile) && <div className={styles.priceFilters}>
                            <div>
                                <label htmlFor="minPrice">Min Price</label>
                                <label htmlFor="maxPrice">Max Price</label>
                            </div>
                            <div>
                                <input type="number" id='minPrice' value={filters.price.minPrice} onChange={e => setFilters(filters => ({ ...filters, price: { ...filters.price, minPrice: e.target.value } }))} />
                                <input type="number" id='maxPrice' value={filters.price.maxPrice} onChange={e => setFilters(filters => ({ ...filters, price: { ...filters.price, maxPrice: e.target.value } }))} />
                            </div>
                        </div>}

                        {(isMobile && selectedAttribute === 1 || !isMobile) && <div className={styles.categoryFilters}>
                            {!isMobile && <h3>Category</h3>}
                            <div className={styles.filterOption}>
                                <input type="checkbox" id="smartphones" checked={filters.categories.has("smartphones")} onChange={e => setFilters(filters => {
                                    const newCategories = new Set(filters.categories)
                                    if(newCategories.has("smartphones"))
                                        newCategories.delete("smartphones")
                                    else
                                        newCategories.add("smartphones")
                                    return { ...filters, categories: newCategories }
                                })} />
                                <label htmlFor="smartphones">Smartphones</label>
                            </div>
                        </div>
                        }

                        {(isMobile && selectedAttribute === 2 || !isMobile) && <div className={styles.availabilityFilters}>
                            {!isMobile && <h3>Availability</h3>}
                            <div className={styles.filterOption}>
                                <input type="checkbox" id="includeOutOfStock" checked={Boolean(filters.availability.includeOutOfStock)} onChange={e => setFilters(filters => ({ ...filters, availability: { ...filters.availability, includeOutOfStock: !filters.availability.includeOutOfStock } }))} />
                                <label htmlFor="includeOutOfStock">Include Out of Stock</label>
                            </div>
                        </div>}
                    </div>
                </div>
                {isMobile && <div className={styles.actionFilters}>
                    <button className={styles.btnApplyFilters} onClick={() => {
                        applyFilters()
                        setFilterMenuOpen(false)
                        }}>Apply</button>
                </div>}
            </div>
        </>
    )
}