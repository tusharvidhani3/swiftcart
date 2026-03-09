import { useContext } from "react";
import ErrorContext from "../contexts/ErrorContext";

export function useApi() {

    const {setErrorType} = useContext(ErrorContext)

    async function apiFetch(url, options = {}) {
        try {
            const res = await fetch(url, options)
            if(res.status > 403 && res.status != 409) {
                if(res.status >= 500)
                    throw new Error('Server')
                else
                    throw new Error()
            }
            return res
        }
        catch(err) {
            let errorType = null
            switch(err.message) {
                case 'Failed to fetch':
                    errorType = 'network'
                    break
                case 'Unauthorized':
                    errorType = 'unauthorized'
                    break
                case 'Not found':
                    errorType = 'not Found'
                    break
                case 'Server':
                    errorType = 'server'
                    break
            }
            setErrorType(errorType)
        }
    }

    return apiFetch
}