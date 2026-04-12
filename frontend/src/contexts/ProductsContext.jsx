import { createContext, useState } from "react"

const ProductsContext = createContext()
export default ProductsContext

export function ProductsProvider({ children }) {

    const [editingProduct, setEditingProduct] = useState(null)

    return (
        <ProductsContext.Provider value={{ editingProduct, setEditingProduct }}>
            {children}
        </ProductsContext.Provider>
    )
}