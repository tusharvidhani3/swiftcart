import { useContext } from "react";
import SellerHeader from "./SellerHeader";
import UIContext from "../contexts/UIContext";
import { Outlet } from "react-router";
import SideMenu from "./SideMenu";

export default function SellerApp({ mainClass }) {

    const { isSideMenuOpen, setSideMenuOpen } = useContext(UIContext)
    const { isMobile } = useContext(UIContext)

    return (
        <>
            {isMobile && <SellerHeader />}
            <div className='sellerApp'>
                <SideMenu isSideMenuOpen={isSideMenuOpen} setSideMenuOpen={setSideMenuOpen} />
                {isSideMenuOpen && <div className="backdrop-overlay" onClick={() => setSideMenuOpen(false)} />}
                <main className={mainClass}>
                    <Outlet />
                </main>
            </div>
        </>
    )
}