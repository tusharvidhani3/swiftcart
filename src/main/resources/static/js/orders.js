const ordersContainer = document.getElementsByClassName("orders-container")[0]
const orderItemCardTemplate = document.getElementById("order-item-card")
const months = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  ];

fetch("/api/orders/items", {
    method: "GET",
    credentials: "include"
})
.then(res => {
    if(res.status === 403)
        window.location.href = "./login.html"
    return res.json()
})
.then(orderItemsPage => {
    const orderItems = orderItemsPage.content
    const ordersFragment = document.createDocumentFragment()
    orderItems.forEach(orderItem => {
        const orderItemCard = orderItemCardTemplate.content.children[0].cloneNode(true)
        orderItemCard.getElementsByClassName("product-title")[0].textContent = orderItem.product.productName
        orderItemCard.getElementsByTagName("img")[0].src = orderItem.product.image
        orderItemCard.setAttribute("data-order-id", orderItem.orderId)
        const dateObj = new Date(orderItem.placedAt)
        orderItemCard.getElementsByClassName("order-date")[0].textContent = `${months[dateObj.getMonth()]} ${dateObj.getDate()}`
        ordersFragment.append(orderItemCard)
    });
    ordersContainer.append(ordersFragment)
})

ordersContainer.addEventListener("click", e => {
    const orderItemCard = e.target.closest(".order-item-card")
    if(orderItemCard)
        window.location.href = `./order-summary.html?orderId=${orderItemCard.getAttribute("data-order-id")}`
})