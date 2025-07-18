import { createContext, useState } from "react"
import Toast from "../components/Toast"

const ToastContext = createContext()
export default ToastContext
export function ToastProvider({ children }) {

    const [toast, setToast] = useState("")

    function showToast(message) {
        setToast(message)
        setTimeout(() => setToast(""), 2000)
    }

    return (
        <ToastContext.Provider value={{showToast}}>
            {children}
            <Toast>{toast}</Toast>
        </ToastContext.Provider>
    )
}