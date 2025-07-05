const urlParams = new URLSearchParams(window.location.search);
const returnTo = urlParams.get("returnTo")
const isSelectMode = Boolean(returnTo)
const selectedAddress = isSelectMode?JSON.parse(localStorage.getItem("selectedAddress")):undefined

const addressesContainer = document.querySelector(".addresses-container")
const addressCardTemplate = document.getElementById("address-card")

if (isSelectMode) {
    addressesContainer.classList.add("select-mode")
}

const fields = {
  name: 'name',
  'mobile-number': 'mobileNumber',
  'address-line-1': 'addressLine1',
  'address-line-2': 'addressLine2',
  city: 'city',
  state: 'state',
  pincode: 'pincode',
  'address-type': 'addressType'
}

function refreshAddresses(addresses) {
    const addressesFragment = document.createDocumentFragment()
    addresses.forEach(addressData => {
        const addressCard = addressCardTemplate.content.children[0].cloneNode(true)
        const address = addressCard.querySelector(".address")
        for(const className in fields) {
        address.getElementsByClassName(className)[0].textContent = addressData[fields[className]]
        }
        
        addressCard.setAttribute("data-address-id", addressData.addressId)
        if(addressData.isDefaultShipping)
            addressCard.classList.add("default")
        if (isSelectMode && addressData.addressId == selectedAddress.addressId)
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
            .then(res => {
                if(res.ok)
                    window.location.reload()
            })
    }
    else if (e.target.classList.contains("delete")) {
        const addressId = e.target.closest(".address-card").getAttribute("data-address-id")
        fetch(`/api/addresses/${addressId}`, {
            method: "DELETE",
            credentials: "include"
        })
            .then(res => {
                if(res.ok)
                    e.target.closest(".address-card").remove()
            })
    }
    else if (e.target.classList.contains("edit")) {
        const addressCard = e.target.closest(".address-card")
        const addressId = addressCard.getAttribute("data-address-id")
        const address = addressCard.getElementsByClassName("address")[0]
        const addressData = {addressId}
        for(const className in fields) {
        addressData[fields[className]] = address.getElementsByClassName(className)[0].textContent
        }
        sessionStorage.setItem("addressToEdit", JSON.stringify(addressData))
        window.location.href = `./add-address.html?mode=edit`
    }
    else {
        const addressCard = e.target.closest(".address-card")
        if (addressCard) {
            document.querySelector(".three-dots-menu.open")?.classList.remove("open")
            addressesContainer.querySelector(".address-card.selected")?.classList.remove("selected")
            addressCard.classList.add("selected")
            selectedAddress.addressId = addressCard.getAttribute("data-address-id")
            const address = addressCard.getElementsByClassName("address")[0]
            for(const className in fields) {
                selectedAddress[fields[className]] = address.getElementsByClassName(className)[0].textContent
            }
        }
    }
})

const addAddressBtn = document.getElementsByClassName("btn-add-address")[0]
addAddressBtn.addEventListener("click", () => {
    window.location.href = "./add-address.html"
})

const deliverAddressBtn = document.querySelector(".btn-deliver-address")
deliverAddressBtn.addEventListener("click", () => {
    localStorage.setItem("selectedAddress", JSON.stringify(selectedAddress))
    window.location.href = `./${returnTo}`
})