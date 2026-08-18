import { useContext } from "react"
import UserContext from "../contexts/UserContext"
import { Navigate, Outlet, useLocation } from "react-router"
import ErrorMessage from "./ErrorMessage"

const ProtectedRoute = ({ allowedRoles, element }) => {

    const { user } = useContext(UserContext)
    const location = useLocation()

    if(!user) {
        return <Navigate to={`/auth/login?redirectTo=${location.pathname}`} replace />
    }
    else if(!allowedRoles.includes(user.role))
        return <ErrorMessage type='forbidden' />
    if(element)
        return element
    return <Outlet />
}

export default ProtectedRoute