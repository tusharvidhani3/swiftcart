import { useContext, useRef } from 'react';
import styles from '../styles/CreateProductListing.module.css'
import productUploadIcon from '../assets/images/product-upload.svg'
import ToastContext from '../contexts/ToastContext';
import { useNavigate } from 'react-router';

export default function CreateProductListing() {

    const { fileInputRef } = useRef(null)
    const { showToast } = useContext(ToastContext)
    const navigate = useNavigate()

    async function createProduct(productForm) {
        const productFormData = new FormData(productForm), productData = {}
        productFormData.forEach((value, key) => {
            if (key !== "productImage") {
                productData[key] = value;
            }
        })
        const formData = new FormData()
        formData.append("createProductRequest", new Blob(
            [JSON.stringify(productData)], { type: "application/json" }
        ))
        const files = fileInputRef.current.files
        for (let i = 0; i < files.length; i++) {
            formData.append("productImages", files[i]);
        }
        const res = fetch("http://localhost:8080/api/products", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify(formData)
        })
        const productResponse = await res.json()
        showToast("Product listed successfully")
        navigate('/seller/products')
    }

    return (
        <form id={styles.productForm} onSubmit={e => {
            e.preventDefault()
            createProduct(e.currentTarget)
        }}>
            <div>
                <label htmlFor="file-upload" className={styles.btnImageUpload}>
                    <img src={productUploadIcon} alt="product upload image" />
                    <h2>Upload Product Image</h2>
                </label>
                <input ref={fileInputRef} type="file" accept="image/*" id="file-upload" name="productImage" multiple hidden />
            </div>
            <div className={styles.fields}>
                <div>
                    <label htmlFor="product-title">Product Title</label>
                    <input type="text" id="product-title" name="productName" />
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <textarea type="text" id="description" name="description"></textarea>
                </div>
                <div>
                    <label htmlFor="price">Price</label>
                    <input type="number" id="price" name="price" />
                </div>
                <div>
                    <label htmlFor="mrp">Maximum Retail Price</label>
                    <input type="number" id="mrp" name="mrp" />
                </div>
                <div>
                    <label htmlFor="category">Category</label>
                    <input type="text" id="category" name="category" />
                </div>
                <div>
                    <label htmlFor="stock">Quantity in stock</label>
                    <input type="number" id="stock" name="stock" />
                </div>
                <button>List Product</button>
            </div>
        </form>
    )
}