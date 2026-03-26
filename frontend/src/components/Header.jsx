import { Menu, Search, User, ChevronDown, CircleUser, Package, MapPin, LogOut, ShoppingCart } from 'lucide-react'
import styles from "../styles/Header.module.css";
import { Link, useLocation, useSearchParams } from "react-router";
import { useNavigate } from "react-router";
import { useContext, useState } from "react";
import UserContext from "../contexts/UserContext";
import CartContext from "../contexts/CartContext";

function Header({ setSideMenuOpen }) {

    const { userInfo, handleLogout } = useContext(UserContext)
    const navigate = useNavigate()
    const [searchParams, setSearchParams] = useSearchParams()
    const keyword = searchParams.get('k') || ""
    const [currKeyword, setCurrKeyword] = useState(keyword)
    const location = useLocation()
    const [showProfileDropDown, setShowProfileDropDown] = useState(false)
    const { cart } = useContext(CartContext)

    function handleSearch() {
        setSearchParams({ k: currKeyword })
        if (location.pathname !== '/')
            navigate('/')
    }

    return (
        <header className={styles.header}>
            <div className={styles.headerLeft}>
                <button className={styles.hamburger} onClick={e => {
                    e.stopPropagation()
                    setSideMenuOpen(true)
                }}><Menu /></button>
                <Link to="/" onClick={() => setSearchParams({})} className={`${styles.swiftcart} ${styles.link}`}>
                    <span>SwiftCart</span>
                </Link>
            </div>
            <form className={styles.searchbox} onSubmit={e => {
                e.preventDefault()
                handleSearch()
            }}>
                <input type="search" name="keyword" placeholder="Search for products" value={currKeyword} onChange={e => setCurrKeyword(e.target.value)} />
                <button><Search /></button>
            </form>
            <div className={`${styles.headerRight} ${userInfo ? styles.loggedIn : ''}`}>
                {!location.pathname.startsWith('/auth') && <div className={`${styles.profile} ${userInfo && showProfileDropDown ? styles.show : ''}`} onMouseEnter={userInfo ? () => setShowProfileDropDown(true) : undefined} onMouseLeave={userInfo ? () => setShowProfileDropDown(false) : undefined}>
                    <button className={styles.btnProfile} onClick={userInfo ? undefined : () => navigate('/auth/login')}>
                        <User />
                        <span>{userInfo ? userInfo.firstName || 'User' : "Login"}</span><ChevronDown className={styles.dropDownArrow} />
                    </button>
                    <ul className={styles.dropDownMenu} onClick={() => setShowProfileDropDown(false)}>
                        <li className={styles.myProfile}><Link to="/profile"><CircleUser />My Profile</Link></li>
                        <li className={styles.orders}><Link to="/orders" className={styles.orders}><Package />Orders</Link></li>
                        <li className={styles.manageAddresses}><Link to="/addresses" className={styles.addresses}><MapPin />Saved Addresses</Link></li>
                        <li className={styles.logout} onClick={handleLogout}><Link><LogOut />Logout</Link></li>
                    </ul>
                </div>}
                <Link to="/cart" className={`${styles.cart} ${styles.link}`}><ShoppingCart /><span className={styles.label}>Cart</span><span className={styles.cartCount}>{(cart?.items)?(cart.items.reduce((accumulator, cartItem) => accumulator + cartItem.quantity, 0)):0}</span></Link>
            </div>
        </header>
    )
}

export default Header;