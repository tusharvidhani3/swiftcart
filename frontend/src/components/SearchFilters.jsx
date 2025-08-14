import { useState } from 'react'
import dropDownArrow from '../assets/icons/drop-down-arrow.svg'
import styles from '../styles/Home.module.css'
import sortIcon from '../assets/icons/sort.svg'
import filterIcon from '../assets/icons/filter.svg'
import customRadio from '../assets/icons/radio.webp'
import selectedCustomRadio from '../assets/icons/selected-radio.webp'

export default function SearchFilters({ searchFilters, setSearchFilters }) {

    const [sortByDropDownOpen, setSortByDropDownOpen] = useState(false)

    return (
        <div className={styles.searchFilters}>
            <div className={styles.sortBy}>
                <button onClick={() => setSortByDropDownOpen(!sortByDropDownOpen)} className={styles.btnSort}><img src={sortIcon} /> Sort</button>
                {sortByDropDownOpen && <div className='backdrop-overlay' onClick={() => setSortByDropDownOpen(false)}></div>}
                <div className={`${styles.sortByDropDown} ${sortByDropDownOpen?styles.open:''}`}>
                    <h3 className={styles.sortByTitle}>SORT BY</h3>
                <ul className={styles.sortByDropDownList}>
                    <li onClick={() => {
                        setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "relevance" }))
                        setSortByDropDownOpen(false)
                        }}><div>Relevance</div><img src={searchFilters.sortBy==='relevance'?selectedCustomRadio:customRadio} className={styles.customRadio} /></li>
                    {/* <li onClick={() => setSearchFilters(searchFilters => ({...searchFilters, sortBy: "listedAt", sortOrder: "desc"}))}><div className={styles.customRadio}></div>Newest First</li> */}
                    <li onClick={() => {
                        setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "price", sortOrder: "asc" }))
                        setSortByDropDownOpen(false)
                        }}><div>Price: Low to High</div><img className={styles.customRadio} src={searchFilters.sortBy==='price' && searchFilters.sortOrder==='asc'?selectedCustomRadio:customRadio} /></li>
                    <li onClick={() => {
                        setSearchFilters(searchFilters => ({ ...searchFilters, sortBy: "price", sortOrder: "desc" }))
                        setSortByDropDownOpen(false)
                        }}><div>Price: High to Low</div><img className={styles.customRadio} src={searchFilters.sortBy==='price' && searchFilters.sortOrder==='desc'?selectedCustomRadio:customRadio} /></li>
                </ul>
                </div>
            </div>

            <div className={styles.filter}>
                <button className={styles.btnFilter}><img src={filterIcon} />Filter</button>
            </div>
        </div>
    )
}