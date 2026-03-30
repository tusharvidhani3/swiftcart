import { createContext, useContext, useEffect, useState } from "react";
import CartContext from "./CartContext";

const CheckoutContext = createContext()
export default CheckoutContext

export function CheckoutProvider({ children }) {

    const [isBuyNow, setBuyNow] = useState(false)

    return (
        <CheckoutContext.Provider value={{isBuyNow, setBuyNow}}>
            {children}
        </CheckoutContext.Provider>
    )
}