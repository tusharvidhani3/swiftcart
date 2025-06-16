export function setupHeader() {

    const profileBtn = document.querySelector(".btn-profile")
    const header = document.querySelector("header")
    const dropDownMenu = document.getElementsByClassName("drop-down-menu")[0]

    fetch("/api/auth/me", {
        method: "GET",
        credentials: "include"
    })
        .then((response) => {
            if (response.ok) {
                header.classList.add("logged-in")
                profileBtn.addEventListener("click", () => {
                    dropDownMenu.classList.toggle("show")
                })
                return response.json()
            }
            else {
                profileBtn.addEventListener("click", () => {
                    window.location.href = "./login.html"
                })
            }
        })
        .then((res) => {
            if (res) {
                const profileBtnTxt = document.querySelector(".btn-profile-txt")
                if (res.firstName) {
                    profileBtnTxt.textContent = res.firstName
                }
                else
                    profileBtnTxt.textContent = "User"
            }
        })

    const myProfile = header.getElementsByClassName("my-profile")[0]
    const logout = header.getElementsByClassName("logout")[0]
    myProfile.addEventListener("click", () => {
        window.location.href = "./profile.html"
    })

    logout.addEventListener("click", () => {
        fetch("/api/auth/logout", {
            method: "POST",
            credentials: "include"
        })
            .then(() => {
                window.location.href = "./login.html"
            })
    })
}