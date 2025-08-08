import { Link, useNavigate, useSearchParams } from 'react-router'
import styles from '../styles/AuthForm.module.css'
import shoppingIcon from '../assets/icons/shopping-icon.svg'
import { useContext, useEffect, useState } from 'react'
import UserContext from '../contexts/UserContext'

export default function AuthForm({ mode }) {

    const { userInfo, setUserInfo, handleLogin } = useContext(UserContext)
    const [authForm, setAuthForm] = useState()
    const navigate = useNavigate()
    useEffect(() => {
        if (userInfo) {
            if(userInfo.role === 'ROLE_CUSTOMER')
                navigate('/')
            else if(userInfo.role === 'ROLE_SELLER')
                navigate('/seller/dashboard')
        }
    }, [userInfo])

    const isRegisterMode = mode === "register"

    async function register() {
        const res = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(authForm)
        })
        const user = await res.json()
        setUserInfo(user)
    }

    return (
        <>
            <div className={styles.banner}>
                <h1 className={styles.bannerTitle}>{isRegisterMode ? 'Create Your SwiftCart Account' : 'Login'}</h1>
                <p className={styles.bannerQuote}>{isRegisterMode ? 'Join SwiftCart - Where Smart Shopping Begins' : 'Get access to your Orders, Wishlist and Recommendations'}</p>
                <img className={styles.bannerImage} src={shoppingIcon} alt="shopping cart" />
            </div>
            <form id={styles.authForm} onSubmit={e => {
                e.preventDefault()
                if (isRegisterMode)
                    register(authForm)
                else
                    handleLogin(authForm)
            }}>
                <h2 className={styles.formTitle}>{isRegisterMode ? "Register" : "Login"}</h2>
                <div className={styles.field}>
                    <label htmlFor="mobile-number">Enter mobile number</label>
                    <div className={styles.mobileNumber}><span className={styles.countryCode}>+91</span> <input type="tel" minLength={10} maxLength={10} inputMode="numeric" id="mobile-number" name="mobileNumber" onInput={e => e.target.value = e.target.value.replace(/[^0-9]/g, '')} onChange={e => setAuthForm(authForm => ({...authForm, mobileNumber: e.target.value}))} /></div>
                    <p className={styles.error}>Mobile number is required</p>
                </div>
                <div className={styles.field}>
                    <label htmlFor="password">Enter password</label>
                    <input type="password" id="password" name="password" onChange={e => setAuthForm(authForm => ({...authForm, password: e.target.value}))} />
                    <p className={styles.error}>Password is required</p>
                </div>
                {!isRegisterMode && <Link to='/auth/register'>New User? Click Here to Register</Link>}
                <button className={styles.btnSubmit}>{isRegisterMode ? "Register" : "Login"}</button>
            </form>
        </>
    )
}