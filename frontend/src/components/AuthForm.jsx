import { Link, useNavigate, useSearchParams } from 'react-router'
import styles from '../styles/AuthForm.module.css'
import shoppingIcon from '../assets/icons/shopping-icon.svg'
import { useContext } from 'react'
import UserContext from '../contexts/UserContext'

export default function AuthForm() {

    const { userInfo, setUserInfo } = useContext(UserContext)
    const navigate = useNavigate()
    // if(userInfo)
    //     navigate('/')

    const params = useSearchParams()
    const isRegisterMode = params.get("mode") === "register"

    async function login(loginForm) {
        const res = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            body: JSON.stringify(loginForm)
        })
        const user = await res.json()
        setUserInfo(user)
    }

    async function register(registerForm) {
        const res = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            body: JSON.stringify(registerForm)
        })
        const user = await res.json()
        setUserInfo(user)
    }

    return (
        <>
            <div className={styles.banner}>
                <h1 className={styles.bannerTitle}>Create Your SwiftCart Account</h1>
                <p className={styles.bannerQuote}>Join SwiftCart - Where Smart Shopping Begins</p>
                <img className={styles.bannerImage} src={shoppingIcon} alt="shopping cart" />
            </div>
            <form id={styles.authForm} onSubmit={e => {
                e.preventDefault()
                const authFormData = new FormData(e.currentTarget)
                if(isRegisterMode)
                    register(authFormData)
                else
                    login(authFormData)
            }}>
                <h2 className={styles.formTitle}>{isRegisterMode?"Register":"Login"}</h2>
                <div className={styles.field}>
                    <label htmlFor="mobile-number">Enter mobile number</label>
                    <div className={styles.mobileNumber}><span className={styles.countryCode}>+91</span> <input type="tel" minLength={10} maxLength={10} inputMode="numeric" id="mobile-number" name="mobileNumber" onInput={e => e.target.value = e.target.value.replace(/[^0-9]/g, '')} /></div>
                    <p className={styles.error}>Mobile number is required</p>
                </div>
                <div className={styles.field}>
                    <label htmlFor="password">Enter password</label>
                    <input type="password" id="password" name="password" />
                        <p className={styles.error}>Password is required</p>
                </div>
                <Link to='/register'>New User? Click Here to Register</Link>
                <button className={styles.btnSubmit}>{isRegisterMode?"Register":"Login"}</button>
            </form>
        </>
    )
}