import { createContext, useContext } from "react";
import UserContext from "./UserContext";
import { useAuthFetch } from "../hooks/useAuthFetch";
import { apiBaseUrl } from "../config";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import ToastContext from "./ToastContext";

const CartContext = createContext()
export default CartContext

async function apiAddToCart(authFetch, productId) {
    const res = await authFetch(`${apiBaseUrl}/api/carts/items`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId: productId, quantity: 1 })
    })
    const response = await res.json()
    if(!res.ok) {
        const error = new Error()
        error.status = response.status
        error.message = response.title
        throw error
    }
    return response
}

async function getCart(authFetch) {
    const res = await authFetch(`${apiBaseUrl}/api/carts`, {
        method: "GET"
    })
    return await res.json()
}

export function CartProvider({ children }) {

    const { userInfo } = useContext(UserContext)
    const authFetch = useAuthFetch()
    const { showToast } = useContext(ToastContext)

    const { data: cart } = useQuery({
        queryKey: ['cart'],
        queryFn: () => getCart(authFetch),
        staleTime: 1000 * 60 * 5,
        enabled: !!userInfo
    })

    const queryClient = useQueryClient()

    const { mutate: addToCart } = useMutation({
        mutationFn: (productId) => apiAddToCart(authFetch, productId),
        onSuccess: (updatedCart) => {
            queryClient.setQueryData(['cart'], updatedCart)
            showToast("Product added to cart")
        },
        onError: error => showToast(error.message || 'Failed to add product to cart')
    })

    return (
        <CartContext.Provider value={{ cart, addToCart }}>
            {children}
        </CartContext.Provider>
    )
}