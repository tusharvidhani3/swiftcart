import { useContext } from "react"
import UserContext from "../contexts/UserContext"
import { Navigate, Outlet, useLocation } from "react-router"

const ProtectedRoute = ({ allowedRoles, element }) => {

    const { userInfo } = useContext(UserContext)
    const location = useLocation()

    if(!userInfo) {
        return <Navigate to={`/auth/login?redirectTo=${location.pathname}`} replace />
    }
    else if(!allowedRoles.includes(userInfo.role))
        return <Navigate to="/unauthorized" replace />
    if(element)
        return element
    return <Outlet />
}

export default ProtectedRoute