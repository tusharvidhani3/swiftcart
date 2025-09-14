import { Fragment, useContext, useEffect, useRef, useState } from 'react';
import styles from '../styles/CreateProductListing.module.css'
import productUploadIcon from '../assets/images/product-upload.svg'
import ToastContext from '../contexts/ToastContext';
import { useNavigate } from 'react-router';
import { useAuthFetch } from '../hooks/useAuthFetch';
import plusIcon from '../assets/icons/plus.svg'
import cancelXIcon from '../assets/icons/cancel-x.png'
import { apiBaseUrl } from '../config';
import ProductsContext from '../contexts/ProductsContext';

export default function CreateProductListing({ isEditMode }) {

    const { showToast } = useContext(ToastContext)
    const { authFetch } = useAuthFetch()
    const navigate = useNavigate()
    const { editingProduct } = useContext(ProductsContext)
    const [selectedFiles, setSelectedFiles] = useState([])
    const [previews, setPreviews] = useState([])
    const [preUploadedPreviews, setPreUploadedPreviews] = useState(isEditMode ? editingProduct.imageUrls : [])
    const [productData, setProductData] = useState(isEditMode ? editingProduct : {
        productName: '',
        description: '',
        price: '',
        mrp: '',
        category: '',
        stock: ''
    })

    const [errorData, setErrorData] = useState({})

    const validationConfig = {
        productName: [{ required: true, message: 'Please enter a product title' }, { minLength: 3, maxLength: 200, message: 'Title must be at between 3 & 200 characters long' }],
        description: [{ required: true, message: 'Please enter description' }, { minLength: 10, maxLength: 1000, message: 'Description must be at between 10 & 1000 characters long' }],
        price: [{ required: true, message: 'Please enter price' }, { min: 1, message: 'Price must be at least ₹1' }],
        mrp: [{ required: true, message: 'Please enter Maximum Retail Price' }, { min: productData.price, message: 'MRP cannot be less than price' }],
        category: [{ required: true, message: 'Please enter category' }, { minLength: 'Category cannot be less than 3 characters' }],
        stock: [{ required: true, message: 'Please enter stock' }, { min: 0, message: 'Stock cannot be less than 0' }],
    }

    useEffect(() => {
        const newPreviews = selectedFiles.map(file => {
            if (file)
                return URL.createObjectURL(file)
        })
        if (isEditMode) {
            setPreviews([...preUploadedPreviews, ...newPreviews])
        }
        else {
            setPreviews(newPreviews);

            return () => {
                newPreviews.forEach((url) => URL.revokeObjectURL(url));
            };
        }
    }, [selectedFiles, preUploadedPreviews]);

    function handleFilesChange(e) {
        const files = Array.from(e.target.files)
        setSelectedFiles(selectedFiles => {
            const updatedFiles = [...selectedFiles]
            let i = selectedFiles.length
            while (i < selectedFiles.length + files.length && i < 9) {
                updatedFiles[i] = files[i-selectedFiles.length]
                i++
            }
            if (previews.length + files.length > 9) {
                setErrorData(errorData => ({
                    ...errorData,
                    productImages: "Cannot upload more than 9 images",
                }))
            } else {
                setErrorData(errorData => {
                    delete errorData['productImages']
                    return { ...errorData }
                })
            }
            return updatedFiles
        })
    }

    async function createProduct() {
        if (isEditMode) {
            const formData = new FormData()
            // productData.deletedImageIds = 
            formData.append("createProductRequest", new Blob(
                [JSON.stringify(productData)], { type: "application/json" }
            ))
        }
        else {
            const formData = new FormData()
            formData.append("createProductRequest", new Blob(
                [JSON.stringify(productData)], { type: "application/json" }
            ))

            for (let i = 0; i < selectedFiles.length; i++) {
                formData.append("productImages", selectedFiles[i]);
            }
            const res = authFetch(`${apiBaseUrl}/api/products`, {
                method: "POST",
                body: formData
            })
            const productResponse = await res.json()
            showToast("Product listed successfully")
            navigate('/seller/products')
        }
    }

    function cancelImage(index) {
        setErrorData(errorData => {
            delete errorData['productImages']
            return { ...errorData }
        })
        if (isEditMode) {
            if (index >= preUploadedPreviews.length) {
                setSelectedFiles(selectedFiles => {
                    return selectedFiles.filter((_, idx) => idx != index - preUploadedPreviews.length)
                })
            }
            else {
                setPreUploadedPreviews(preUploadedPreviews => {
                    return preUploadedPreviews.filter((_, idx) => idx != index)
                })
            }
        }
        else {
            setSelectedFiles(selectedFiles => selectedFiles.filter((_, idx) => idx != index))
        }
    }

    function validateForm() {
        const error = {}
        for (const field in productData) {
            validationConfig[field].some(rule => {
                if (rule.required && !productData[field]) {
                    error[field] = rule.message
                    return true
                }
                if (rule.minLength && productData[field].length < rule.minLength || rule.maxLength && productData[field].length > rule.maxLength) {
                    error[field] = rule.message
                    return true
                }
                else if (rule.min && Number(productData[field]) < rule.min || rule.max && Number(productData[field]) > rule.max) {
                    error[field] = rule.message
                    return true
                }
            })
        }
        if (previews.length < 1)
            error['productImages'] = 'Please upload an image of product'
        if (Object.keys(error).length)
            setErrorData(error)
        else
            return true
    }

    function validateFormField(field) {
        const error = {...errorData}
        validationConfig[field].some(rule => {
            if (rule.required && !productData[field]) {
                error[field] = rule.message
                return true
            }
            if (rule.minLength && productData[field].length < rule.minLength || rule.maxLength && productData[field].length > rule.maxLength) {
                error[field] = rule.message
                return true
            }
            else if (rule.min && Number(productData[field]) < rule.min || rule.max && Number(productData[field]) > rule.max) {
                error[field] = rule.message
                return true
            }
        })
        if (error[field])
            setErrorData(error)
        else
            return true
    }

    function revokeError(field) {
        if (errorData[field]) {
            setErrorData(errorData => {
                delete errorData[field]
                return { ...errorData }
            })
        }
    }

    return (
        <form id={styles.productForm} onSubmit={e => {
            e.preventDefault()
            if (!validateForm())
                return
            createProduct()
        }}>
            <h1 className={styles.h1}>List a Product</h1>
            <div>
                <div className={styles.imageUploader}>
                    {previews.map((preview, i) => <div className={styles.previewImageContainer} key={i}><img src={preview} alt='Uploaded' className={styles.uploadedImage} /> <img onClick={() => cancelImage(i)} className={styles.cancelX} src={cancelXIcon} alt="cancel image" /> {i === 0 && <div className={styles.main}>MAIN</div>}</div>)}
                    {previews.length < 9 && <div className={styles.imageUploadButton}>
                        <label htmlFor={'file-upload'} className={styles.btnImageUpload}>
                            <img src={selectedFiles?.length ? plusIcon : productUploadIcon} alt="upload product image" />
                            <h2>Add Image(s)</h2>
                        </label>
                        <input type="file" accept="image/*" id='file-upload' name="productImage" hidden multiple onChange={handleFilesChange} />
                    </div>}
                </div>
                <div className={`${styles.error} ${styles.imageError}`}>{errorData.productImages}</div>
            </div>
            <div className={styles.fields}>
                <div className={styles.field}>
                    <label htmlFor="product-title">Product Title</label>
                    <div>
                        <input type="text" id="product-title" name="productName" value={productData.productName} onChange={e => {
                            setProductData(productData => ({ ...productData, productName: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.productName}</div>
                    </div>
                </div>
                <div className={styles.field}>
                    <label htmlFor="description">Description</label>
                    <div>
                        <textarea type="text" id="description" name="description" value={productData.description} onChange={e => {
                            setProductData(productData => ({ ...productData, description: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.description}</div>
                    </div>
                </div>
                <div className={styles.field}>
                    <label htmlFor="price">Price</label>
                    <div>
                        <input type="number" id="price" name="price" value={productData.price} onChange={e => {
                            setProductData(productData => ({ ...productData, price: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.price}</div>
                    </div>
                </div>
                <div className={styles.field}>
                    <label htmlFor="mrp">Maximum Retail Price</label>
                    <div>
                        <input type="number" id="mrp" name="mrp" value={productData.mrp} onChange={e => {
                            setProductData(productData => ({ ...productData, mrp: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.mrp}</div>
                    </div>
                </div>
                <div className={styles.field}>
                    <label htmlFor="category">Category</label>
                    <div>
                        <input type="text" id="category" name="category" value={productData.category} onChange={e => {
                            setProductData(productData => ({ ...productData, category: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.category}</div>
                    </div>
                </div>
                <div className={styles.field}>
                    <label htmlFor="stock">Quantity in stock</label>
                    <div>
                        <input type="number" id="stock" name="stock" value={productData.stock} onChange={e => {
                            setProductData(productData => ({ ...productData, stock: e.target.value }))
                            revokeError(e.target.name)
                            }} onBlur={e => validateFormField(e.target.name)} />
                        <div className={styles.error}>{errorData.stock}</div>
                    </div>
                </div>
                <button className={styles.btnSubmit}>{editingProduct ? 'Edit Product Listing' : 'List Product'}</button>
            </div>
        </form>
    )
}