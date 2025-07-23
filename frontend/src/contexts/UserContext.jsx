import { createContext, useEffect, useState } from "react"
import { useNavigate } from "react-router"

const UserContext = createContext()
export default UserContext
export function UserProvider({ children }) {

    const [userInfo, setUserInfo] = useState(null)
    const [isTokenExpired, setTokenExpired] = useState(false)
    const navigate = useNavigate()

    async function refreshAccessToken() {
        const res = await fetch('http://localhost:8080/api/auth/refresh-token', {
            method: "POST",
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        if (res.ok)
            setTokenExpired(false)
        else
            setUserInfo(null) //tokenexpired will be leaved true
    }

    async function updateUserInfo() {
        const res = await fetch("http://localhost:8080/api/users/me", {
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

    async function handleLogin(userCredentials) {
        const res = await fetch("http://localhost:8080/api/auth/login", {
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
        // else
        //     setFormError()
    }

    async function handleLogout() {
        const res = await fetch("http://localhost:8080/api/auth/logout", {
            method: "POST",
            credentials: "include"
        })
        if (res.ok) {
            setUserInfo(null)
            setTokenExpired(true)
            navigate('/auth/login')
        }
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
