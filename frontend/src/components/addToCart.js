export default async function addToCart(productId, showToast) {

    const res = await fetch("http://localhost:8080/api/cart/items", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId, quantity: 1 })
    })
    if (res.ok) {
        showToast("Product added to cart")
    }
    else if (res.status === 403) {
        setTokenExpired(true)
    }
}