import { useContext } from "react"
import UIContext from "../contexts/UIContext"
import { CartProvider } from "../contexts/CartContext"
import { Outlet } from "react-router"
import Header from './Header'
import SideMenu from './SideMenu'

export default function CustomerApp({ mainClass }) {

    const { isMobile, isSideMenuOpen, setSideMenuOpen } = useContext(UIContext)

    return (
        <CartProvider>
                <Header setSideMenuOpen={setSideMenuOpen} />
                {isMobile && <SideMenu isSideMenuOpen={isSideMenuOpen} setSideMenuOpen={setSideMenuOpen} />}
                <main className={mainClass}>
                        <Outlet />
                </main>
        </CartProvider>
    )
}