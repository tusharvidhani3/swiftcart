const urlParams = new URLSearchParams(window.location.search)
const addressId = urlParams.get("addressId")
let checkoutData = {}
const cartItemsCount = document.getElementById("cart-items-count")
const price = document.getElementsByClassName("price")[0]
const totalAmount = document.getElementsByClassName("total-amount")[0]
const priceToPay = document.getElementsByClassName("price-to-pay")[0]

fetchShippingAddress(addressId)

const address = {
    "name": document.getElementById("name"),
    "addressLine1": document.getElementById("address-line-1"),
    "addressLine2": document.getElementById("address-line-2"),
    "city": document.getElementById("city"),
    "state": document.getElementById("state"),
    "pincode": document.getElementById("pincode"),
    "mobileNumber": document.getElementById("mobile-number"),
    "address-type": document.getElementById("address-type")
}

function fetchShippingAddress(addressId) {
    fetch(`/api/addresses/${addressId ?? "default"}`, {
        method: "GET",
        credentials: "include",
    })
        .then(response => response.json())
        .then(addressData => {
            checkoutData = addressData
            for (const field in address)
                address[field].textContent = checkoutData[field]
        })
}

const changeAddressBtn = document.querySelector(".btn-change-address")
changeAddressBtn.addEventListener("click", () => {
    window.location.href = `./manage-addresses.html?mode=select&selected=${checkoutData.addressId}&returnTo=checkout.html`
})

for (const field in address)
    address[field].textContent = checkoutData[field]

const payBtn = document.getElementsByClassName("btn-pay")[0]
payBtn.addEventListener("click", e => {
    fetch("/api/orders/checkout", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-type": "application/json"
        },
        body: JSON.stringify({
            "cartId": checkoutData.cartId,
            "paymentMethod": document.querySelector('input[name="paymentMethod"]:checked').value,
            "shippingAddressId": checkoutData.addressId
        })
    })
        .then(() => {
            console.log("Order placed")
        })
})

function fetchCartSummary() {
    fetch("/api/cart/summary", {
        method: "GET",
        credentials: "include",
    })
    .then(res => res.json())
    .then(summary => {
        cartItemsCount.textContent = summary.cartItemsCount
        price.textContent = summary.totalPrice
        totalAmount.textContent = summary.totalPrice
        checkoutData.cartId = summary.cartId
        priceToPay.textContent = `₹ ${summary.totalPrice}`
    })
    // .catch(() => window.location.href = "./error.html")
}

fetchCartSummary()