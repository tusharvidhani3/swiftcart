const productForm = document.getElementsByTagName("form")[0]

productForm.addEventListener("submit", e => {
    e.preventDefault()
    const productFormData = new FormData(productForm)
    const productData = {}
    productFormData.forEach((value, key) => {
        if (key !== "productImage") {
            productData[key] = value;
        }
    })
    const formData = new FormData()
    formData.append("createProductRequest", JSON.stringify(productData))
    formData.append("productImage", productForm.querySelector('input[type="file"]').files[0])
    console.log(productData)
    fetch("/api/products", {
        method: "POST",
        credentials: "include",
        body: formData
    })
        .then(() => console.log("success"))
    // .then(() => window.location.href = "./products-listed.html")
})