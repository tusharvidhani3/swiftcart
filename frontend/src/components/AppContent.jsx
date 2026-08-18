import UserContext from "@/contexts/UserContext";
import { useContext } from "react";
import SellerApp from "./SellerApp";
import CustomerApp from "./CustomerApp";
import { useMatches } from "react-router";

export default function AppContent() {
    const matches = useMatches();
    const mainClassKey = matches.find(m => m.handle?.mainClass)?.handle.mainClass || '';
    const mainClass = `${mainClassKey && mainClassKey}`;
    const { userInfo } = useContext(UserContext)
    return userInfo?.role === 'ROLE_SELLER' ? <SellerApp mainClass={mainClass} /> : <CustomerApp mainClass={mainClass} />
}