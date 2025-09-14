import { Link, useNavigate, useSearchParams } from 'react-router'
import styles from '../styles/AuthForm.module.css'
import shoppingIcon from '../assets/icons/shopping-icon.svg'
import { useContext, useEffect, useState } from 'react'
import UserContext from '../contexts/UserContext'
import { apiBaseUrl } from '../config'
import eyeIcon from '../assets/icons/eye.svg'
import eyeOffIcon from '../assets/icons/eye-off.svg'

export default function AuthForm({ mode }) {

    const { userInfo, setUserInfo, handleLogin } = useContext(UserContext)
    const [authForm, setAuthForm] = useState({
        mobileNumber: undefined,
        password: ""
    })
    const [showPassword, setShowPassword] = useState(false)
    const [searchParams] = useSearchParams()
    const redirectTo = searchParams.get('redirectTo')
    const isRegisterMode = mode === "register"
    const [errorData, setErrorData] = useState({})
    const validationConfig = {
        mobileNumber: [{ required: true, message: 'Please enter mobile number' }, { pattern: /^[6789]{1}[0-9]{9}$/, message: 'Please enter a valid 10-digit mobile number' }],
        password: isRegisterMode ? [{ required: true, message: 'Please enter password' }, { minLength: 8, message: 'Password must be at least 8 characters long' }, { pattern: /.*[a-z].*/, message: 'Password must contain a lowercase letter' }, { pattern: /.*[A-Z].*/, message: 'Password must contain an uppercase letter' }, { pattern: /.*\d.*/, message: 'Password must contain a digit' }, { pattern: /.*[^A-Za-z0-9].*/, message: 'Password must contain a special character' }] : [{ required: true, message: 'Please enter password' }, { pattern: /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/, message: 'Invalid username or password' }]
    }
    const navigate = useNavigate()
    useEffect(() => {
        if (userInfo)
            navigate(redirectTo || (userInfo.role === 'ROLE_CUSTOMER' ? '/' : '/seller'))
    }, [userInfo])

    useEffect(() => {
        setErrorData({})
    }, [mode])

    async function register() {
        const res = await fetch(`${apiBaseUrl}/api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(authForm)
        })
        const user = await res.json()
        setUserInfo(user)
    }

    function validateForm() {
        const error = {}
        for (const field in validationConfig) {
            validationConfig[field].some(rule => {
                if (rule.required && !authForm[field]) {
                    error[field] = rule.message
                    return true
                }
                if (rule.minLength && authForm[field].length < rule.minLength) {
                    error[field] = rule.message
                    return true
                }
                if (rule.pattern && !rule.pattern.test(authForm[field])) {
                    error[field] = rule.message
                    return true
                }
            })
        }

        if (Object.keys(error).length)
            setErrorData(error)
        else
            return true
    }

    function validateFormField(field) {
        const error = {...errorData}
        validationConfig[field].some(rule => {
            if (rule.required && !authForm[field]) {
                error[field] = rule.message
                return true
            }
            if (rule.minLength && authForm[field].length < rule.minLength) {
                error[field] = rule.message
                return true
            }
            if (rule.pattern && !rule.pattern.test(authForm[field])) {
                error[field] = rule.message
                return true
            }
        })
        if (error[field])
            setErrorData(error)
    }

    function revokeError(field) {
        if (errorData[field]) {
            setErrorData(errorData => {
                delete errorData[field]
                return { ...errorData }
            })
        }
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
                if (validateForm()) {
                    if (isRegisterMode)
                        register(authForm)
                    else
                        handleLogin(authForm)
                }
            }}>
                <h2 className={styles.formTitle}>{isRegisterMode ? "Register" : "Login"}</h2>
                <div className={styles.field}>
                    <label htmlFor="mobile-number">Enter mobile number</label>
                    <div className={styles.mobileNumber}><span className={styles.countryCode}>+91</span> <input type="tel" minLength={10} maxLength={10} inputMode="numeric" id="mobile-number" name="mobileNumber" onInput={e => e.target.value = e.target.value.replace(/[^0-9]/g, '')} onChange={e => {
                        setAuthForm(authForm => ({ ...authForm, mobileNumber: e.target.value }))
                        revokeError(e.target.name)
                        }} onBlur={e => validateFormField(e.target.name)} /></div>
                    <p className={styles.error}>{errorData.mobileNumber}</p>
                </div>
                <div className={`${styles.field} ${styles.passwordField}`}>
                    <label htmlFor="password">{isRegisterMode ? 'Create' : 'Enter'} password</label>
                    <input type={showPassword ? 'text' : "password"} id="password" name="password" onChange={e => {
                        setAuthForm(authForm => ({ ...authForm, password: e.target.value }))
                        revokeError(e.target.name)
                        }} onBlur={e => validateFormField(e.target.name)} />
                    {authForm.password && <button type='button' className={styles.btnTogglePasswordVisibility} onMouseDown={(e) => e.preventDefault()} onClick={() => {
                        setShowPassword(showPassword => !showPassword)
                        }}><img src={showPassword ? eyeOffIcon : eyeIcon} alt='toggle password visibility' /></button>}
                    {isRegisterMode && !authForm.password ? <p className={styles.instruction}>Your password should be at least 8 characters long and include a mix of uppercase, lowercase, numbers, and special characters</p> : <p className={`${styles.error} ${styles.passwordError}`}>{errorData.password}</p>}
                </div>
                {isRegisterMode ? <Link to='/auth/login'>Existing User? Log in</Link> : <Link to='/auth/register'>New User? Create an account</Link>}
                <button className={styles.btnSubmit}>{isRegisterMode ? "Register" : "Login"}</button>
            </form>
        </>
    )
}