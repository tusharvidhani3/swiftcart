import { Link, NavLink } from "react-router";
import myProfile from "../assets/icons/my-profile.svg";
import orders from "../assets/icons/orders.svg";
import locationIcon from '../assets/icons/location.svg'
import logout from "../assets/icons/logout.svg";
import login from '../assets/icons/login.svg'
import cartIcon from "../assets/icons/cart.svg";
import closeIcon from '../assets/icons/close-x.svg'
import styles from '../styles/SideMenu.module.css'
import { useContext, useEffect } from "react";
import UserContext from "../contexts/UserContext";
import dashboardIcon from '../assets/icons/dashboard.svg'
import productTagIcon from '../assets/icons/product-tag.svg'

export default function SideMenu({ isSideMenuOpen, setSideMenuOpen }) {

    const { userInfo, handleLogout } = useContext(UserContext)

    useEffect(() => {
        if (isSideMenuOpen) {
            document.body.classList.add("no-scroll");
        } else {
            document.body.classList.remove("no-scroll");
        }

        return () => document.body.classList.remove("no-scroll");
    }, [isSideMenuOpen]);


    return (
        <>
            {isSideMenuOpen && <div className="backdrop-overlay" onClick={() => setSideMenuOpen(false)} />}
            <aside className={`${styles.sideMenu} ${isSideMenuOpen ? styles.open : ''}`} onClick={e => {
                e.stopPropagation()
                if (e.target.closest('a') || e.target.closest('button'))
                    setSideMenuOpen(false)
            }}>
                <div className={styles.banner}></div>
                <nav className={styles.navLinks}>
                    {userInfo?.role === 'ROLE_SELLER' ?
                        <>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller' end><img src={dashboardIcon} />Dashboard</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/seller/orders" end><img src={orders} />Orders</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller/products' end><img src={productTagIcon} alt="products" />Products</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/profile" end><img src={myProfile} />Profile</NavLink>
                        </>
                        :
                        <>
                            <Link to="/orders"><img src={orders} />Orders</Link>
                            <Link to="/cart"><img src={cartIcon} />Cart</Link>
                            <Link to="/profile"><img src={myProfile} />Profile</Link>
                            <Link to="/addresses" className={styles.addresses}><img src={locationIcon} />Saved Addresses</Link>
                        </>}
                    {userInfo ? <Link onClick={handleLogout}><img src={logout} />Logout</Link> : <Link to="/auth/login"><img src={login} />Login</Link>}
                </nav>
                {isSideMenuOpen && <button className={styles.btnClose} onClick={e => {
                    e.stopPropagation()
                    setSideMenuOpen(false)
                }}><img src={closeIcon} /></button>}
            </aside>
        </>
    )
}