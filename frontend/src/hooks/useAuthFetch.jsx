import { useContext } from "react";
import UserContext from "../contexts/UserContext";

export function useAuthFetch() {
    const { isTokenExpired, setTokenExpired } = useContext(UserContext)

    async function authFetch(url, options = {}) {
        const res = await fetch(url, {
            ...options,
            credentials: 'include'
        })
        if(res.status === 403 || res.status === 401) {
            setTokenExpired(true)
            await new Promise(resolve => setTimeout(() => {
                if(!isTokenExpired)
                    resolve()
            }, 500))
            return authFetch(url, options)
        }
        return res
    }

    return {authFetch}
}