const addressForm = document.getElementsByTagName("form")[0]

addressForm.addEventListener("submit", e => {
    e.preventDefault()
    const formData = new FormData(addressForm)
    const addressData = Object.fromEntries(formData)
    
})

function addEditAddress(addressId) {
    fetch(`/api/addresses/${addressId??""}`, {
        method: `{addressId}?PUT:POST`,
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