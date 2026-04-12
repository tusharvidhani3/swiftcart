import { createContext, useState } from "react"

const AddressesContext = createContext()
export default AddressesContext

export function AddressesProvider({ children }) {

    const [selectedAddress, setSelectedAddress] = useState(null)

    return (
        <AddressesContext.Provider value={{ selectedAddress, setSelectedAddress }}>
            {children}
        </AddressesContext.Provider>
    )
}