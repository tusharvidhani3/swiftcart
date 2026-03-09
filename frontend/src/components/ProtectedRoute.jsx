import { useContext } from "react"
import UserContext from "../contexts/UserContext"
import { Navigate, Outlet, useLocation } from "react-router"
import ErrorMessage from "./ErrorMessage"

const ProtectedRoute = ({ allowedRoles, element }) => {

    const { userInfo } = useContext(UserContext)
    const location = useLocation()

    if(!userInfo) {
        return <Navigate to={`/auth/login?redirectTo=${location.pathname}`} replace />
    }
    else if(!allowedRoles.includes(userInfo.role))
        return <ErrorMessage type='unauthorized' />
    if(element)
        return element
    return <Outlet />
}

export default ProtectedRoute