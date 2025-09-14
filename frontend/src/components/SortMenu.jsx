import { useContext, useState } from 'react'
import dropDownArrow from '../assets/icons/drop-down-arrow.svg'
import styles from '../styles/Home.module.css'
import UIContext from '../contexts/UIContext'
import customRadio from '../assets/icons/radio.webp'
import selectedCustomRadio from '../assets/icons/selected-radio.webp'
import { useSearchParams } from 'react-router'

export default function SortMenu({ sortDropDownOpen, setSortDropDownOpen }) {

    const { isMobile } = useContext(UIContext)
    const [searchParams, setSearchParams] = useSearchParams()

    const sort = searchParams.get('sort') || 'relevance'

    return (
        <div className={styles.sortBy}>
            {isMobile && sortDropDownOpen && <div className='backdrop-overlay' onClick={() => setSortDropDownOpen(false)}></div>}
            <div className={`${styles.sortByDropDown} ${sortDropDownOpen ? styles.open : ''}`}>
                <h3 className={styles.sortByTitle}>Sort By</h3>
                <ul className={styles.sortByDropDownList}>
                    <li className={sort === 'relevance' ? styles.selected : ''} onClick={() => {
                        setSearchParams(prev => {
                            const params = new URLSearchParams(prev);
                            params.set("sort", "relevance");
                            return params;
                        })
                        setSortDropDownOpen(false)
                    }}><div>Relevance</div><img src={sort === 'relevance' ? selectedCustomRadio : customRadio} className={styles.customRadio} /></li>
                    {/* <li onClick={() => setSearchFilters(searchFilters => ({...searchFilters, sortBy: "listedAt", sortOrder: "desc"}))}><div className={`${styles.customRadio} ${sortBy === 'relevance' ? styles.selected : ''}`}></div>Newest First</li> */}
                    <li className={sort === 'price' ? styles.selected : ''} onClick={() => {
                        setSearchParams(prev => {
                            const params = new URLSearchParams(prev);
                            params.set("sort", "price");
                            return params;
                        })
                        setSortDropDownOpen(false)
                    }}><div>Price: Low to High</div><img className={styles.customRadio} src={sort === 'price' ? selectedCustomRadio : customRadio} /></li>
                    <li className={sort === 'price_desc' ? styles.selected : ''} onClick={() => {
                        setSearchParams(prev => {
                            const params = new URLSearchParams(prev);
                            params.set("sort", "price_desc");
                            return params;
                        })
                        setSortDropDownOpen(false)
                    }}><div>Price: High to Low</div><img className={styles.customRadio} src={sort === 'price_desc' ? selectedCustomRadio : customRadio} /></li>
                </ul>
            </div>
        </div>
    )
}