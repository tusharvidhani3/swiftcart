const profileForm = document.getElementById("profile-form")

function fetchUserData() {
    fetch("/api/auth/me", {
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
        for(const field in userData) {
            if(field !== "userId")
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