const urlParams = new URLSearchParams(window.location.search)
const orderId = urlParams.get("orderId")

const orderIdSpan = document.getElementsByClassName("order-id")[0]
const orderDate = document.getElementsByClassName("order-date")[0]
const orderTotal = document.getElementsByClassName("order-total")[0]
const productsOrdered = document.getElementsByClassName("products-ordered")[0]
const orderItemTemplate = document.getElementById("order-item")
const months = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  ];

fetch(`/api/orders/${orderId}`, {
    method: "GET",
    credentials: "include"
})
.then(res => res.json())
.then(orderResponse => {
    orderIdSpan.textContent = orderId
    const date = new Date(orderResponse.placedAt)
    orderDate.textContent = `${date.getDate()}-${months[date.getMonth()]}-${date.getFullYear()}`
    orderTotal.textContent = `₹ ${orderResponse.totalAmount}`
    const orderItemsFragment = document.createDocumentFragment()
    orderResponse.orderItems.forEach(orderItemData => {
        const orderItem = orderItemTemplate.content.children[0].cloneNode(true)
        orderItem.setAttribute("data-order-item-id", orderItemData.orderItemId)
        orderItem.setAttribute("data-product-id", orderItemData.product.productId)
        orderItem.getElementsByTagName("img")[0].src = orderItemData.product.image
        orderItem.getElementsByClassName("product-title")[0].textContent = orderItemData.product.productName
        orderItem.getElementsByClassName("item-total")[0].textContent = `₹ ${orderItemData.product.price * orderItemData.quantity}`
        if(orderItemData.quantity>1) {
            const productQty = orderItem.getElementsByClassName("product-qty")[0]
            productQty.textContent = orderItemData.quantity
            productQty.classList.add("show")
        }
        orderItem.getElementsByClassName("order-status")[0].textContent = orderItemData.orderStatus
        orderItemsFragment.append(orderItem)
    });
    productsOrdered.append(orderItemsFragment)
    const shipToName = document.getElementsByClassName("name")[0]
    shipToName.textContent = orderResponse.shippingAddress.name
    const shipToAddress = document.getElementsByClassName("address")[0]
    shipToAddress.textContent = `${orderResponse.shippingAddress.addressLine1}, ${orderResponse.shippingAddress.addressLine2}, ${orderResponse.shippingAddress.city}, ${orderResponse.shippingAddress.state} - ${orderResponse.shippingAddress.pincode}`
    const itemsTotal = document.getElementsByClassName("items-total")[0]
    itemsTotal.textContent = `₹ ${orderResponse.totalAmount}`
    const deliveryCharge = document.getElementsByClassName("delivery-charge")[0]
    deliveryCharge.textContent = `FREE DELIVERY`
    document.getElementsByClassName("order-total")[1].textContent = `₹ ${orderResponse.totalAmount}`
    document.getElementsByClassName("payment-method")[0].textContent = orderResponse.paymentMethod
})

productsOrdered.addEventListener("click", e => {
    const orderItem = e.target.closest(".order-item")
    const returnBtn = e.target.closest(".btn-review")
    const reviewBtn = e.target.closest(".btn-review")
    if(returnBtn) {
        const orderItemId = orderItem.getAttribute("data-order-item-id")
        fetch("/api/")
    }
    else if(reviewBtn) {

    }
    else if(orderItem) {
        window.location.href = `./product-details.html?productId=${orderItem.getAttribute("data-product-id")}`
    }
})