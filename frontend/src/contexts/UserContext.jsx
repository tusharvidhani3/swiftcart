import { createContext } from "react"
import { useQuery } from "@tanstack/react-query"
import { api } from "../api/client"

const UserContext = createContext()
export default UserContext

export function UserProvider({ children }) {

    const { data: user, isLoading: isUserLoading, isError, error } = useQuery({
        queryKey: ['user'],
        queryFn: () => api.get('/users/me'),
        staleTime: Infinity,
        refetchOnWindowFocus: false,
        retry: (failureCount, error) => {
            if (failureCount >= 3) return false;

            if (!error.response?.status) return true;

            const transientStatuses = [502, 503, 504];
            if (transientStatuses.includes(error.response?.status)) {
                return true;
            }

            return false;
        }
    })

    return (
        <UserContext.Provider value={{ user, isUserLoading }}>
            {children}
        </UserContext.Provider>
    )
}
