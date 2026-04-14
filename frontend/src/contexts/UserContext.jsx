import { createContext } from "react"
import { apiBaseUrl } from "../config"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { useApi } from "../hooks/useApi"

const UserContext = createContext()
export default UserContext

async function handleRegister(apiFetch, authForm) {
    const res = await apiFetch(`${apiBaseUrl}/api/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(authForm)
    })
    return await res.json()
}

async function handleLogout(apiFetch) {
    await apiFetch(`${apiBaseUrl}/api/auth/logout`, {
        method: "POST",
        credentials: "include"
    })
}

async function handleLogin(apiFetch, userCredentials) {
    const res = await apiFetch(`${apiBaseUrl}/api/auth/login`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(userCredentials)
    })
    if (res.ok)
        return await res.json()
    else if (res.status === 401) {
        const error = new Error()
        error.status = 401
        throw error
    }
}

async function getUserInfo() {
    const res = await fetch(`${apiBaseUrl}/api/users/me`, {
        method: "GET",
        credentials: 'include'
    })
    const response = await res.json()
    if (!res.ok) {
        const error = new Error()
        error.message = response.title
        error.status = response.status
        throw error
    }
    return response
}

export function UserProvider({ children }) {

    const apiFetch = useApi()

    const queryClient = useQueryClient()

    const { data: userInfo, isError, error } = useQuery({
        queryKey: ['user'],
        queryFn: () => getUserInfo(),
        staleTime: 1000 * 60 * 60 * 24,
        retry: (failureCount, error) => {
            if (failureCount >= 3) return false;

            if (!error.status) return true;

            const transientStatuses = [502, 503, 504];
            if (transientStatuses.includes(error.status)) {
                return true;
            }

            return false;
        }
    })

    const { mutate: register } = useMutation({
        mutationFn: (authForm) => handleRegister(apiFetch, authForm),
        onSuccess: (user) => {
            queryClient.setQueryData(['user'], user)
        }
    })

    const { mutate: login } = useMutation({
        mutationFn: (authForm) => handleLogin(apiFetch, authForm),
        onSuccess: (user) => {
            queryClient.setQueryData(['user'], user)
        }
    })

    const { mutate: logout } = useMutation({
        mutationFn: () => handleLogout(apiFetch),
        onSuccess: () => {
            queryClient.resetQueries({ queryKey: ['user'], exact: true });
        }
    })

    return (
        <UserContext.Provider value={{ userInfo, login, register, logout }}>
            {children}
        </UserContext.Provider>
    )
}
