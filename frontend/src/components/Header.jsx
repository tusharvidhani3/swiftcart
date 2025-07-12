import search from "../assets/icons/search.svg";
import profile from "../assets/icons/profile.svg";
import dropDownArrow from "../assets/icons/drop-down-arrow.svg";
import myProfile from "../assets/icons/my-profile.svg";
import orders from "../assets/icons/orders.svg";
import location from "../assets/icons/location.svg";
import logout from "../assets/icons/logout.svg";
import store from "../assets/icons/store.svg";
import cart from "../assets/icons/cart.svg";
import styles from "../styles/header.module.css";
import { Link } from "react-router";
import { useNavigate } from "react-router";
import { useContext, useEffect, useRef, useState } from "react";
import UserContext from "../contexts/UserContext";

function Header() {

    const [keyword, setKeyword] = useState("")
    const {userInfo, setTokenExpired, handleLogout} = useContext(UserContext)
    const navigate = useNavigate()
    const [showProfileDropDown, setShowProfileDropDown] = useState(false)
    const loginBtnRef = useRef(null)
    useEffect(() => {
        if(!userInfo) {
            loginBtnRef.current.addEventListener("click", () => navigate('/login'))
        }
    }, [userInfo])

    function handleSearch() {
        navigate('/', { state: {keyword: keyword}})
    }

    return (
        <header className={styles.header}>
            <div className={styles.headerLeft}>
                <button className={styles.hamburger}>☰</button>
                <Link to="/" className={`${styles.swiftcart} ${styles.link}`}>
                    <span>SwiftCart</span>
                </Link>
            </div>
            <form className={styles.searchbox} onSubmit={handleSearch}>
                <input type="search" name="keyword" placeholder="Search for products" value={keyword} onChange={e => setKeyword(e.target.value)} />
                    <button><img src={search} alt="search button" /></button>
            </form>
            <div className={styles.headerRight}>
                <div className={`${styles.profile} ${userInfo && showProfileDropDown?styles.show:''}`} onMouseEnter={() => setShowProfileDropDown(true)} onMouseLeave={() => setShowProfileDropDown(false)}>
                    <button className={`${styles.btnProfile} ${userInfo?styles.loggedIn:''}`} ref={loginBtnRef}>
                        <img src={profile} alt="profile icon" />
                        <span>{userInfo?userInfo.firstName:"Login"}</span><img src={dropDownArrow} alt="drop down" className={styles.dropDownArrow} />
                    </button>
                    <ul className={styles.dropDownMenu}>
                        <li className={styles.myProfile}><Link to="/profile"><img src={myProfile} />My Profile</Link></li>
                        <li className={styles.orders}><Link to="/orders" className={styles.orders}><img src={orders} />Orders</Link></li>
                        <li className={styles.manageAddresses}><Link to="/manageAddresses" className={styles.addresses}><img src={location} />Saved Addresses</Link></li>
                        <li className={styles.logout} onClick={handleLogout}><Link><img src={logout} />Logout</Link></li>
                    </ul>
                </div>
                <Link to="/cart" className={`${styles.cart} ${styles.link}`}><img src={cart} alt="cart" /><span className={styles.label}>Cart</span><span className={styles.cartCount}></span></Link>
            </div>
        </header>
    )
}

export default Header;