let checkoutData = JSON.parse(sessionStorage.getItem("checkoutData"))
if (!checkoutData)
    window.location.href = "./error.html"

const urlParams = new URLSearchParams(window.location.search)
const addressId = urlParams.get("addressId")
if(addressId)
    fetchShippingAddress(addressId)

const addressSpan = document.getElementsByClassName("address")[0]

function fetchShippingAddress(addressId) {
    fetch(`/api/addresses/${addressId}`, {
        method: "GET",
        credentials: "include",
    })
        .then(response => response.json())
        .then(address => {
            checkoutData = address
            addressSpan.innerHTML = `${address.name} <br> ${address.addressLine1},... , ${address.pincode}`
        })
}

const changeAddressBtn = document.querySelector(".btn-change-address")
changeAddressBtn.addEventListener("click", () => {
    window.location.href = `./manage-addresses.html?mode=select&selected=${checkoutData.addressId}&returnTo=checkout.html`
})

addressSpan.innerHTML = `${checkoutData.name} <br> ${checkoutData.addressLine1},... , ${checkoutData.pincode}`

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
        sessionStorage.removeItem("checkoutData")
        console.log("Order placed")
    })
})