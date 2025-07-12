import { createContext, useEffect, useState } from "react"
import { useNavigate } from "react-router"

const UserContext = createContext()
export default UserContext
export function UserProvider({ children }) {

    const [userInfo, setUserInfo] = useState(null)
    const [isTokenExpired, setTokenExpired] = useState(false)
    const [isTokenRefreshed, setTokenRefreshed] = useState(false)
    const navigate = useNavigate()

    async function updateRefreshToken() {
        const res = await fetch('http://localhost:8080/api/auth/refresh-token', {
            method: "POST",
            credentials: "include"
        })
        if (res.ok)
            setTokenRefreshed(true)
        else
            setUserInfo(null)
    }

    async function updateUserInfo() {
        const res = await fetch("http://localhost:8080/api/users/me", {
            method: "GET",
            credentials: "include"
        })
        if (res.ok) {
            const user = await res.json()
            setUserInfo(user)
        }
        else if (res.status === 403) {
            updateRefreshToken()
        }
    }

    async function handleLogin(userCredentials) {
        const res = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            credentials: "include"
        })
        if (res.ok) {
            const user = await res.json()
            setUserInfo(user)
            setTokenRefreshed(false)
            setTokenExpired(false)
        }
        else
            setFormError()
    }

    async function handleLogout() {
        const res = await fetch("http://localhost:8080/api/auth/logout", {
            method: "POST",
            credentials: "include"
        })
        if (res.ok) {
            setUserInfo(null)
            setTokenRefreshed(false)
            setTokenExpired(false)
            navigate('/login')
        }
    }

    useEffect(() => {
        const init = async () => {
            if (!userInfo) {
                await updateUserInfo()
            }
            else if (isTokenExpired) {
                await updateRefreshToken()
            }
        }

        init()
    }, [isTokenRefreshed, isTokenExpired])

    return (
        <UserContext.Provider value={{ userInfo, setUserInfo, setTokenExpired, handleLogin, handleLogout }}>
            {children}
        </UserContext.Provider>
    )
}
