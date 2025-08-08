import { createContext, useEffect, useState } from "react"

const AddressesContext = createContext()
export default AddressesContext

export function AddressesProvider({ children }) {

    const [addresses, setAddresses] = useState([])
    const [editingAddress, setEditingAddress] = useState(null)
    const [selectedAddress, setSelectedAddress] = useState(null)

    return (
        <AddressesContext.Provider value={{addresses, setAddresses, editingAddress, setEditingAddress, selectedAddress, setSelectedAddress}}>
            {children}
        </AddressesContext.Provider>
    )
}