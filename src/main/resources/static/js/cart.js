const cartItemsContainer = document.querySelector(".cart-items-container")
const cartItemTemplate = document.getElementById("cart-item")

const urlParams = new URLSearchParams(window.location.search);
const selectedAddressId = urlParams.get("addressId")

fetch("/api/cart", {
    method: "GET",
    credentials: "include",
})
    .then(response => response.json())
    .then(cartResponse => refreshCart(cartResponse))

function refreshCart(cartResponse) {
    cartItemsContainer.innerHTML = ""
    const cartItems = cartResponse.cartItems
    const cartItemsFragment = document.createDocumentFragment()
    cartItems.forEach(cartItemData => {
        const cartItem = cartItemTemplate.content.children[0].cloneNode(true)
        const productImage = cartItem.querySelector(".product-image")
        productImage.setAttribute("src", cartItemData.product.image)
        const productTitle = cartItem.querySelector(".product-title")
        productTitle.textContent = cartItemData.product.productName
        const productPrice = cartItem.querySelector(".product-price")
        productPrice.textContent = `₹ ${cartItemData.product.price}`
        cartItemsFragment.append(cartItem)
        const qty = cartItem.querySelector(".qty")
        qty.textContent = cartItemData.quantity
        cartItem.setAttribute("data-product-id", cartItemData.product.productId)
        cartItem.setAttribute("data-cart-item-id", cartItemData.cartItemId)
    })
    cartItemsContainer.append(cartItemsFragment)
    const cartTotal = document.querySelector(".total-amount")
    const priceToPay = document.querySelector(".price-to-pay")
    const price = document.querySelector(".price")
    const cartItemsCount = document.querySelector(".cart-items-count")
    cartTotal.textContent = cartResponse.totalPrice
    priceToPay.textContent = cartResponse.totalPrice
    price.textContent = cartResponse.totalPrice
    cartItemsCount.textContent = cartResponse.cartItems.length
}

cartItemsContainer.addEventListener("click", (e) => {
    e.stopPropagation()
    const cartItemId = e.target.closest(".cart-item").getAttribute("data-cart-item-id")
    if (e.target.parentElement.classList.contains("btn-qty")) {
        const currentQty = parseInt(e.target.closest(".cart-item").querySelector(".qty").textContent)
        if (e.target.parentElement.classList.contains("btn-increment-qty")) {
            fetch(`/api/cart/items/${cartItemId}`, {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ "quantity": currentQty + 1 })
            })
            .then(response => response.json())
            .then(cartResponse => refreshCart(cartResponse))
        }
        else {
            fetch(`/api/cart/items/${cartItemId}`, {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ "quantity": currentQty - 1 })
            })
            .then(response => response.json())
            .then(cartResponse => refreshCart(cartResponse))
        }
    }
    else if (e.target.classList.contains("btn-delete")) {
        fetch(`/api/cart/items/${cartItemId}`, {
            method: "DELETE",
            credentials: "include"
        })
        .then(response => response.json())
        .then(cartResponse => refreshCart(cartResponse))
    }
    else {
        const productId = e.target.closest(".cart-item").getAttribute("data-product-id")
        window.location.href = `./product-details.html?productId=${productId}`
    }

})

const changeAddressBtn = document.querySelector(".btn-change-address")
const addressSpan = document.querySelector(".address")

function fetchShippingAddress(addressId) {
    fetch(`/api/addresses/${addressId ?? "default"}`, {
        method: "GET",
        credentials: "include",
    })
        .then(response => response.json())
        .then(address => {
            addressSpan.innerHTML = `${address.name} <br> ${address.addressLine1},... ${address.pincode}`
            changeAddressBtn.addEventListener("click", () => {
                window.location.href = `.manage-addresses/?mode=select?selected=${address.addressId}`
            })
        })
}
fetchShippingAddress(selectedAddressId)

const payBtn = document.querySelector(".btn-pay")
payBtn.addEventListener(("click"), () => {

})