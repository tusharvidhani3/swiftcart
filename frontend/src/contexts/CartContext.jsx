import { createContext, useContext, useEffect, useState } from "react";
import UserContext from "./UserContext";
import { useAuthFetch } from "../hooks/useAuthFetch";

const CartContext = createContext()
export default CartContext

export function CartProvider({ children }) {

    const [cart, setCart] = useState(null)
    const { userInfo } = useContext(UserContext)
    const { authFetch } = useAuthFetch()

    async function updateCart() {
        const res = await authFetch("http://localhost:8080/api/cart", {
            method: "GET"
        })
        const cartResponse = await res.json()
        if (cartResponse.cartItems.length)
            setCart(cartResponse)
        else
            setCart(null)
    }

    async function addToCart(productId, showToast) {
        const res = await authFetch("http://localhost:8080/api/cart/items", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ productId, quantity: 1 })
        })
        const cartResponse = await res.json()
        console.log(cartResponse)
        setCart(cartResponse)
        showToast("Product added to cart")
    }

    useEffect(() => {
        console.log(userInfo)
        if (userInfo) {
            updateCart()
        }
    }, [userInfo])

    return (
        <CartContext.Provider value={{ cart, setCart, addToCart }}>
            {children}
        </CartContext.Provider>
    )
}