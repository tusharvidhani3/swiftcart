const urlParams = new URLSearchParams(window.location.search)
const isEditMode = urlParams.get("mode") == "edit"
const addressToEdit = JSON.parse(sessionStorage.getItem("addressToEdit"))

if (isEditMode && addressToEdit) {
    document.getElementsByClassName("btn-add-address")[0].textContent = "Edit Address"
    const fields = {
        name: 'name',
        'mobile-number': 'mobileNumber',
        'address-line-1': 'addressLine1',
        'address-line-2': 'addressLine2',
        city: 'city',
        state: 'state',
        pincode: 'pincode',
    }
    for (let key in fields) {
        const input = document.getElementById(key);
        if (input) input.value = addressToEdit[fields[key]];
    }
    document.getElementsByName("addressType")[0].value = addEditAddress["addressType"]
}
const addressForm = document.getElementsByTagName("form")[0]

addressForm.addEventListener("submit", e => {
    e.preventDefault()
    const formData = new FormData(addressForm)
    const addressData = Object.fromEntries(formData)
    if(isEditMode)
        addressData["addressId"] = parseInt(addressToEdit.addressId)
    addEditAddress(addressData)
})

function addEditAddress(addressData) {
    fetch(`/api/addresses/${addressData ? addressData.addressId : ""}`, {
        method: `${addressData?"PUT":"POST"}`,
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(addressData)
    })
        .then(() => {
            window.location.href = `./manage-addresses.html`
        })
}