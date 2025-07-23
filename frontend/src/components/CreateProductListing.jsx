import { useContext, useRef, useState } from 'react';
import styles from '../styles/CreateProductListing.module.css'
import productUploadIcon from '../assets/images/product-upload.svg'
import ToastContext from '../contexts/ToastContext';
import { useNavigate } from 'react-router';
import { useAuthFetch } from '../hooks/useAuthFetch';

export default function CreateProductListing() {

    const { showToast } = useContext(ToastContext)
    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()
    const [selectedFiles, setSelectedFiles] = useState([])
    const [productData, setProductData] = useState({
        productName: '',
        description: '',
        price: '',
        mrp: '',
        category: '',
        stock: ''
    })

    const handleFileChange = e => {
        const files = Array.from(e.target.files)
        setSelectedFiles(files)
    }

    async function createProduct() {
        const formData = new FormData()
        formData.append("createProductRequest", new Blob(
            [JSON.stringify(productData)], { type: "application/json" }
        ))

        for (let i = 0; i < selectedFiles.length; i++) {
            formData.append("productImages", selectedFiles[i]);
        }
        console.log(formData)
        const res = authFetch("http://localhost:8080/api/products", {
            method: "POST",
            body: formData
        })
        const productResponse = await res.json()
        console.log(productResponse)
        showToast("Product listed successfully")
        navigate('/seller/products')
    }

    return (
        <form id={styles.productForm} onSubmit={e => {
            e.preventDefault()
            createProduct()
        }}>
            <div>
                <label htmlFor="file-upload" className={styles.btnImageUpload}>
                    <img src={productUploadIcon} alt="product upload image" />
                    <h2>Upload Product Image</h2>
                </label>
                <input type="file" accept="image/*" id="file-upload" name="productImage" multiple hidden onChange={handleFileChange} />
            </div>
            <div className={styles.fields}>
                <div>
                    <label htmlFor="product-title">Product Title</label>
                    <input type="text" id="product-title" name="productName" value={productData.productName} onChange={e => setProductData(productData => ({...productData, productName: e.target.value}))} />
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <textarea type="text" id="description" name="description" value={productData.description} onChange={e => setProductData(productData => ({...productData, description: e.target.value}))} />
                </div>
                <div>
                    <label htmlFor="price">Price</label>
                    <input type="number" id="price" name="price" value={productData.price} onChange={e => setProductData(productData => ({...productData, price: e.target.value}))} />
                </div>
                <div>
                    <label htmlFor="mrp">Maximum Retail Price</label>
                    <input type="number" id="mrp" name="mrp" value={productData.mrp} onChange={e => setProductData(productData => ({...productData, mrp: e.target.value}))} />
                </div>
                <div>
                    <label htmlFor="category">Category</label>
                    <input type="text" id="category" name="category" value={productData.category} onChange={e => setProductData(productData => ({...productData, category: e.target.value}))} />
                </div>
                <div>
                    <label htmlFor="stock">Quantity in stock</label>
                    <input type="number" id="stock" name="stock" value={productData.stock} onChange={e => setProductData(productData => ({...productData, stock: e.target.value}))} />
                </div>
                <button>List Product</button>
            </div>
        </form>
    )
}