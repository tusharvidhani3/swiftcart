import { createContext, useEffect, useState } from "react"

const AddressesContext = createContext()
export default AddressesContext

export function AddressesProvider({ children }) {

    const [selectedAddressId, setSelectedAddressId] = useState(localStorage.getItem('selectedAddressId'))

    useEffect(() => {
        if(selectedAddressId)
            localStorage.setItem('selectedAddressId', selectedAddressId)
    }, [selectedAddressId])

    return (
        <AddressesContext.Provider value={{ selectedAddressId, setSelectedAddressId }}>
            {children}
        </AddressesContext.Provider>
    )
}