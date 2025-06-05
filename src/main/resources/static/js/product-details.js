const urlParams = new URLSearchParams(window.location.search);
const productId = urlParams.get("productId");

const productImage = document.querySelector(".product-image")
const productTitle = document.querySelector(".product-title")
const productPrice = document.querySelector(".product-price")

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
        productPrice.textContent = "₹ "+ product.price
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
    fetch(`/api/customer/cart/products/${productId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    })
})