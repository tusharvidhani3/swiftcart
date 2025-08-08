import { createContext, useEffect, useState } from "react";
import useMediaQuery from "../hooks/useMediaQuery";

const UIContext = createContext()
export default UIContext
export function UIProvider({ children }) {

    const [isSideMenuOpen, setSideMenuOpen] = useState(false)
    const isMobile = useMediaQuery('(max-width: 767px)')
    useEffect(() => {
        window.addEventListener('click', () => setSideMenuOpen(false))
    }, [])

    return (
        <UIContext.Provider value={{isSideMenuOpen, setSideMenuOpen, isMobile}}>
            {children}
        </UIContext.Provider>
    )
}