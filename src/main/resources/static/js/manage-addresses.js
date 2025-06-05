const urlParams = new URLSearchParams(window.location.search);
const mode = urlParams.get("mode");
const selectedAddressId = urlParams.get("selected")
const isSelectMode = mode == "select"

const addressesContainer = document.querySelector(".addresses-container")
const addressCardTemplate = document.getElementById("address-card")

if (isSelectMode) {
    addressesContainer.classList.add("select-mode")
}

function refreshAddresses(addresses) {
    const addressesFragment = document.createDocumentFragment()
    addresses.forEach(addressData => {
        const addressCard = addressCardTemplate.content.children[0].cloneNode(true)
        const address = addressCard.querySelector(".address")
        address.innerHTML = `${addressData.name} - ${addressData.mobileNumber} <br> ${addressData.addressLine1}, ${addressData.addressLine2}, ${addressData.city}, ${addressData.state} - ${addressData.pincode}`
        addressCard.setAttribute("data-address-id", addressData.addressId)
        if (isSelectMode && addressData.addressId == selectedAddressId)
            addressCard.classList.add("selected")
        addressesFragment.append(addressCard)
    })
    addressesContainer.append(addressesFragment)
}

fetch("/api/addresses", {
    method: "GET",
    credentials: "include",
})
    .then(response => response.json())
    .then(addresses => refreshAddresses(addresses))

addressesContainer.addEventListener("click", e => {
    e.stopPropagation()
    if (e.target.classList.contains("three-dots")) {
        document.querySelector(".three-dots-menu.open")?.classList.remove("open")
        e.target.closest(".three-dots-menu").classList.toggle("open")
    }
    else if (e.target.classList.contains("set-default")) {
        e.target.closest(".three-dots-menu").classList.remove("open")
        const addressId = e.target.closest(".address-card").getAttribute("data-address-id")
        fetch(`/api/addresses/${addressId}/default`, {
            method: "PUT",
            credentials: "include"
        })
            .then()
    }
    else if (e.target.classList.contains("remove")) {
        const addressId = e.target.closest(".address-card").getAttribute("data-address-id")
        fetch(`/api/addresses/${addressId}`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(() => e.target.closest(".address-card").remove())
    }
    else if (e.target.classList.contains("edit")) {
        const addressId = e.target.closest(".address-card").getAttribute("data-address-id")
        //delegate field data to edit page
    }
    else {
        document.querySelector(".three-dots-menu.open")?.classList.remove("open")
        const addressCard = e.target.closest(".address-card")
        if (addressCard) {
            document.querySelector(".three-dots-menu.open")?.classList.remove("open")
            addressesContainer.querySelector(".address-card.selected")?.classList.remove("selected")
            addressCard.classList.add("selected")
            selectedAddressId = addressCard.getAttribute("data-address-id")
        }
    }
})

const addAddressBtn = document.getElementsByClassName("btn-add-address")[0]
addAddressBtn.addEventListener("click", () => {
    window.location.href = "./add-address.html"
})

const deliverAddressBtn = document.querySelector(".btn-deliver-address")
deliverAddressBtn.addEventListener("click", () => {
    window.location.href = `./cart.html?addressId = ${selectedAddressId}`
})