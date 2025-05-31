const cartItemsContainer = document.querySelector(".cart-items-container")
const cartItemTemplate = document.getElementById("cart-item")

fetch("/api/cart", {
    method: "GET",
    credentials: "include",
})
    .then(response => response.json())
    .then(cartResponse => refreshCart(cartResponse))

function refreshCart(cartResponse) {
    console.log(cartResponse)
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
    cartTotal.textContent = cartResponse.totalPrice
    priceToPay.textContent = cartResponse.totalPrice
}

cartItemsContainer.addEventListener("click", (e) => {
    e.stopPropagation()
    console.log(e.target.parentElement)
    const cartItemId = e.target.closest(".cart-item").getAttribute("data-cart-item-id")
    if (e.target.parentElement.classList.contains("btn-qty")) {
        const currentQty = parseInt(e.target.closest(".cart-item").querySelector(".qty").textContent)
        console.log(currentQty)
        if (e.target.parentElement.classList.contains("btn-increment-qty")) {
            fetch(`/api/cart/items/${cartItemId}`, {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ "quantity": currentQty + 1 })
            })
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
                .then(cartResponse => refreshCart(cartResponse))
        }
    }
    else if (e.target.classList.contains("btn-delete")) {
        fetch(`/api/cart/items/${cartItemId}`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(cartResponse => refreshCart())
    }
    else if (e.target.classList.contains("cart-item")) {
        const productId = e.target.getAttribute("data-product-id")
        window.location.href = `./product-details.html?productId=${productId}`
    }

})

const changeAddressBtn = document.querySelector(".btn-change-address")

function fetchShippingAddress(addressId) {
    fetch(`/api/${addressId ?? "default"}`, {
        method: "GET",
        credentials: "include",
    })
        .then(response => response.json())
        .then(address => {

            changeAddressBtn.addEventListener("click", () => {
                window.location.href = `./?selected=${address.addressId}`
            })
        })
}

