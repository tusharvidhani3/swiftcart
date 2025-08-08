import { useContext, useState } from "react"
import UIContext from "../contexts/UIContext"
import { CartProvider } from "../contexts/CartContext"
import { ToastProvider } from "../contexts/ToastContext"
import { CheckoutProvider } from "../contexts/CheckoutContext"
import { Outlet } from "react-router"
import Header from './Header'
import SideMenu from './SideMenu'

export default function CustomerApp({ mainClass }) {

    const [keyword, setKeyword] = useState("")
    const { isMobile, isSideMenuOpen, setSideMenuOpen } = useContext(UIContext)

    return (
        <CartProvider>
            <ToastProvider>
                <Header keyword={keyword} setKeyword={setKeyword} setSideMenuOpen={setSideMenuOpen} />
                {isMobile && <SideMenu isSideMenuOpen={isSideMenuOpen} setSideMenuOpen={setSideMenuOpen} />}
                {isSideMenuOpen && <div className="backdrop-overlay" onClick={() => setSideMenuOpen(false)} />}
                <main className={mainClass}>
                    <CheckoutProvider>
                        <Outlet context={{ keyword }} />
                    </CheckoutProvider>
                </main>
            </ToastProvider>
        </CartProvider>
    )
}