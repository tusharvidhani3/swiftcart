import { Link } from "react-router";
import myProfile from "../assets/icons/my-profile.svg";
import orders from "../assets/icons/orders.svg";
import locationIcon from '../assets/icons/location.svg'
import logout from "../assets/icons/logout.svg";
import cartIcon from "../assets/icons/cart.svg";
import closeIcon from '../assets/icons/close-x.svg'
import styles from '../styles/SideMenu.module.css'
import { useContext } from "react";
import UserContext from "../contexts/UserContext";

export default function SideMenu({ isSideMenuOpen, setSideMenuOpen }) {

    const { handleLogout } = useContext(UserContext)

    return (
        <aside className={`${styles.sideMenu} ${isSideMenuOpen?styles.open:''}`} onClick={e => {
            e.stopPropagation()
            if(e.target.closest('a') || e.target.closest('button'))
                setSideMenuOpen(false)
            }}>
            <div className={styles.banner}></div>
            <nav className={styles.navLinks}>
                <Link to="/orders"><img src={orders} />Orders</Link>
                <Link to="/cart"><img src={cartIcon} />Cart</Link>
                <Link to="/profile"><img src={myProfile} />Profile</Link>
                <Link to="/addresses" className={styles.addresses}><img src={locationIcon} />Saved Addresses</Link>
                <Link onClick={handleLogout}><img src={logout} />Logout</Link>
            </nav>
            <button className={styles.btnClose} onClick={e => {
                e.stopPropagation()
                setSideMenuOpen(false)
                }}><img src={closeIcon} /></button>
        </aside>
    )
}