import { Link, NavLink } from "react-router";
import { CircleUser, Package, MapPin, LogOut, LogIn, ShoppingCart, X, Tag, LayoutDashboard } from 'lucide-react'
import styles from '../styles/SideMenu.module.css'
import { useContext, useEffect } from "react";
import UserContext from "../contexts/UserContext";
import { useApi } from "../hooks/useApi";

export default function SideMenu({ isSideMenuOpen, setSideMenuOpen }) {

    const { userInfo, logout } = useContext(UserContext)
    const apiFetch = useApi()

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
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller' end><LayoutDashboard />Dashboard</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/seller/orders" end><Package />Orders</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller/products' end><Tag />Products</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/profile" end><CircleUser />Profile</NavLink>
                        </>
                        :
                        <>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/orders"><Package />Orders</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/cart"><ShoppingCart />Cart</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/profile"><CircleUser />Profile</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/addresses"><MapPin />Saved Addresses</NavLink>
                        </>}
                    {userInfo ? <Link onClick={() => logout(apiFetch)}><LogOut />Logout</Link> : <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/auth/login"><LogIn color="black" />Login</NavLink>}
                </nav>
                {isSideMenuOpen && <button className={styles.btnClose} onClick={e => {
                    e.stopPropagation()
                    setSideMenuOpen(false)
                }}><X /></button>}
            </aside>
        </>
    )
}