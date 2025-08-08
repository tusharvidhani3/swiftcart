import { useState } from 'react'
import dropDownArrow from '../assets/icons/drop-down-arrow.svg'
import styles from '../styles/Home.module.css'

export default function SearchFilters({ searchFilters, setSearchFilters }) {

    const [sortByDropDownOpen, setSortByDropDownOpen] = useState(false)

    return (
        <div className={styles.searchFilters}>
            <div className={styles.sortBy}>
                <div onClick={() => setSortByDropDownOpen(!sortByDropDownOpen)}>
                    <button className={styles.btnSort}>Sort</button>
                </div>
                {sortByDropDownOpen && <>
                <div className={styles.backdropOverlay}></div>
                <ul className={styles.sortByDropDown}>
                    <li onClick={() => setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "relevance" }))}>Relevance</li>
                    {/* <li onClick={() => setSearchFilters(searchFilters => ({...searchFilters, sortBy: "listedAt", sortOrder: "desc"}))}>Newest First</li> */}
                    <li onClick={() => setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "price", sortOrder: "asc" }))}>Price: Low to High</li>
                    <li onClick={() => setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "price", sortOrder: "desc" }))}>Price: High to Low</li>
                </ul>
                </>}
            </div>

            <div className={styles.filter}>
                <button className={styles.btnFilter}>Filter</button>
            </div>
        </div>
    )
}