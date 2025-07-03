const header = document.querySelector("header")

export function setupHeader() {

    const logout = header.getElementsByClassName("logout")[0]
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

    await fetch("/api/customer/me", {
        method: "GET",
        credentials: "include"
    })
        .then((response) => {
            if (response.ok) {
                window.isLoggedIn = true
                fetchCartCount()
                header.classList.add("logged-in")
                if(window.innerWidth < 768)
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
        .then((customer) => {
            if (customer) {
                const profileBtnTxt = header.querySelector(".btn-profile-txt")
                if (customer.firstName) {
                    profileBtnTxt.textContent = res.firstName
                }
                else
                    profileBtnTxt.textContent = "User"
            }
        })
}