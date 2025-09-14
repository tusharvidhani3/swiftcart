import { createContext, useState } from "react"

const ProductsContext = createContext()
export default ProductsContext

export function ProductsProvider({ children }) {

    const [ products, setProducts ] = useState(null)
    const [editingProduct, setEditingProduct] = useState(null)

    return (
        <ProductsContext.Provider value={{products, setProducts, editingProduct, setEditingProduct}}>
            {children}
        </ProductsContext.Provider>
    )
}