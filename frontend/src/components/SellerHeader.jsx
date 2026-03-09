import styles from '../styles/SellerHeader.module.css'
import hamburgerIcon from '../assets/icons/hamburger.svg'
import { useContext } from 'react'
import UIContext from '../contexts/UIContext'

export default function SellerHeader() {

    const { setSideMenuOpen } = useContext(UIContext)

    return (
        <header className={styles.header}>
            <button className={styles.hamburger} onClick={e => {
                e.stopPropagation()
                setSideMenuOpen(true)
            }}><img src={hamburgerIcon} alt="hamburger" /></button>
        </header>
    )
}