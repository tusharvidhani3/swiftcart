import styles from '../styles/Home.module.css'

export default function SearchSummary({rangeStart, rangeEnd, resultsCount, keyword, category}) {

    return keyword || category ? (
        <div className={`${styles.searchSummary}`}>
            <p>Showing <span className={styles.rangeStart}>{rangeStart}</span> - <span className={styles.rangeEnd}>{rangeEnd}</span> of <span className={styles.resultsCount}>{resultsCount}</span> results</p>
            <p>for &quot;<span className={styles.searchKeyword}>{keyword || category}</span>&quot;</p>
        </div>
    ):null
}