import { useContext } from "react";
import { ToastProvider } from "../contexts/ToastContext";
import SellerHeader from "./SellerHeader";
import UIContext from "../contexts/UIContext";
import SideMenu from "./SideMenu";
import { Outlet } from "react-router";

export default function SellerApp({ mainClass }) {

    const { isMobile, isSideMenuOpen, setSideMenuOpen } = useContext(UIContext)

    return (
        <ToastProvider>
            <SellerHeader />
            {isMobile && <SideMenu isSideMenuOpen={isSideMenuOpen} setSideMenuOpen={setSideMenuOpen} />}
            {isSideMenuOpen && <div className="backdrop-overlay" onClick={() => setSideMenuOpen(false)} />}
            <main className={mainClass}>
                <Outlet />
            </main>
        </ToastProvider>
    )
}