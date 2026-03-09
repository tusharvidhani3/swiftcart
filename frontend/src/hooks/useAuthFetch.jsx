import { useContext } from "react";
import UserContext from "../contexts/UserContext";
import { useNavigate } from "react-router";
import { useApi } from "./useApi";

export function useAuthFetch() {
    const { isTokenExpired, setTokenExpired } = useContext(UserContext)
    const navigate = useNavigate()
    const apiFetch = useApi()

    async function authFetch(url, options = {}) {
        const res = await apiFetch(url, {
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