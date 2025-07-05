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
    formData.append("createProductRequest", new Blob(
        [JSON.stringify(productData)], { type: "application/json" }
    ))
    const files = productForm.querySelector('input[type="file"]').files
    for (let i = 0; i < files.length; i++) {
        formData.append("productImages", files[i]);
    }
    fetch("/api/products", {
        method: "POST",
        credentials: "include",
        body: formData
    })
        .then(() => console.log("success"))
    // .then(() => window.location.href = "./products-listed.html")
})