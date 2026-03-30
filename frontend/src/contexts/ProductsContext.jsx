import { createContext, useState } from "react"

const ProductsContext = createContext()
export default ProductsContext

export function ProductsProvider({ children }) {

    const [ productsPagedModel, setProductsPagedModel ] = useState(null)
    const [editingProduct, setEditingProduct] = useState(null)

    return (
        <ProductsContext.Provider value={{productsPagedModel, setProductsPagedModel, editingProduct, setEditingProduct}}>
            {children}
        </ProductsContext.Provider>
    )
}