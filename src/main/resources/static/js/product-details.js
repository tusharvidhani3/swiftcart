const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("productId");

const productImage = document.querySelector(".product-image")
const productTitle = document.querySelector(".product-title")
const productPrice = document.querySelector(".product-price")
const productDescription = document.getElementsByClassName("product-description")[0]
const toast = document.getElementById("toast")

function getProductDetails(productId) {
    fetch(`/api/products/${productId}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(res => res.json())
        .then(product => {
            productImage.src = product.image
            productTitle.textContent = product.productName
            productPrice.textContent = "₹ " + product.price
            productDescription.textContent = product.description
        })
}

getProductDetails(productId)

const buyNowBtn = document.querySelector(".btn-buy-now")
const addToCartBtn = document.querySelector(".btn-add-to-cart")

buyNowBtn.addEventListener('click', (e) => {
    e.stopPropagation()
    fetch(`/api/customer/cart/checkout/buy-now/${productId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
    // .then(cartItem => )
})

addToCartBtn.addEventListener('click', (e) => {
    e.stopPropagation()
    if (e.target.closest(".go-to-cart"))
        window.location.href = "./cart.html"
    else {
        fetch(`/api/cart/items`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ productId: productId })
        })
            .then(res => {
                if (res.ok) {
                    toast.classList.add("show")
                    toast.textContent = "Item added to cart!"
                    setTimeout(() => toast.classList.remove("show"), 1500)
                    addToCartBtn.textContent = "Go to Cart"
                    addToCartBtn.classList.add("go-to-cart")
                }
            })
    }
})