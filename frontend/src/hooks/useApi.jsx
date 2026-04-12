import { useContext } from "react"
import ErrorMessage from "../components/ErrorMessage"
import ToastContext from "../contexts/ToastContext"

export function useApi() {

    const { showToast } = useContext(ToastContext)

    async function apiFetch(url, options = {}) {
        try {
            const res = await fetch(url, options)
            if(res.status === 403)
                return <ErrorMessage type='forbidden' />
            else if(res.status === 500)
                showToast("Unable to fetch data at the moment. Please try again later.")
            return res
        }
        catch(err) {
            if(err.message === 'Failed to fetch')
                showToast("No internet connection. Please check and try again.")
        }
    }

    return apiFetch
}