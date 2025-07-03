const profileForm = document.getElementById("profile-form")

profileForm.addEventListener("submit", e => {
    e.preventDefault()
    const profileFormData = new FormData(profileForm)
    fetch("/api/customer", {
        method: "PUT",
        headers: {
            "Content-type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(Object.fromEntries(profileFormData.entries()))
    })
    // .then(() => window.location.reload())

})

function fetchUserData() {
    fetch("/api/customer/me", {
        method: "GET",
        credentials: "include",
    })
    .then(res => {
        if(res.ok)
        return res.json()
    else
    window.location.href = "./login.html"
    })
    .then(customer => {
        console.log(customer)
        for(const field in customer) {
            if(field !== "userId" && field !== "roles")
                document.getElementById(field).value = customer[field]
        }

        profileForm.addEventListener("input", e => {
            if(customer[e.target.id] === e.target.value || (e.target.value === "" && customer[e.target.id] === null))
                profileForm.classList.remove("changed")
            else
            profileForm.classList.add("changed")
        })
    })
}

fetchUserData()