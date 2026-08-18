import { createContext, useContext } from "react";
import UserContext from "./UserContext";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { api } from "@/api/client";

const CartContext = createContext()
export default CartContext

export function CartProvider({ children }) {

    const { user } = useContext(UserContext)

    const { data: cart } = useQuery({
        queryKey: ['cart'],
        queryFn: () => api.get('/carts'),
        staleTime: 1000 * 60 * 5,
        enabled: !!user
    })

    const queryClient = useQueryClient()

    const { mutate: addToCart } = useMutation({
        mutationFn: (productId) => api.post(`/carts/items`, { productId: productId, quantity: 1 }),
        onSuccess: (updatedCart) => {
            queryClient.invalidateQueries({ queryKey: ['cart', 'summary'] })
            queryClient.setQueryData(['cart'], updatedCart)
            toast.success("Product added to cart")
        },
        onError: error => toast.error(error.response?.data?.message || 'Failed to add product to cart')
    })

    return (
        <CartContext.Provider value={{ cart, addToCart }}>
            {children}
        </CartContext.Provider>
    )
}