const searchForm = document.querySelector("form")
const productsContainer = document.querySelector(".products-container")
const searchSummary = document.querySelector(".search-summary")
const rangeStart = searchSummary.querySelector(".range-start")
const rangeEnd = searchSummary.querySelector(".range-end")
const resultsCount = searchSummary.querySelector(".results-count")
const searchKeyword = searchSummary.querySelector(".search-keyword")
const productTemplate = document.getElementById("product-template")
const toast = document.getElementById("toast")

function getProducts(keyword = "") {
    productsContainer.innerHTML = ""
    fetch(`/api/products?keyword=${keyword}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(res => res.json())
        .then(products => {
            if (keyword) {
                rangeStart.textContent = products.number * products.size + products.numberOfElements ? 1 : 0
                rangeEnd.textContent = products.number * products.size + products.numberOfElements
                resultsCount.textContent = products.totalElements
                searchKeyword.textContent = keyword
                searchSummary.classList.add("open")
            }
            const productsFragment = document.createDocumentFragment()
            products.content.forEach(product => {
                const productCard = productTemplate.content.children[0].cloneNode(true)
                const productImage = productCard.querySelector(".product-image")
                productImage.setAttribute("src", product.image)
                const productTitle = productCard.querySelector(".product-title")
                productTitle.textContent = product.productName
                const productPrice = productCard.querySelector(".product-price")
                productPrice.textContent = "₹ " + product.price
                productsFragment.append(productCard)
                productCard.setAttribute("data-product-id", product.productId)
                productCard.setAttribute("data-category", product.category)
            })
            productsContainer.append(productsFragment)
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
    const addToCartBtn = e.target.closest(".btn-add-to-cart")
    if (addToCartBtn) {
        e.stopPropagation()
        const productId = e.target.closest(".product-card").getAttribute("data-product-id")
        fetch("/api/cart/items", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ productId: productId })
        })
            .then(res => {
                if (res.ok) {
                    toast.classList.add("show")
                    toast.textContent = "Product added to cart"
                    setTimeout(() => toast.classList.remove("show"), 1500)
                }
            })
    }
    else if (e.target != productsContainer) {
        const productId = e.target.closest(".product-card").getAttribute("data-product-id")
        window.open(`./product-details.html?productId=${parseInt(productId)}`)
    }
})

function setupHeader() {

    const profileBtn = document.querySelector(".btn-profile")
    const header = document.querySelector("header")

    profileBtn.addEventListener("click", () => {
        window.location.href = "./login.html"
    })

    let isLoggedIn
    fetch("/api/auth/me", {
        method: "GET",
        credentials: "include"
    })
        .then((response) => {
            if (response.ok) {
                header.classList.add("logged-in")
                profileBtn.addEventListener("click", () => {
                    window.location.href = "./profile.html"
                })
                return response.json()
            }
        })
        .then((res) => {
            if (res) {
                const profileBtnTxt = document.querySelector(".btn-profile-txt")
                if (res.firstName) {
                    profileBtnTxt.textContent = res.firstName
                }
                else
                    profileBtnTxt.textContent = "User"
            }
        })
}
setupHeader()