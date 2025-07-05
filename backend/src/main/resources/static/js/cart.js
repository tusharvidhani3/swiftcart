import { setupHeader } from "./global.js";
import { setupUser } from "./global.js";
setupHeader()
await setupUser()

const cartItemsContainer = document.querySelector(".cart-items-container")
const cartItemTemplate = document.getElementById("cart-item")

let checkoutData = {}

if (window.isLoggedIn) {
    fetch("/api/cart", {
        method: "GET",
        credentials: "include",
    })
        .then(response => response.json())
        .then(cartResponse => {
            refreshCart(cartResponse)
            checkoutData["cartId"] = cartResponse.cartId
        })
}
else
    refreshCart()

function refreshCart(cartResponse) {

    if (!window.isLoggedIn) {
        document.getElementsByTagName("main")[0].classList.add("cart-empty")
        document.getElementsByTagName("main")[0].style.backgroundColor = "white"
        const emptyCart = document.getElementsByClassName("empty-cart")[0]
        emptyCart.getElementsByTagName("img")[0].src = "assets/images/d438a32e-765a-4d8b-b4a6-520b560971e8.webp"
        emptyCart.getElementsByTagName("h2")[0].textContent = "Missing Cart items?"
        emptyCart.getElementsByTagName("p")[0].textContent = "Login to see the items you added previously"
        const shopNow = emptyCart.getElementsByClassName("shop-now")[0]
        shopNow.textContent = "Login"
        shopNow.href = "./login.html"
        return
    }

    else if (!cartResponse.cartItems.length) {
        document.getElementsByTagName("main")[0].classList.add("cart-empty")
        return
    }

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
        if (cartItemData.quantity == 1)
            cartItem.getElementsByClassName("btn-decrement-qty")[0].classList.add("grayed-out")
        cartItem.setAttribute("data-product-id", cartItemData.product.productId)
        cartItem.setAttribute("data-cart-item-id", cartItemData.cartItemId)
    })
    cartItemsContainer.append(cartItemsFragment)
    const cartTotal = document.querySelector(".total-amount")
    const priceToPay = document.querySelector(".price-to-pay")
    const price = document.querySelector(".price")
    const cartItemsCount = document.querySelector(".cart-items-count")
    cartTotal.textContent = `₹ ${cartResponse.totalPrice}`
    priceToPay.textContent = `₹ ${cartResponse.totalPrice}`
    price.textContent = `₹ ${cartResponse.totalPrice}`
    cartItemsCount.textContent = cartResponse.cartItems.length
}

const toast = document.getElementById("toast")
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
                .then(response => {
                    if (response.ok)
                        return response.json()
                    else {
                        toast.textContent = "Cannot add more items. Stock limit reached"
                        toast.classList.add("show")
                        setTimeout(() => toast.classList.remove("show"), 1500)
                    }
                })
                .then(cartResponse => {
                    if (cartResponse)
                        refreshCart(cartResponse)
                })
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
    else if (e.target.classList.contains("btn-remove")) {
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

const checkoutBtn = document.querySelector(".btn-checkout")
checkoutBtn.addEventListener(("click"), () => {
    sessionStorage.setItem("checkoutData", JSON.stringify(checkoutData))
    window.location.href = "./checkout.html"
})