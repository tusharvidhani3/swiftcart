const profileForm = document.getElementById("profile-form")

profileForm.addEventListener("submit", e => {
    e.preventDefault()
    const profileFormData = new FormData(profileForm)
    fetch("/api/users", {
        method: "PUT",
        headers: {
            "Content-type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(Object.fromEntries(profileFormData.entries()))
    })
    .then(() => window.location.reload())

})

function fetchUserData() {
    fetch("/api/users/me", {
        method: "GET",
        credentials: "include",
    })
    .then(res => {
        if(res.ok)
        return res.json()
    else
    window.location.href = "./login.html"
    })
    .then(userData => {
        console.log(userData)
        for(const field in userData) {
            if(field !== "userId" && field !== "role")
                document.getElementById(field).value = userData[field]
        }

        profileForm.addEventListener("input", e => {
            if(userData[e.target.id] === e.target.value || (e.target.value === "" && userData[e.target.id] === null))
                profileForm.classList.remove("changed")
            else
            profileForm.classList.add("changed")
        })
    })
}

fetchUserData()