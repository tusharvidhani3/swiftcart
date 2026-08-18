import { Link, NavLink } from "react-router";
import { CircleUser, Package, MapPin, LogOut, LogIn, ShoppingCart, X, Tag, LayoutDashboard } from 'lucide-react'
import styles from '../styles/SideMenu.module.css'
import { useContext, useEffect } from "react";
import UserContext from "../contexts/UserContext";
import { api } from "@/api/client";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export default function SideMenu({ isSideMenuOpen, setSideMenuOpen }) {

    const { user } = useContext(UserContext)

    const queryClient = useQueryClient()

    const { mutate: logout } = useMutation({
        mutationFn: () => api.post('/auth/logout'),
        onSuccess: () => {
            queryClient.resetQueries({ queryKey: ['user'], exact: true });
        }
    })

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
                    {user?.role === 'ROLE_SELLER' ?
                        <>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller' end><LayoutDashboard color="black" />Dashboard</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/seller/orders" end><Package color="black" />Orders</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to='/seller/products' end><Tag color="black" />Products</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/profile" end><CircleUser color="black" />Profile</NavLink>
                        </>
                        :
                        <>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/orders"><Package color="black" />Orders</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/cart"><ShoppingCart color="black" />Cart</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/profile"><CircleUser color="black" />Profile</NavLink>
                            <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/addresses"><MapPin color="black" />Saved Addresses</NavLink>
                        </>}
                    {user ? <Link onClick={() => logout()}><LogOut color="black" />Logout</Link> : <NavLink className={({ isActive }) => isActive ? styles.activeLink : ''} to="/auth/login"><LogIn color="black" />Login</NavLink>}
                </nav>
                {isSideMenuOpen && <button className={styles.btnClose} onClick={e => {
                    e.stopPropagation()
                    setSideMenuOpen(false)
                }}><X /></button>}
            </aside>
        </>
    )
}