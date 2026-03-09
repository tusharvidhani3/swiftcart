import { Link, useNavigate, useSearchParams } from 'react-router'
import styles from '../styles/AuthForm.module.css'
import shoppingIcon from '../assets/icons/shopping-icon.svg'
import { useContext, useEffect, useState } from 'react'
import UserContext from '../contexts/UserContext'
import { apiBaseUrl } from '../config'
import eyeIcon from '../assets/icons/eye.svg'
import eyeOffIcon from '../assets/icons/eye-off.svg'
import { useApi } from '../hooks/useApi'
import GoogleLoginButton from './GoogleLoginButton'

export default function AuthForm({ mode }) {

    const { userInfo, setUserInfo, handleLogin } = useContext(UserContext)
    const [authForm, setAuthForm] = useState({
        email: "",
        password: ""
    })
    const [showPassword, setShowPassword] = useState(false)
    const [searchParams] = useSearchParams()
    const redirectTo = searchParams.get('redirectTo')
    const isRegisterMode = mode === "register"
    const [errorData, setErrorData] = useState({})
    const validationConfig = {
        email: [{ required: true, message: 'Please enter email' }, { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$/, message: 'Please enter a valid email' }],
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

    const apiFetch = useApi()

    async function register() {
        const res = await apiFetch(`${apiBaseUrl}/api/auth/register`, {
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
        const error = { ...errorData }
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
        <div className='flex items-center justify-center h-full pb-4 gap-0 w-full'>
            <div className={styles.banner}>
                <h1 className="font-bold text-3xl my-3">{isRegisterMode ? 'Create Your SwiftCart Account' : 'Login'}</h1>
                <p className={styles.bannerQuote}>{isRegisterMode ? 'Join SwiftCart - Where Smart Shopping Begins' : 'Get access to your Orders, Wishlist and Recommendations'}</p>
                <img className={styles.bannerImage} src={shoppingIcon} alt="shopping cart" />
            </div>
            <div className='flex flex-col items-center max-w-[544px] w-full'>
                <form id={styles.authForm} onSubmit={e => {
                    e.preventDefault()
                    if (validateForm()) {
                        if (isRegisterMode)
                            register(authForm)
                        else
                            handleLogin(authForm, setErrorData)
                    }
                }}>
                    <h2 className={styles.formTitle}>{isRegisterMode ? "Register" : "Login"}</h2>
                    <div className={styles.field}>
                        <label htmlFor="email">Enter email</label>
                        <div className={styles.email}><input type="email" id="email" name="email" onChange={e => {
                            setAuthForm(authForm => ({ ...authForm, email: e.target.value }))
                            revokeError(e.target.name)
                        }} onBlur={e => validateFormField(e.target.name)} /></div>
                        <p className={styles.error}>{errorData.email}</p>
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

                <a href={`${apiBaseUrl}/oauth2/authorization/google`}>
                    <GoogleLoginButton width={'w-full'} />
                </a>
            </div>
        </div>
    )
}