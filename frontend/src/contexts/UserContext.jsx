import { createContext, useEffect, useState } from "react"
import { useNavigate } from "react-router"
import { apiBaseUrl } from "../config"
import { useApi } from "../hooks/useApi"

const UserContext = createContext()
export default UserContext
export function UserProvider({ children }) {

    const [userInfo, setUserInfo] = useState(null)
    const [isTokenExpired, setTokenExpired] = useState(false)
    const navigate = useNavigate()
    const apiFetch = useApi()

    async function refreshAccessToken() {
        const res = await fetch(`${apiBaseUrl}/api/auth/refresh-token`, {
            method: "POST",
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        if (res.ok)
            setTokenExpired(false)
        else
            setUserInfo(null) //tokenexpired will be left true
    }

    async function updateUserInfo() {
        const res = await fetch(`${apiBaseUrl}/api/users/me`, {
            method: "GET",
            credentials: "include",
            headers: {
                'Content-Type': 'application/json'
            }
        })
        if (res.ok) {
            const user = await res.json()
            setUserInfo(user) //tokenexpired will be leaved as false
        }
        else if (res.status === 403) {
            setTokenExpired(true)
        }
    }

    async function handleLogin(userCredentials, setErrorData) {
        const res = await apiFetch(`${apiBaseUrl}/api/auth/login`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(userCredentials)
        })
        if (res.ok) {
            const user = await res.json()
            setUserInfo(user)
            setTokenExpired(false)
        }
        else {
            setErrorData({password: 'Invalid username or password'})
        }
    }

    async function handleLogout() {
        await apiFetch(`${apiBaseUrl}/api/auth/logout`, {
            method: "POST",
            credentials: "include"
        })
        setUserInfo(null)
        setTokenExpired(true)
        navigate('/auth/login')
    }

    useEffect(() => {
        if (isTokenExpired) {
            const init = async () => await refreshAccessToken()
            init()
        }

        else if (!userInfo) {
            const init = async () => await updateUserInfo()
            init()
        }

    }, [isTokenExpired])

    return (
        <UserContext.Provider value={{ userInfo, setUserInfo, isTokenExpired, setTokenExpired, handleLogin, handleLogout }}>
            {children}
        </UserContext.Provider>
    )
}
