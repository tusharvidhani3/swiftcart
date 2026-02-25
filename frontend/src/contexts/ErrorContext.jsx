import { createContext, useState } from "react";
import ErrorMessage from "../components/ErrorMessage";

const ErrorContext = createContext()
export default ErrorContext

export function ErrorProvider({ children }) {

    const [errorType, setErrorType] = useState(null)

    return errorType ? <ErrorMessage type={errorType} />
        : (
            <ErrorContext.Provider value={{ setErrorType }}>
                {children}
            </ErrorContext.Provider>
        )
}