import { createContext, useContext, useEffect, useState } from "react";
import CartContext from "./CartContext";

const CheckoutContext = createContext()
export default CheckoutContext

export function CheckoutProvider({ children }) {

    const [isBuyNow, setBuyNow] = useState(false)
    const [checkoutCart, setCheckoutCart] = useState(null)
    const {cart} = useContext(CartContext)
    useEffect(() => {
        setCheckoutCart(cart)
    }, [cart])

    return (
        <CheckoutContext.Provider value={{isBuyNow, checkoutCart, setCheckoutCart, setBuyNow}}>
            {children}
        </CheckoutContext.Provider>
    )
}