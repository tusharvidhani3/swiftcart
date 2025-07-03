const urlParams = new URLSearchParams(window.location.search)
const mode = urlParams.get("mode")
const user = urlParams.get("user")
const loginBtn = document.getElementsByClassName("btn-login")[0]
const main = document.getElementsByTagName("main")[0]
const formTitle = document.getElementsByClassName("form-title")[0]
if(mode) {
    if(mode === "login") {
        
    }
    else if(user) {
        loginBtn.textContent = "Start Selling"
        main.classList.add("register-seller")
        formTitle.textContent = "Register as Seller"
        document.title = "Seller Registration"
    }
    else {
    loginBtn.textContent = "Register"
    formTitle.textContent = "Register"
    document.title = "Register"
    }
    main.classList.add("register")
}

fetch("/api/customer/me", {
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

function continueLogin() {

    const loginForm = document.querySelector("form")

    loginForm.addEventListener("input", e => {
        if(e.target.tagName === "INPUT") {
            if(e.target.name === "mobileNumber") {
                if(!/^[6-9]/.test(e.target.value) && e.target.value.length > 0)
                    showError("Indian mobile numbers must start with 6–9", e.target);
                else if(e.target.value>10)
                    showError("Must be 10 digits only", e.target)
            }
        }
    })

    loginForm.addEventListener("submit", e => {
        e.preventDefault()
        const formData = new FormData(loginForm)
        const data = Object.fromEntries(formData.entries())
        doSubmit(data)
    })
}

function doSubmit(request) {
    const params = new URLSearchParams(window.location.search);
    const redirectTo = params.get("redirect") || "/";
    if(mode) {
        fetch("/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(request)
        })
        .then(res => {
            if(res.status === 201)
                return res.json()
        })
        .then((user) => window.location.href = redirectTo)
    }
    else {
    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
        .then(res => res.text())
        .then(() => {
            window.location.href = redirectTo
        })
        .catch(() => alert("Login failed"))
    }
}

function showError(errorMessage, inputElement) {
    const errorElement = inputElement.closest(".field").getElementsByClassName("error")[0]
    errorElement.textContent = errorMessage
    errorElement.classList.add("show")
}