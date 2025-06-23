const header = document.querySelector("header")

export function setupHeader() {

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

    const searchForm = header.querySelector("form")
    searchForm.addEventListener('submit', (e) => {
        e.preventDefault()
        const keyword = searchForm.querySelector("input").value
        window.location.href = `./index.html?keyword=${keyword}`
    })

    const hamburger = header.getElementsByClassName("hamburger")[0]
    hamburger.addEventListener("click", () => {
        document.body.classList.add("hamburger-open")
    })
}

export async function setupUser() {

    function fetchCartCount() {
        const cartQtyCount = header.getElementsByClassName("cart-count")[0]
        fetch("/api/cart/count", {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(qty => cartQtyCount.textContent = qty)
    }

    const profileBtn = header.querySelector(".btn-profile")
    const profile = header.getElementsByClassName("profile")[0]

    await fetch("/api/user/me", {
        method: "GET",
        credentials: "include"
    })
        .then((response) => {
            if (response.ok) {
                window.isLoggedIn = true
                fetchCartCount()
                header.classList.add("logged-in")
                profileBtn.addEventListener("click", () => window.location.href = "./profile.html")
                profile.addEventListener("mouseenter", () => profile.classList.add("show"))
                profile.addEventListener("mouseleave", () => profile.classList.remove("show"))
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
                const profileBtnTxt = header.querySelector(".btn-profile-txt")
                if (res.firstName) {
                    profileBtnTxt.textContent = res.firstName
                }
                else
                    profileBtnTxt.textContent = "User"
            }
        })
}