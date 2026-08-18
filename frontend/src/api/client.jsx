import axios from "axios";
import { apiBaseUrl } from "../config";
import { QueryCache, QueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

export const api = axios.create({
    baseURL: `${apiBaseUrl}/api`,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
})

export const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            retry: (failureCount, error) => {
                const status = error.response?.status
                if (status === 401 || status === 403 || status === 404) return false;
                return failureCount < 3;
            }
        }
    },
    queryCache: new QueryCache({
        onError: (error) => {
            if (error.response?.status === 403)
                toast.error("Access Denied: You do not have permission to perform this action")
            else if (error.response?.status === 500)
                toast.error("Unable to fetch data at the moment. Please try again later.")
        }
    })
})

api.interceptors.response.use((response) => response.data, async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true
        try {
            await handleAccessTokenRefresh()
            return api(originalRequest)
        }
        catch (err) {
            console.log(err)
            queryClient.removeQueries({ queryKey: ['user'], exact: true })
            if (!originalRequest.url.endsWith('/users/me'))
                window.location.href = '/auth/login'
            return Promise.reject(err)
        }
    }
    return Promise.reject(error)
})

let refreshPromise = null
async function handleAccessTokenRefresh() {
    if (refreshPromise)
        return refreshPromise

    refreshPromise = axios.post(`${apiBaseUrl}/api/auth/refresh-token`, {}, {
        withCredentials: true
    }).catch(err => {
        refreshPromise = null
        throw err
    }).finally(() => refreshPromise = null)
}