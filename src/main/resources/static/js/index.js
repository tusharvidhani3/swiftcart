const searchForm = document.querySelector("form")
const productsContainer = document.querySelector(".products-container")
const searchSummary = document.querySelector(".search-summary")
const rangeStart = searchSummary.querySelector(".range-start")
const rangeEnd = searchSummary.querySelector(".range-end")
const resultsCount = searchSummary.querySelector(".results-count")
const searchKeyword = searchSummary.querySelector(".search-keyword")
let productCard = document.querySelector(".product-card")
function getProducts(keyword = "") {
    productsContainer.innerHTML=""
    fetch(`/api/products?keyword=${keyword}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
    .then(res => res.json())
    .then(products => {
        if(keyword) {
        rangeStart.textContent = products.number*products.size+products.numberOfElements?1:0
        rangeEnd.textContent = products.number*products.size+products.numberOfElements
        resultsCount.textContent = products.totalElements
        searchKeyword.textContent = keyword
        searchSummary.classList.add("open")
        }
        products.content.forEach(product => {
        productCard = productCard.cloneNode(true)
        const productImage = productCard.querySelector(".product-image");
        productImage.setAttribute("src", product.image)
        const productTitle = productCard.querySelector(".product-title")
        productTitle.textContent = product.productName
        const productPrice = productCard.querySelector(".product-price")
        productPrice.textContent = "₹ "+product.price
        productsContainer.append(productCard)
        productCard.setAttribute("data-product-id", product.productId)
        productCard.setAttribute("data-category", product.category)
        })
    })
}
getProducts()
searchForm.addEventListener('submit', (e) => {
    e.preventDefault()
    const keyword = searchForm.querySelector("input").value
    getProducts(keyword)
})

productsContainer.addEventListener('click', (e) => {
    e.stopPropagation()
    if(e.target != productsContainer) {
    const productId = e.target.closest(".product-card").getAttribute("data-product-id")
    window.open(`./product-details.html?productId=${parseInt(productId)}`)
    }
})