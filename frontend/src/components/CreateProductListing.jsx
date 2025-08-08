import { useContext, useEffect, useRef, useState } from 'react';
import styles from '../styles/CreateProductListing.module.css'
import productUploadIcon from '../assets/images/product-upload.svg'
import ToastContext from '../contexts/ToastContext';
import { useNavigate } from 'react-router';
import { useAuthFetch } from '../hooks/useAuthFetch';
import plusIcon from '../assets/icons/plus.svg'
import cancelXIcon from '../assets/icons/cancel-x.png'

export default function CreateProductListing() {

    const { showToast } = useContext(ToastContext)
    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()
    const [selectedFiles, setSelectedFiles] = useState([])
    const [previews, setPreviews] = useState([])
    const [productData, setProductData] = useState({
        productName: '',
        description: '',
        price: '',
        mrp: '',
        category: '',
        stock: ''
    })

    const [error, setError] = useState({
        productImages: "",
        productName: "",
        description: "",
        price: "",
        mrp: "",
        category: "",
        stock: ""
    })

    useEffect(() => {
        const newPreviews = selectedFiles.map((file) => {
            if (file)
                return URL.createObjectURL(file)
        });
        setPreviews(newPreviews);

        return () => {
            newPreviews.forEach((url) => URL.revokeObjectURL(url));
        };
    }, [selectedFiles]);

    function handleFilesChange(e) {
        const files = Array.from(e.target.files)
        setSelectedFiles(selectedFiles => {
            const updatedFiles = [...selectedFiles]
            let i = 0, j = 0;
            while (i < files.length && j < 9) {
                if (!updatedFiles[j]) {
                    updatedFiles[j] = files[i]
                    i++
                }
                j++
            }
            if (i < files.length) {
            setError(error => ({
                ...error,
                productImages: "Cannot upload more than 9 images",
            }))
        } else {
            setError(error => ({
                ...error,
                productImages: "",
            }))
        }
            return updatedFiles
        })
    }

    function handleFileChange(e, i) {
        const file = e.target.files[0]
        setSelectedFiles(selectedFiles => {
            selectedFiles[i] = file
            return [...selectedFiles]
        })
    }

    async function createProduct() {
        const formData = new FormData()
        formData.append("createProductRequest", new Blob(
            [JSON.stringify(productData)], { type: "application/json" }
        ))

        for (let i = 0; i < selectedFiles.length; i++) {
            formData.append("productImages", selectedFiles[i]);
        }
        const res = authFetch("http://localhost:8080/api/products", {
            method: "POST",
            body: formData
        })
        const productResponse = await res.json()
        showToast("Product listed successfully")
        navigate('/seller/products')
    }

    function cancelImage(index) {
        setSelectedFiles(selectedFiles => {
            delete selectedFiles[index]
            setError(error => ({
                ...error,
                productImages: "",
            }))
            return selectedFiles.map((file, i) => {
                if (index === i)
                    selectedFiles[index] = null
                return selectedFiles[i]
            })
        })
    }

    return (
        <form id={styles.productForm} onSubmit={e => {
            e.preventDefault()
            createProduct()
        }}>
            <h1>List a Product</h1>
            <label htmlFor='multi-file-upload' className={styles.multiImageUploader}><img src={plusIcon} />Upload multiple files</label>
            <input type="file" accept="image/*" id='multi-file-upload' name='productImage' hidden multiple onChange={handleFilesChange} />
            <div className={styles.imageUploader}>
                {Array(9).fill(null).map((ele, i) => <div className={styles.imageUploadButton} key={i} data-index={i} >
                    <label htmlFor={`file-upload-${i}`} className={styles.btnImageUpload}>
                        <img src={productUploadIcon} alt="product upload image" />
                        <h2>Upload</h2>
                    </label>
                    <input type="file" accept="image/*" id={`file-upload-${i}`} name="productImage" hidden onChange={(e) => handleFileChange(e, i)} />
                    {previews[i] && <><img src={previews[i]} alt={`Uploaded ${i}`} className={styles.uploadedImage} /> <img onClick={() => cancelImage(i)} className={styles.cancelX} src={cancelXIcon} alt="cancel image" /></>}
                </div>)}
            </div>
            {error?.productImages && <div className={styles.error}>{error.productImages}</div>}
            <div className={styles.fields}>
                <div>
                    <label htmlFor="product-title">Product Title</label>
                    <input type="text" id="product-title" name="productName" value={productData.productName} onChange={e => setProductData(productData => ({ ...productData, productName: e.target.value }))} />
                    {error?.productName && <div className={styles.error}>{error.productName}</div>}
                </div>
                <div>
                    <label htmlFor="description">Description</label>
                    <textarea type="text" id="description" name="description" value={productData.description} onChange={e => setProductData(productData => ({ ...productData, description: e.target.value }))} />
                    {error?.description && <div className={styles.error}>{error.description}</div>}
                </div>
                <div>
                    <label htmlFor="price">Price</label>
                    <input type="number" id="price" name="price" value={productData.price} onChange={e => setProductData(productData => ({ ...productData, price: e.target.value }))} />
                    {error?.price && <div className={styles.error}>{error.price}</div>}
                </div>
                <div>
                    <label htmlFor="mrp">Maximum Retail Price</label>
                    <input type="number" id="mrp" name="mrp" value={productData.mrp} onChange={e => setProductData(productData => ({ ...productData, mrp: e.target.value }))} />
                    {error?.mrp && <div className={styles.error}>{error.mrp}</div>}
                </div>
                <div>
                    <label htmlFor="category">Category</label>
                    <input type="text" id="category" name="category" value={productData.category} onChange={e => setProductData(productData => ({ ...productData, category: e.target.value }))} />
                    {error?.category && <div className={styles.error}>{error.category}</div>}
                </div>
                <div>
                    <label htmlFor="stock">Quantity in stock</label>
                    <input type="number" id="stock" name="stock" value={productData.stock} onChange={e => setProductData(productData => ({ ...productData, stock: e.target.value }))} />
                    {error?.stock && <div className={styles.error}>{error.stock}</div>}
                </div>
                <button className={styles.btnSubmit}>List Product</button>
            </div>
        </form>
    )
}