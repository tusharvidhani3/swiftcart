import { useNavigate } from "react-router";
import { useApi } from "./useApi";
import { useMutation, useQueryClient } from "@tanstack/react-query";

async function handleAccessTokenRefresh() {
    const res = await fetch(`${apiBaseUrl}/api/auth/refresh-token`, {
        method: "POST",
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    return res
}

export function useAuthFetch() {
    const navigate = useNavigate()
    const apiFetch = useApi()
    const queryClient = useQueryClient()

    const { mutate: refreshAccessToken } = useMutation({
        mutationFn: handleAccessTokenRefresh,
        onSuccess: (res) => {
            if (!res.ok)
                queryClient.invalidateQueries({ queryKey: ['user'] })
        }
    })

    async function authFetch(url, options = {}) {
        const res = await apiFetch(url, {
            ...options,
            credentials: 'include'
        })
        if (res?.status === 401) {
            const response = res.json()
            if (response.code === 'TOKEN_EXPIRED') {
                refreshAccessToken()
                return await authFetch(url, options)
            }
            else {
                queryClient.invalidateQueries(['user'])
                if(!url.endsWith("/users/me"))
                    navigate('/auth/login')
            }
        }
        return res
    }

    return authFetch
}