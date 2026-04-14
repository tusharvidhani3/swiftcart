import { useNavigate } from "react-router";
import { useApi } from "./useApi";
import { apiBaseUrl } from "../config";
import { QueryClient } from "@tanstack/react-query";

let refreshPromise = null
async function handleAccessTokenRefresh() {
    if (refreshPromise) {
        const res = await refreshPromise
        if(!res.ok)
            throw new Error("Refresh failed")
        return
    }

    try {
        refreshPromise = fetch(`${apiBaseUrl}/api/auth/refresh-token`, {
            method: "POST",
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        const res = await refreshPromise
        if (!res.ok)
            throw new Error("Refresh failed")
    }
    finally {
        refreshPromise = null
    }
}

const queryClient = new QueryClient()

export function useAuthFetch() {
    const navigate = useNavigate()
    const apiFetch = useApi()

    async function authFetch(url, options = {}) {
        const res = await apiFetch(url, {
            ...options,
            credentials: 'include'
        })
        if (res?.status === 401 && !options._retry) {
            try {
                await handleAccessTokenRefresh()
                return await authFetch(url, { ...options, _retry: true })
            }
            catch (error) {
                queryClient.removeQueries({ queryKey: ['user'], exact: true })
                navigate('/auth/login')
                return
            }
        }
        return res
    }

    return authFetch
}