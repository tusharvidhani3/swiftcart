fetch("/api/auth/check", {
    method: "GET",
    credentials: "include"  // VERY IMPORTANT to send cookies
})
    .then((response) => {
        if(response.ok) {
            window.location.href = "/";
        }
        else {
        continueLogin()
        }
    })
    .catch(() => continueLogin())

function continueLogin() {

    const loginForm = document.querySelector("form")

    loginForm.addEventListener("submit", e => {
        e.preventDefault()
        const formData = new FormData(loginForm)
        const data = Object.fromEntries(formData.entries())
        login(data)
    })
}

function login(loginRequest) {
    const params = new URLSearchParams(window.location.search);
    const redirectTo = params.get("redirect") || "/";
    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(loginRequest)
    })
        .then(res => res.text())
        .then(() => {
            window.location.href = redirectTo

        })
        .catch(() => alert("Login failed"))
}