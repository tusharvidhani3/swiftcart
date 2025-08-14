import { useContext } from "react";
import UserContext from "../contexts/UserContext";
import { useNavigate } from "react-router";

export function useAuthFetch() {
    const { isTokenExpired, setTokenExpired, userInfo } = useContext(UserContext)
    const navigate = useNavigate()

    async function authFetch(url, options = {}) {
        if(!userInfo)
            navigate(`/auth/login`)
        const res = await fetch(url, {
            ...options,
            credentials: 'include'
        })
        if(res.status === 403 || res.status === 401) {
            setTokenExpired(true)
            await new Promise(resolve => setTimeout(() => {
                if(!isTokenExpired)
                    resolve()
                else
                    navigate('/auth/login')
            }, 500))
            return authFetch(url, options)
        }
        return res
    }

    return {authFetch}
}